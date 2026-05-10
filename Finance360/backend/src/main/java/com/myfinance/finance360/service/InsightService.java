package com.myfinance.finance360.service;

import com.myfinance.finance360.model.Goal;
import com.myfinance.finance360.model.Transaction;
import com.myfinance.finance360.model.TransactionType;
import com.myfinance.finance360.model.User;
import com.myfinance.finance360.repository.GoalRepository;
import com.myfinance.finance360.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private GoalRepository goalRepository;

    // ── Monthly report for this user ──────────────────────────────────────
    public Map<String, Object> getMonthlyReport(User user, int year, int month) {
        if (month < 1 || month > 12) {
            return Collections.singletonMap("message",
                    "Invalid month. Please provide a month between 1 and 12.");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay  = yearMonth.atDay(1);
        LocalDate lastDay   = yearMonth.atEndOfMonth();

        // Only this user's transactions in the given month
        List<Transaction> monthlyTx =
                transactionRepository.findByUserAndDateBetween(user, firstDay, lastDay);

        double income  = sum(monthlyTx, TransactionType.INCOME);
        double expense = sum(monthlyTx, TransactionType.EXPENSE);
        double savings = income - expense;

        String topCategory = monthlyTx.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ))
                .entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // Goals for this user
        List<String> goalsList = goalRepository.findByUser(user).stream()
                .map(g -> g.getName() + " — ₹" + String.format("%.2f", g.getTargetAmount()))
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("income",      income);
        report.put("expense",     expense);
        report.put("savings",     savings);
        report.put("topCategory", topCategory);
        report.put("goals",       goalsList);
        return report;
    }

    // ── General financial insights for this user ──────────────────────────
    public List<String> getFinancialInsights(User user) {
        List<String> insights = new ArrayList<>();
        List<Transaction> all = transactionRepository.findByUser(user);

        if (all.isEmpty()) {
            insights.add("No transactions found. Start by adding your income and expenses.");
            return insights;
        }

        double income  = sum(all, TransactionType.INCOME);
        double expense = sum(all, TransactionType.EXPENSE);
        double balance = income - expense;

        // Balance status
        if (balance > 0) {
            insights.add("✓ You have a positive balance of ₹" + fmt(balance));
        } else if (balance < 0) {
            insights.add("✗ You have a negative balance of ₹" + fmt(Math.abs(balance))
                    + ". Review your expenses.");
        } else {
            insights.add("Your income and expenses are exactly balanced.");
        }

        // Savings rate
        if (income > 0) {
            double rate = (balance / income) * 100;
            insights.add("Savings rate: " + String.format("%.1f%%", rate)
                    + (rate < 20 ? " — try to save at least 20%." : " — great job!"));
        }

        // Top expense category
        List<Transaction> expenses = all.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .toList();

        if (!expenses.isEmpty()) {
            expenses.stream()
                    .collect(Collectors.groupingBy(
                            Transaction::getCategory,
                            Collectors.summingDouble(Transaction::getAmount)
                    ))
                    .entrySet().stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                    .ifPresent(e -> insights.add(
                            "Top spending category: " + e.getKey()
                                    + " (₹" + fmt(e.getValue()) + ")"
                    ));
        }

        // Top income category
        all.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ))
                .entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .ifPresent(e -> insights.add(
                        "Top income source: " + e.getKey()
                                + " (₹" + fmt(e.getValue()) + ")"
                ));

        // Goal progress
        List<Goal> goals = goalRepository.findByUser(user);
        if (!goals.isEmpty()) {
            goals.forEach(g -> {
                double progress = income > 0 ? Math.min((balance / g.getTargetAmount()) * 100, 100) : 0;
                insights.add("Goal \"" + g.getName() + "\": "
                        + String.format("%.1f", progress) + "% complete"
                        + (progress >= 100 ? " ✓ Achieved!" : ""));
            });
        }

        return insights;
    }

    // ── Private helpers ───────────────────────────────────────────────────
    private double sum(List<Transaction> list, TransactionType type) {
        return list.stream()
                .filter(t -> t.getType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private String fmt(double value) {
        return String.format("%.2f", value);
    }
}
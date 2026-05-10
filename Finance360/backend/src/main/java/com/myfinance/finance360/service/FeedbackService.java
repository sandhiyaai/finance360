package com.myfinance.finance360.service;

import com.myfinance.finance360.model.Transaction;
import com.myfinance.finance360.model.TransactionType;
import com.myfinance.finance360.repository.TransactionRepository;
import com.myfinance.finance360.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class FeedbackService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private GoalRepository goalRepository;

    // =========================
    // 🧠 GENERATE INSIGHTS
    // =========================
    public List<String> generateInsights() {

        List<String> insights = new ArrayList<>();
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            insights.add("ℹ️ No transactions available to analyze.");
            return insights;
        }

        LocalDate today = LocalDate.now();

        // =========================
        // 📊 Weekly Spending Analysis
        // =========================
        LocalDate lastWeek = today.minusDays(7);
        LocalDate twoWeeksAgo = today.minusDays(14);

        double thisWeekExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE &&
                        !t.getDate().isBefore(lastWeek))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double lastWeekExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE &&
                        t.getDate().isBefore(lastWeek) &&
                        !t.getDate().isBefore(twoWeeksAgo))
                .mapToDouble(Transaction::getAmount)
                .sum();

        if (thisWeekExpense > lastWeekExpense) {
            insights.add("⚠️ Your spending increased this week. Try to reduce expenses.");
        } else {
            insights.add("✅ Good job! You reduced your spending this week.");
        }

        // =========================
        // 🍔 Category Analysis
        // =========================
        Map<String, Double> categoryMap = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                categoryMap.put(
                        t.getCategory(),
                        categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount()
                );
            }
        }

        if (!categoryMap.isEmpty()) {
            String maxCategory = Collections.max(
                    categoryMap.entrySet(),
                    Map.Entry.comparingByValue()
            ).getKey();

            insights.add("💡 You are spending most on: " + maxCategory);
        }

        // =========================
        // 💰 Savings Analysis
        // =========================
        double income = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double savings = income - expense;

        if (savings < 0) {
            insights.add("❌ You are overspending! Expenses exceed income.");
        } else if (income > 0 && savings < income * 0.2) {
            insights.add("⚠️ Your savings are low. Try saving at least 20%.");
        } else {
            insights.add("✅ Great! You are maintaining good savings.");
        }

        // =========================
        // 🎯 Goal Tracking (SMART)
        // =========================
        goalRepository.findAll().forEach(goal -> {

            double target = goal.getTargetAmount();
            LocalDate deadline = goal.getDeadline();

            long daysLeft = ChronoUnit.DAYS.between(today, deadline);
            double progress = (savings / target) * 100;

            if (savings >= target) {
                insights.add("🎯 Goal achieved: " + goal.getName());
            }
            else if (daysLeft <= 0) {
                insights.add("⚠️ Deadline missed for goal: " + goal.getName());
            }
            else {
                double remaining = target - savings;
                double perDay = remaining / daysLeft;

                insights.add("📊 " + goal.getName() + ": "
                        + String.format("%.2f", progress) + "% completed");

                insights.add("💰 Save ₹" + String.format("%.2f", perDay)
                        + " per day to reach " + goal.getName());

                if (progress < 50) {
                    insights.add("⚠️ You are behind schedule for " + goal.getName());
                } else {
                    insights.add("🔥 Good progress on " + goal.getName());
                }
            }
        });

        return insights;
    }

    // =========================
    // 📊 MONTHLY REPORT
    // =========================
    public Map<String, Object> getMonthlyReport(int year, int month) {

        Map<String, Object> report = new HashMap<>();

        List<Transaction> transactions = transactionRepository.findAll();

        List<Transaction> monthlyTransactions = transactions.stream()
                .filter(t -> t.getDate().getYear() == year &&
                        t.getDate().getMonthValue() == month)
                .toList();

        if (monthlyTransactions.isEmpty()) {
            report.put("message", "No data for this month");
            return report;
        }

        double income = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double savings = income - expense;

        // 🍔 Top Category
        Map<String, Double> categoryMap = new HashMap<>();

        for (Transaction t : monthlyTransactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                categoryMap.put(
                        t.getCategory(),
                        categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount()
                );
            }
        }

        String topCategory = "N/A";

        if (!categoryMap.isEmpty()) {
            topCategory = Collections.max(
                    categoryMap.entrySet(),
                    Map.Entry.comparingByValue()
            ).getKey();
        }

        // 🎯 Goal Tracking (Monthly)
        List<String> goalInsights = new ArrayList<>();
        LocalDate today = LocalDate.now();

        goalRepository.findAll().forEach(goal -> {

            double target = goal.getTargetAmount();
            long daysLeft = ChronoUnit.DAYS.between(today, goal.getDeadline());

            if (savings >= target) {
                goalInsights.add("🎯 Goal achieved: " + goal.getName());
            }
            else if (daysLeft <= 0) {
                goalInsights.add("⚠️ Deadline passed for goal: " + goal.getName());
            }
            else {
                double remaining = target - savings;
                double perDay = remaining / daysLeft;

                goalInsights.add("📊 Save ₹" + String.format("%.2f", perDay)
                        + "/day for goal: " + goal.getName());
            }
        });

        report.put("income", income);
        report.put("expense", expense);
        report.put("savings", savings);
        report.put("topCategory", topCategory);
        report.put("goals", goalInsights);

        return report;
    }
}
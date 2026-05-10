package com.myfinance.finance360.service;

import com.myfinance.finance360.model.Transaction;
import com.myfinance.finance360.model.TransactionType;
import com.myfinance.finance360.model.User;
import com.myfinance.finance360.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    // ── Save new transaction (user already set in controller) ─────────────
    public Transaction saveTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    // ── Get all transactions for this user only ───────────────────────────
    public List<Transaction> getAllTransactions(User user) {
        return repository.findByUser(user);
    }

    // ── Filter by type for this user ──────────────────────────────────────
    public List<Transaction> getByType(User user, TransactionType type) {
        return repository.findByUserAndType(user,type);
    }

    // ── Filter by date range for this user ────────────────────────────────
    public List<Transaction> getByDate(User user, LocalDate start, LocalDate end) {
        return repository.findByUserAndDateBetween(user, start, end);
    }

    // ── Income / expense / balance summary for this user ─────────────────
    public Map<String, Double> getSummary(User user) {
        List<Transaction> transactions = repository.findByUser(user);

        double income = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        Map<String, Double> summary = new HashMap<>();
        summary.put("income",  income);
        summary.put("expense", expense);
        summary.put("balance", income - expense);
        return summary;
    }

    // ── Category-wise totals for this user ────────────────────────────────
    public Map<String, Double> getCategorySummary(User user) {
        return repository.findByUser(user).stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    // ── Delete — only if this transaction belongs to this user ────────────
    public void deleteTransaction(User user, Long id) {
        Transaction transaction = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NoSuchElementException(
                        "Transaction not found or does not belong to you"));
        repository.delete(transaction);
    }

    // ── Update — only if this transaction belongs to this user ────────────
    public Transaction updateTransaction(User user, Long id, Transaction updated) {
        Transaction existing = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NoSuchElementException(
                        "Transaction not found or does not belong to you"));

        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setType(updated.getType());
        existing.setDate(updated.getDate());
        existing.setNote(updated.getNote());
        existing.setFrequency(updated.getFrequency());
        // user is NOT updated — it stays the same owner

        return repository.save(existing);
    }
}
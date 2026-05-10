package com.myfinance.finance360.service;

import com.myfinance.finance360.model.Transaction;
import com.myfinance.finance360.model.Frequency;
import com.myfinance.finance360.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class RecurringTransactionService {

    @Autowired
    private TransactionRepository repository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateRecurringTransactions() {

        List<Transaction> templates = repository.findByIsTemplateTrue();
        for (Transaction t : templates) {

            if (t.getFrequency() == Frequency.DAILY) {
                createDaily(t);
            }

            if (t.getFrequency() == Frequency.WEEKLY &&
                    t.getDate().getDayOfWeek() == LocalDate.now().getDayOfWeek()) {
                createWeekly(t);
            }

            if (t.getFrequency() == Frequency.MONTHLY &&
                    t.getDate().getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
                createMonthly(t);
            }
        }
    }

    private void createMonthlyTransaction(Transaction t) {

        LocalDate today = LocalDate.now();

        boolean exists = repository.findAll().stream()
                .anyMatch(tx ->
                        tx.getCategory().equals(t.getCategory()) &&
                                tx.getDate().getMonth() == today.getMonth() &&
                                tx.getDate().getYear() == today.getYear() &&
                                tx.getAmount().equals(t.getAmount())
                );

        if (exists) return;

        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(t.getAmount());
        newTransaction.setCategory(t.getCategory());
        newTransaction.setType(t.getType());
        newTransaction.setDate(today);
        newTransaction.setNote(t.getNote());
        newTransaction.setFrequency(t.getFrequency());

        repository.save(newTransaction);
    }
    private void createNewTransaction(Transaction t) {

        LocalDate today = LocalDate.now();

        boolean exists = repository.findAll().stream()
                .anyMatch(tx ->
                        tx.getCategory().equals(t.getCategory()) &&
                                tx.getDate().equals(today) &&
                                tx.getAmount().equals(t.getAmount())
                );

        if (exists) return;

        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(t.getAmount());
        newTransaction.setCategory(t.getCategory());
        newTransaction.setType(t.getType());
        newTransaction.setDate(today);
        newTransaction.setNote(t.getNote());
        newTransaction.setFrequency(t.getFrequency());

        repository.save(newTransaction);
    }
    private void createDaily(Transaction t) {
        createIfNotExists(t, LocalDate.now());
    }
    private void createWeekly(Transaction t) {
        createIfNotExists(t, LocalDate.now());
    }
    private void createMonthly(Transaction t) {
        createIfNotExists(t, LocalDate.now());
    }
    private void createIfNotExists(Transaction t, LocalDate date) {

        boolean exists = repository.findAll().stream()
                .anyMatch(tx ->
                        !tx.isTemplate() &&
                                tx.getCategory().equals(t.getCategory()) &&
                                tx.getAmount().equals(t.getAmount()) &&
                                tx.getDate().getMonth() == date.getMonth() &&
                                tx.getDate().getYear() == date.getYear()
                );

        if (exists) return;

        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(t.getAmount());
        newTransaction.setCategory(t.getCategory());
        newTransaction.setType(t.getType());
        newTransaction.setDate(date);
        newTransaction.setNote(t.getNote());
        newTransaction.setFrequency(t.getFrequency());
        newTransaction.setTemplate(false); // 🔥 IMPORTANT

        repository.save(newTransaction);
    }
}
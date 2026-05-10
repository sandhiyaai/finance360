package com.myfinance.finance360.repository;

import com.myfinance.finance360.model.Transaction;
import com.myfinance.finance360.model.TransactionType;
import com.myfinance.finance360.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // All transactions for a user
    List<Transaction> findByUser(User user);

    // Filter by type for a user
    List<Transaction> findByUserAndType(User user, TransactionType type);

    // Filter by date range for a user
    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    // Recurring templates
    List<Transaction> findByUserAndIsTemplateTrue(User user);

    // All templates
    List<Transaction> findByIsTemplateTrue();

    // Find by id AND user
    Optional<Transaction> findByIdAndUser(Long id, User user);
}
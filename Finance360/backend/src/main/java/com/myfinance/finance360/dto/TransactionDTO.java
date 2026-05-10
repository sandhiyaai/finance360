package com.myfinance.finance360.dto;

import com.myfinance.finance360.model.Frequency;
import com.myfinance.finance360.model.Transaction;
import com.myfinance.finance360.model.TransactionType;

import java.time.LocalDate;

/**
 * What the frontend receives — no User object, no circular reference.
 * Constructed directly from a Transaction entity.
 */
public record TransactionDTO(
        Long id,
        Double amount,
        TransactionType type,
        String category,
        LocalDate date,
        String note,
        Frequency frequency,
        boolean isTemplate
) {
    /** Convert entity → DTO in one place */
    public static TransactionDTO from(Transaction t) {
        return new TransactionDTO(
                t.getId(),
                t.getAmount(),
                t.getType(),
                t.getCategory(),
                t.getDate(),
                t.getNote(),
                t.getFrequency(),
                t.isTemplate()
        );
    }
}
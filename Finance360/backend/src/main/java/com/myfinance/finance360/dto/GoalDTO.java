package com.myfinance.finance360.dto;

import com.myfinance.finance360.model.Goal;

import java.time.LocalDate;

/**
 * What the frontend receives — no User object, no circular reference.
 */
public record GoalDTO(
        Long id,
        String name,
        Double targetAmount,
        LocalDate deadline
) {
    public static GoalDTO from(Goal g) {
        return new GoalDTO(
                g.getId(),
                g.getName(),
                g.getTargetAmount(),
                g.getDeadline()
        );
    }
}
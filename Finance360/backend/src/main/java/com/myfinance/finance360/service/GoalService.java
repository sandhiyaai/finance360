package com.myfinance.finance360.service;

import com.myfinance.finance360.model.Goal;
import com.myfinance.finance360.model.User;
import com.myfinance.finance360.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GoalService {

    @Autowired
    private GoalRepository repository;

    // ── Create goal (user already set in controller) ──────────────────────
    public Goal createGoal(Goal goal) {
        return repository.save(goal);
    }

    // ── Get all goals for this user only ──────────────────────────────────
    public List<Goal> getAllGoals(User user) {
        return repository.findByUser(user);
    }

    // ── Delete goal — only if it belongs to this user ─────────────────────
    public void deleteGoal(User user, Long id) {
        Goal goal = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NoSuchElementException(
                        "Goal not found or does not belong to you"));
        repository.delete(goal);
    }
}
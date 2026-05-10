package com.myfinance.finance360.repository;

import com.myfinance.finance360.model.Goal;
import com.myfinance.finance360.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // All goals for a user
    List<Goal> findByUser(User user);

    // Find by id AND user (security check)
    Optional<Goal> findByIdAndUser(Long id, User user);
}
package com.myfinance.finance360.controller;

import com.myfinance.finance360.dto.GoalDTO;
import com.myfinance.finance360.model.Goal;
import com.myfinance.finance360.model.User;
import com.myfinance.finance360.repository.UserRepository;
import com.myfinance.finance360.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired private GoalService service;
    @Autowired private UserRepository userRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public GoalDTO createGoal(@RequestBody Goal goal, Authentication auth) {
        goal.setUser(getCurrentUser(auth));
        return GoalDTO.from(service.createGoal(goal));
    }

    @GetMapping
    public List<GoalDTO> getGoals(Authentication auth) {
        return service.getAllGoals(getCurrentUser(auth))
                .stream().map(GoalDTO::from).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id, Authentication auth) {
        service.deleteGoal(getCurrentUser(auth), id);
        return ResponseEntity.noContent().build();
    }
}
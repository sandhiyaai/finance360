package com.myfinance.finance360.controller;

import com.myfinance.finance360.model.User;
import com.myfinance.finance360.repository.UserRepository;
import com.myfinance.finance360.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insights")
public class InsightController {

    @Autowired
    private InsightService insightService;

    @Autowired
    private UserRepository userRepository;

    // ── Helper ────────────────────────────────────────────────────────────
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // ── GET /insights ─────────────────────────────────────────────────────
    @GetMapping
    public List<String> getInsights(Authentication authentication) {
        return insightService.getFinancialInsights(getCurrentUser(authentication));
    }

    // ── GET /insights/monthly?year=2025&month=4 ───────────────────────────
    @GetMapping("/monthly")
    public Map<String, Object> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        return insightService.getMonthlyReport(getCurrentUser(authentication), year, month);
    }
}
package com.myfinance.finance360.controller;

import com.myfinance.finance360.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
@CrossOrigin("*")
public class FeedbackController {

    @Autowired
    private FeedbackService service;
    @GetMapping
    public List<String> getInsights() {
        return service.generateInsights();
    }
    @GetMapping("/monthly")
    public Map<String, Object> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {

        return service.getMonthlyReport(year, month);
    }
}
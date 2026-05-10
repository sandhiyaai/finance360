package com.myfinance.finance360.controller;

import com.myfinance.finance360.dto.TransactionDTO;
import com.myfinance.finance360.model.Transaction;
import com.myfinance.finance360.model.TransactionType;
import com.myfinance.finance360.model.User;
import com.myfinance.finance360.repository.UserRepository;
import com.myfinance.finance360.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired private TransactionService service;
    @Autowired private UserRepository userRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public TransactionDTO addTransaction(@RequestBody Transaction transaction, Authentication auth) {
        transaction.setUser(getCurrentUser(auth));
        return TransactionDTO.from(service.saveTransaction(transaction));
    }

    @GetMapping
    public List<TransactionDTO> getAllTransactions(Authentication auth) {
        return service.getAllTransactions(getCurrentUser(auth))
                .stream().map(TransactionDTO::from).toList();
    }

    @GetMapping("/type")
    public List<TransactionDTO> getByType(@RequestParam TransactionType type, Authentication auth) {
        return service.getByType(getCurrentUser(auth), type)
                .stream().map(TransactionDTO::from).toList();
    }

    @GetMapping("/summary")
    public Map<String, Double> getSummary(Authentication auth) {
        return service.getSummary(getCurrentUser(auth));
    }

    @GetMapping("/date")
    public List<TransactionDTO> getByDate(
            @RequestParam LocalDate start, @RequestParam LocalDate end, Authentication auth) {
        return service.getByDate(getCurrentUser(auth), start, end)
                .stream().map(TransactionDTO::from).toList();
    }

    @GetMapping("/category-summary")
    public Map<String, Double> getCategorySummary(Authentication auth) {
        return service.getCategorySummary(getCurrentUser(auth));
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id, Authentication auth) {
        service.deleteTransaction(getCurrentUser(auth), id);
    }

    @PutMapping("/{id}")
    public TransactionDTO updateTransaction(
            @PathVariable Long id, @RequestBody Transaction updated, Authentication auth) {
        return TransactionDTO.from(service.updateTransaction(getCurrentUser(auth), id, updated));
    }
}
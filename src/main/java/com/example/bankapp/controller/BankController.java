package com.example.bankapp.controller;

import com.example.bankapp.model.Account;
import com.example.bankapp.repo.AccountRepository;
import com.example.bankapp.service.AcoountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@Controller
public class BankController {
    @Autowired
    AcoountServiceImpl accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/dashboard")
    public String getDashBoard(Model model) {
       String username = SecurityContextHolder.getContext().getAuthentication().getName();
       Account account = accountService.findAccountByUsername(username);
       model.addAttribute("account", account);
        return "dashboard";
    }

    @GetMapping("/register")
    public String registerAccount(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public void saveUsers(@ModelAttribute Account account) {
        account.setPassword(encoder.encode(account.getPassword()));
        accountRepository.save(account);
    }

    @GetMapping("/login")
    public String loginAccount(Model model) {
        return "login";
    }

    @PostMapping("/deposit")
    public String depositAmount(@RequestParam BigDecimal amount){
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentaccount=accountService.findAccountByUsername(username);
        accountService.deposit(currentaccount,amount);
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdrawAmount(@RequestParam BigDecimal amount){
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentaccount=accountService.findAccountByUsername(username);
        accountService.withdraw(currentaccount,amount);
        return "redirect:/dashboard";
    }

    @PostMapping("/transfer")
    public String transferAmount(@RequestParam String toUsername,@RequestParam BigDecimal amount,Model model) {
        String fromAccountUsername=SecurityContextHolder.getContext().getAuthentication().getName();
        Account fromAccount=accountService.findAccountByUsername(fromAccountUsername);
        accountService.transferAmount(fromAccount,toUsername,amount);
        return "redirect:/dashboard";
    }

    @GetMapping("/transactions")
    public String transactionHistory(Model model) {
        String accountUser=SecurityContextHolder.getContext().getAuthentication().getName();
        Account account=accountService.findAccountByUsername(accountUser);
        model.addAttribute("transactions",accountService.getTransactionHistory(account));
        return "transactions";
    }

}
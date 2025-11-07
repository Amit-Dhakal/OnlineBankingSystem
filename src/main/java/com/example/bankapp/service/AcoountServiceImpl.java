package com.example.bankapp.service;

import com.example.bankapp.model.Account;
import com.example.bankapp.model.Transaction;
import com.example.bankapp.repo.AccountRepository;
import com.example.bankapp.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Service
public class AcoountServiceImpl implements UserDetailsService {

//    @Autowired
//    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public Account findAccountByUsername(String username) {
//        return accountRepository.findByUsername(username).get();
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with username: " + username));
    }

    public Account registerAccount(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username Already Exists");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);
        return account;
    }


    public void deposit(Account account,BigDecimal amount){
        account.setBalance(account.getBalance().add(amount));
        Transaction transaction=new Transaction(amount,"Deposit", LocalDateTime.now(),account);
        transactionRepository.save(transaction);
    }


    public void withdraw(Account account,BigDecimal amount){
        if(account.getBalance().compareTo(amount)<0){
        throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account=findAccountByUsername(username);
        Collection<SimpleGrantedAuthority> grantedAuthorityList= List.of(new SimpleGrantedAuthority(account.getRoles()));

       return new User(account.getUsername(),account.getPassword(),grantedAuthorityList);
       //return new Account(account.getPassword(),account.getUsername(),account.getBalance(),account.getTransactions(),authorities());
    }



//    public Collection<? extends GrantedAuthority> authorities(){
//     return Arrays.asList(new SimpleGrantedAuthority("User"));
//    }

    public void transferAmount(Account fromAccount,String toUsername,BigDecimal amount){

        Account toUserAccount=findAccountByUsername(toUsername);
        //Deduct from ACCOUNT
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);
        //Add to ACCOUNT
        toUserAccount.setBalance(toUserAccount.getBalance().add(amount));
        accountRepository.save(toUserAccount);

        }


        public List<Transaction> getTransactionHistory(Account account){
        return transactionRepository.findByAccountId(account.getId());
        }

}

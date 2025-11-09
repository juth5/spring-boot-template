package study.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import study.mapper.AccountMapper;
import study.model.Account;

@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountMapper accountMapper, PasswordEncoder passwordEncoder) {
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void createAccount(String username, String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        Account acc = new Account();
        acc.setUsername(username);
        acc.setPassword(encoded);
        acc.setRole("USER");

        accountMapper.insertAccount(acc);
    }

}

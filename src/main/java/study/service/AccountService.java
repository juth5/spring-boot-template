package study.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import study.dto.response.LoginResponse;
import study.mapper.AccountMapper;
import study.model.Account;
import study.security.JwtUtil;

@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AccountService(AccountMapper accountMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void createAccount(String username, String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        Account acc = new Account();
        acc.setUsername(username);
        acc.setPassword(encoded);
        acc.setRole("USER");

        accountMapper.insertAccount(acc);
    }

    public LoginResponse logIn(String username, String rawPassword) {
        // ✅ ① DBからユーザー取得
        Account user = accountMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("ユーザーが存在しません");
        }
        // ✅ ② パスワード照合
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("パスワードが違います");
        }
        // ✅ ③ JWT発行
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        // ✅ ④ JSONで返す
        return new LoginResponse(token, user.getUsername());
    }

    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        accounts = accountMapper.getAccounts();
        return accounts;
    };

    public Account findByUserId(Long userId) {
        Account account = accountMapper.findByUserId(userId);
        return account;
    }
}




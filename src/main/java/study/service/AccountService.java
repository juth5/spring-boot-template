package study.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import study.dto.response.AccountResponse;
import study.dto.response.LoginResponse;
import study.exception.DuplicateUsernameException;
import study.exception.InvalidCredentialsException;
import study.exception.UserNotFoundException;
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
        if (accountMapper.findByUsername(username) != null) {
            throw new DuplicateUsernameException(username);
        }
        Account acc = new Account();
        acc.setUsername(username);
        acc.setPassword(passwordEncoder.encode(rawPassword));
        acc.setRole("USER");
        accountMapper.insertAccount(acc);
    }

    public LoginResponse logIn(String username, String rawPassword) {
        Account user = accountMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername());
    }

    public List<AccountResponse> getAccounts() {
        return accountMapper.getAccounts().stream()
                .map(a -> new AccountResponse(a.getId(), a.getUsername(), a.getRole(), a.getCreatedAt()))
                .toList();
    }

    public AccountResponse findByUserId(Long userId) {
        Account a = accountMapper.findByUserId(userId);
        if (a == null) {
            throw new UserNotFoundException("id=" + userId);
        }
        return new AccountResponse(a.getId(), a.getUsername(), a.getRole(), a.getCreatedAt());
    }
}

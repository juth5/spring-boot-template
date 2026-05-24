package study.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import study.dto.request.AccountCreateRequest;
import study.dto.request.LoginRequest;
import study.dto.response.AccountResponse;
import study.dto.response.LoginResponse;
import study.security.UserPrincipal;
import study.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class ApiAccountRestController {

    private final AccountService accountService;

    public ApiAccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public String accountCreate(@Valid @RequestBody AccountCreateRequest request) {
        accountService.createAccount(request.getUsername(), request.getPassword());
        return "created";
    }

    @PostMapping("/logIn")
    public LoginResponse logIn(@Valid @RequestBody LoginRequest request) {
        return accountService.logIn(request.getUsername(), request.getPassword());
    }

    @GetMapping("/list")
    public List<AccountResponse> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/me")
    public AccountResponse me(@AuthenticationPrincipal UserPrincipal user) {
        return accountService.findByUserId(user.getUserId());
    }
}

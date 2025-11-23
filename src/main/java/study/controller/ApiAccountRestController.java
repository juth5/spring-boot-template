package study.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.dto.request.AccountCreateRequest;
import study.dto.response.LoginResponse;
import study.model.Account;
import study.model.QaLog;
import study.security.UserPrincipal;
import study.service.AccountService;
import study.service.QaLogService;

@RestController
@RequestMapping("/api/account")
public class ApiAccountRestController {

  private final AccountService accountService;
  private final QaLogService qaLogService;

  public ApiAccountRestController(AccountService accountService, QaLogService qaLogService) {
      this.accountService = accountService;
      this.qaLogService = qaLogService;

  }

  @PostMapping("/create")
  public String accountCreate(@RequestBody AccountCreateRequest request) {
    accountService.createAccount(request.getUsername(), request.getPassword());
    return "created";
  }

  @PostMapping("/logIn")
  public LoginResponse logIn(@RequestBody AccountCreateRequest request) {
    LoginResponse response = accountService.logIn(request.getUsername(), request.getPassword());
    return response;
  }

  @GetMapping("/list")
  public List<Account> getAccounts() {
    List<Account> accounts = accountService.getAccounts();
    return accounts;
  }

  @GetMapping("/me")
  public Account me(@AuthenticationPrincipal UserPrincipal user) {
    Account account = accountService.findByUserId(user.getUserId());
    return account;
  }
}

package study.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.dto.request.AccountCreateRequest;
import study.dto.response.LoginResponse;
import study.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class ApiAccountRestController {

  private final AccountService accountService;

  public ApiAccountRestController(AccountService accountService) {
      this.accountService = accountService;
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



  
}

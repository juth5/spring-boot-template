package study.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.model.QaLog;
import study.security.UserPrincipal;
import study.service.QaLogService;

@RestController
@RequestMapping("/api/my")
public class ApiMyPageRestController {
  private final QaLogService qaLogService;

  public ApiMyPageRestController(QaLogService qaLogService) {
      this.qaLogService = qaLogService;
  }

  @GetMapping("/qa")
  public List<QaLog> getMyQa(@AuthenticationPrincipal UserPrincipal user) {
    List<QaLog> logs = qaLogService.findByUserId(user.getUserId());
    return logs;
  }
}

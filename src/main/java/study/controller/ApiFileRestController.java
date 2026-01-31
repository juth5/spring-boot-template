package study.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.dto.request.InspectionFileRequest;
import study.dto.request.MaintenanceFileRequest;
import study.service.FileService;

@RestController
@RequestMapping("/api/file")
public class ApiFileRestController {
  private final FileService fileService;

  public ApiFileRestController(FileService fileService) {
    this.fileService = fileService;
  }

  @PostMapping("/inspection")
  public int postInspectionFile(@RequestBody InspectionFileRequest inspectionFileRequest) {
    return fileService.insertFile(inspectionFileRequest);
  }
  @PostMapping("/maintenance")
  public int postMaintenanceFile(@RequestBody MaintenanceFileRequest maintenanceFileRequest) {
    return fileService.insertFile(maintenanceFileRequest);
  }
}

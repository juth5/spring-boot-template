package study.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.dto.response.ServiceResponse;
import study.model.ChatRequestParameter;
import study.security.UserPrincipal;
import study.service.ChatGptService;

@RestController
@RequestMapping("/api/chatgpt")
public class ApiChatGptRestController {

    private final ChatGptService chatGptService;

    public ApiChatGptRestController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @PostMapping
    public ServiceResponse<Map<String, String>> callChatGpt(
            @RequestBody ChatRequestParameter param,
            @AuthenticationPrincipal UserPrincipal user) {

        String response = chatGptService.callChatGpt(param, user);

        if (response == null) {
            return new ServiceResponse<>(false, "失敗しました。", null);
        }
        return new ServiceResponse<>(true, "回答を生成しました。", Map.of("answer", response));
    }

    @PostMapping("/mermaid")
    public ServiceResponse<Map<String, String>> callChatGptMermaid(
            @RequestBody ChatRequestParameter param,
            @AuthenticationPrincipal UserPrincipal user) {

        String response = chatGptService.callChatGptMermaid(param);

        if (response == null) {
            return new ServiceResponse<>(false, "失敗しました。", null);
        }
        return new ServiceResponse<>(true, "回答を生成しました。", Map.of("answer", response));
    }
}

package study.controller;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import study.model.ChatRequestParameter;
import study.model.ServiceResponse;
import study.security.UserPrincipal;
import study.service.ChatGptService;

@RestController
@RequestMapping("/api/chatgpt")
public class ApiChatGptRestController {
    @Autowired
    private ChatGptService chatGptService;
    @PostMapping
    public ServiceResponse<Map<String, String>> callChatGpt(@RequestBody ChatRequestParameter param, @AuthenticationPrincipal UserPrincipal user) {


        ServiceResponse<Map<String, String>> serviceResponse = new ServiceResponse<>();
        String response = chatGptService.callChatGpt(param, user);

        if (response == null) {
            serviceResponse.setSuccess(false);
            serviceResponse.setMessage("失敗しました。");
        } else {
            Map<String, String> data = new HashMap<>();
            data.put("answer", response);
            serviceResponse.setSuccess(true);
            serviceResponse.setMessage("回答を生成しました。");
            serviceResponse.setData(data);
        }
        return serviceResponse;
    }
}


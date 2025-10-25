package study.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import study.mapper.ApiCallLogMapper;
import study.model.ApiCallLog;
import study.model.ChatRequestParameter;
import study.service.ChatGptService;

import java.util.List;

@RestController
@RequestMapping("/api/chatgpt")
public class ApiChatGptRestController {

    @Autowired
    private ApiCallLogMapper apiCallLogMapper;

    @Autowired
    private ChatGptService chatGptService;


    @PostMapping
    public void callChatGpt(@RequestBody ChatRequestParameter param) {

        System.out.println("呼ばれた");
                System.out.println(param);

        chatGptService.callChatGpt();
    }


    // 今日の呼び出し回数を取得
    @GetMapping("/count/today")
    public Integer countToday() {
        return apiCallLogMapper.countToday();
    }
}


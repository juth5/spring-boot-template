package study.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import study.mapper.ApiCallLogMapper;
import study.model.ChatRequestParameter;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ChatGptService {
    private static final String API_URL = "https://api.openai.com/v1";

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private final WebClient webClient;
    private final ApiCallLogMapper apiCallLogMapper;

    public ChatGptService(WebClient.Builder webClientBuilder, ApiCallLogMapper apiCallLogMapper) {
        this.webClient = webClientBuilder.baseUrl(API_URL).build();
        this.apiCallLogMapper = apiCallLogMapper;
    }

    public String callChatGpt(ChatRequestParameter param) {

        Integer count = apiCallLogMapper.countToday();

        if (count >= 10) {
            System.out.println("今日の呼び出し回数が上限に達しました。");
            return null;
        }
        String result = callApiChatGpt(param);
        apiCallLogMapper.insertCallLog();

        return result;
    }

    public String callApiChatGpt(ChatRequestParameter param) {
        try {
            // --- リクエストボディ生成 ---
            JSONObject message = new JSONObject()
                    .put("role", "user")
                    .put("content", param.getQuestion());

            JSONObject requestBody = new JSONObject()
                    .put("model", param.getModel())
                    .put("messages", new JSONArray().put(message));

            // --- WebClientでAPI呼び出し ---
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept-Encoding", "gzip, deflate")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 同期的に待機（非同期にしたいなら block() を外す）

            // --- レスポンス解析 ---
            JSONObject data = new JSONObject(response);
            return data.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "エラーが発生しました。";
        }
    }
        
}



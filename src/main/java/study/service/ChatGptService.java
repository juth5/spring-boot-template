package study.service;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import study.mapper.ApiCallLogMapper;
import study.mapper.ApiEmbeddingMapper;
import study.model.ChatRequestParameter;
import study.model.Embedding;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;

@Service
public class ChatGptService {
    private static final String API_URL = "https://api.openai.com/v1";

    @Value("${OPENAI_API_KEY}")
    private String apiKey;
    private final WebClient webClient;
    private final ApiCallLogMapper apiCallLogMapper;
    private final ApiEmbeddingMapper apiEmbeddingMapper;

    //コンストラクタ
    public ChatGptService(WebClient.Builder webClientBuilder, ApiCallLogMapper apiCallLogMapper, ApiEmbeddingMapper apiEmbeddingMapper) {
        HttpClient httpClient = HttpClient.create()
        .resolver(DefaultAddressResolverGroup.INSTANCE); // ← これが重要！

        this.webClient = WebClient.builder()
        .baseUrl(API_URL)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
        
        this.apiCallLogMapper = apiCallLogMapper;
        this.apiEmbeddingMapper = apiEmbeddingMapper;
    }

    //chatGPTのAPIを叩く
    public String callChatGpt(ChatRequestParameter param) {
        //今日のAPIのコール数を取得
        Integer count = apiCallLogMapper.countToday();
        if (count >= 10) {
            System.out.println("今日の呼び出し回数が上限に達しました。");
            return null;
        }
        //渡ってきた質問をベクターに変更する
        double[] vector = createEmbedding(param.getQuestion());

        //dbへベクトル化したものを書き込む
        // apiEmbeddingMapper.insertEmbedding(param.getQuestion(), vector);
        // return null;

        //vectorをもとにDBにアクセスして類似のcontentを取得する
        List<Embedding> embeddings = apiEmbeddingMapper.searchSimilar(vector, 3);

        double threshold = 0.5;

        StringBuilder sb = new StringBuilder();
        sb.append("【ユーザーの質問】\n");
        sb.append(param.getQuestion()).append("\n\n");
        sb.append("以下に示す内容だけを根拠に回答してください。内容に存在しない情報は推測せず、わからない場合は「データに記載がありません」と答えてください。\n\n");

        for (Embedding e : embeddings) {
            if (e.getDistance() <= threshold) {
                sb.append(e.getContent()).append("\n\n");
            }
        }

        String merged = sb.toString();
        param.setReferenceText(merged);
        String result = callApiChatGpt(param);
        apiCallLogMapper.insertCallLog();

        return result;
    }

    public String callApiChatGpt(ChatRequestParameter param) {
        try {
            JSONObject requestBody = new JSONObject()
                    .put("model", param.getModel())
                    .put("input", param.getReferenceText());
            // --- WebClientでAPI呼び出し ---
            String response = webClient.post()
                    .uri("/responses")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 同期的に待機（非同期にしたいなら block() を外す）

            // --- Debug Log ---
            System.out.println("===== RAW RESPONSE =====");
            System.out.println(response);
            System.out.println("========================");

            JSONObject data = new JSONObject(response);

            // --- Debug Log ---
            System.out.println("===== data =====");
            System.out.println(data);
            System.out.println("========================");

            JSONArray outputArray = data.getJSONArray("output");

            JSONObject messageOutput = null;
            for (int i = 0; i < outputArray.length(); i++) {
                JSONObject output = outputArray.getJSONObject(i);
                if ("message".equals(output.getString("type"))) {
                    messageOutput = output;
                    break;
                }
            }

            if (messageOutput == null) {
                throw new RuntimeException("レスポンスにmessageタイプが見つかりません");
            }

            JSONArray contentArray = messageOutput.getJSONArray("content");
            JSONObject firstContent = contentArray.getJSONObject(0);

            String answer = firstContent.getString("text");

            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return "エラーが発生しました。";
        }
    }

    //ベクトルを取得する
    public double[] createEmbedding(String text) {
        try {
            JSONObject requestBody = new JSONObject()
                    .put("model", "text-embedding-3-small")
                    .put("input", text);
            // --- Embedding API call ---
            String response = webClient.post()
                    .uri("/embeddings")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // --- parse embedding ---
            JSONObject json = new JSONObject(response);
            JSONArray arr = json.getJSONArray("data")
                    .getJSONObject(0)
                    .getJSONArray("embedding");
            double[] vector = new double[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                vector[i] = arr.getDouble(i);
            }
            return vector;
        } catch (Exception e) {
            throw new RuntimeException("Embedding生成中にエラーが発生しました", e);
        }
    }
}



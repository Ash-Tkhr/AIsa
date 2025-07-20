package com.aisa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIServiceImpl implements AIService {

    @Value("${ai.service.url:http://ai-service:5000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String processMessage(String message) {
        try {
            // AIサービスにリクエストを送信
            String url = aiServiceUrl + "/process";
            AIRequest request = new AIRequest(message);

            AIResponse response = restTemplate.postForObject(url, request, AIResponse.class);

            return response != null ? response.getResponse() : "AIサービスからの応答がありませんでした。";
        } catch (Exception e) {
            // AIサービスが利用できない場合のフォールバック
            return "AIサービスが一時的に利用できません。しばらく時間をおいて再度お試しください。";
        }
    }

    // 内部クラス（AIサービスとの通信用）
    private static class AIRequest {
        private String message;

        public AIRequest(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class AIResponse {
        private String response;

        public AIResponse() {
        }

        public String getResponse() {
            return response;
        }
    }
}
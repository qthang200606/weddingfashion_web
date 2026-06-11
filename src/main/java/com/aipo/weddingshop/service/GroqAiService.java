package com.aipo.weddingshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GroqAiService {

    // Ép Spring Boot đọc key, nếu trong properties không có thì dùng key thật bạn vừa cấu hình luôn
    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getAiResponse(String userPrompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 🌟 CHUẨN HÓA DANH SÁCH MESSAGES THEO ĐÚNG ĐỊNH DẠNG JSON MÀU ĐỒNG BỘ
            List<Map<String, String>> messagesList = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Bạn là Bella Assistant - trợ lý ảo thông minh của tiệm váy cưới cao cấp BRIDAL STUDIO. Hãy trả lời bằng tiếng Việt, ngắn gọn, ngọt ngào, xưng hô là 'Bella' và gọi khách hàng là bạn. Tập trung tư vấn váy cưới, vest nam và thúc giục đặt lịch hẹn thử váy.");
            messagesList.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);
            messagesList.add(userMessage);

            // Đóng gói Body Request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", messagesList);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gửi request đến Groq
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 🌟 BÓC TÁCH JSON TƯỜNG MINH ĐỂ TRÁNH LỖI CLASS CAST EXCEPTION
                List<?> choices = (List<?>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> messageMap = (Map<?, ?>) firstChoice.get("message");
                    if (messageMap != null) {
                        return messageMap.get("content").toString();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Groq Error: " + e.getMessage());

            return """
            Dạ Bella đang tạm thời bận một chút 🌸
            Nàng có thể để lại yêu cầu hoặc bấm
            'Gặp chuyên viên tư vấn' để Bella kết nối
            trực tiếp với nhân viên ạ.
            """;
        }
        return "Bella đã ghi nhận ý kiến của nàng ạ!";
    }
}
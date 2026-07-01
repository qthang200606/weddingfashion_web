package com.aipo.weddingshop.service;

import com.aipo.weddingshop.entity.Product;
import com.aipo.weddingshop.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroqAiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getAiResponse(String userPrompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            List<Map<String, Object>> messagesList = new ArrayList<>();
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");

            // 🌟 CẤU HÌNH PROMPT CHO AI: Dạy AI cách tự so sánh giá và cách chèn ảnh dạng Markdown
            systemMessage.put("content", "Bạn là Bella Assistant - trợ lý ảo thông minh ngọt ngào của BRIDAL STUDIO. " +
                    "Nhiệm vụ của bạn:\n" +
                    "1. Xưng 'Bella', gọi khách là 'bạn'. Trả lời bằng tiếng Việt.\n" +
                    "2. Sử dụng công cụ để lấy dữ liệu thực tế. Dựa vào danh sách nhận được, nếu khách hỏi chung về danh mục thì liệt kê sản phẩm kèm giá. Nếu khách hỏi sản phẩm 'đắt nhất' hoặc 'rẻ nhất', hãy tự so sánh giá tiền của các sản phẩm trong danh sách trả về để chọn ra sản phẩm đúng yêu cầu.\n" +
                    "3. KHI TRẢ LỜI VỀ SẢN PHẨM, BẮT BUỘC hiển thị hình ảnh bằng định dạng Markdown: ![Tên sản phẩm](URL hình ảnh). Ví dụ: ![Váy cưới Bella](http://...).");
            messagesList.add(systemMessage);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);
            messagesList.add(userMessage);

            List<Map<String, Object>> toolsList = getProductTools();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", messagesList);
            requestBody.put("tools", toolsList);
            requestBody.put("tool_choice", "auto");
            requestBody.put("temperature", 0.2); // Hạ thấp nhiệt độ để AI tính toán so sánh số liệu (giá cả) chính xác nhất

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<?> choices = (List<?>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> messageMap = (Map<?, ?>) firstChoice.get("message");

                    if (messageMap.get("tool_calls") != null) {
                        List<?> toolCalls = (List<?>) messageMap.get("tool_calls");
                        Map<?, ?> firstCall = (Map<?, ?>) toolCalls.get(0);
                        Map<?, ?> function = (Map<?, ?>) firstCall.get("function");
                        String functionName = function.get("name").toString();
                        String argumentsJson = function.get("arguments").toString();
                        String callId = firstCall.get("id").toString();

                        String databaseResult = executeProductSearch(functionName, argumentsJson);

                        messagesList.add((Map<String, Object>) messageMap);

                        Map<String, Object> toolResponseMessage = new HashMap<>();
                        toolResponseMessage.put("role", "tool");
                        toolResponseMessage.put("tool_call_id", callId);
                        toolResponseMessage.put("name", functionName);
                        toolResponseMessage.put("content", databaseResult);
                        messagesList.add(toolResponseMessage);

                        requestBody.put("messages", messagesList);
                        requestBody.remove("tools");

                        HttpEntity<Map<String, Object>> secondEntity = new HttpEntity<>(requestBody, headers);
                        ResponseEntity<Map> secondResponse = restTemplate.postForEntity(GROQ_URL, secondEntity, Map.class);

                        List<?> secondChoices = (List<?>) secondResponse.getBody().get("choices");
                        return ((Map<?, ?>)((Map<?, ?>) secondChoices.get(0)).get("message")).get("content").toString();
                    } else {
                        return messageMap.get("content").toString();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Bella AI Engine Error: " + e.getMessage());
            return "Dạ Bella đang gặp chút sự cố khi check kho váy, nàng đợi Bella một xíu nha 🌸";
        }
        return "Bella đã ghi nhận ý kiến của nàng ạ!";
    }

    private List<Map<String, Object>> getProductTools() {
        List<Map<String, Object>> tools = new ArrayList<>();

        // Tool 1: Tìm theo từ khóa
        Map<String, Object> tool1 = new HashMap<>();
        tool1.put("type", "function");
        Map<String, Object> function1 = new HashMap<>();
        function1.put("name", "search_products_by_keyword");
        function1.put("description", "Tìm kiếm sản phẩm dựa trên từ khóa tên sản phẩm.");

        Map<String, Object> params1 = new HashMap<>();
        params1.put("type", "object");
        Map<String, Object> props1 = new HashMap<>();
        Map<String, Object> keywordProp = new HashMap<>();
        keywordProp.put("type", "string");
        keywordProp.put("description", "Từ khóa tên sản phẩm (Ví dụ: 'váy đuôi cá', 'vest').");
        props1.put("keyword", keywordProp);
        params1.put("properties", props1);
        params1.put("required", List.of("keyword"));

        function1.put("parameters", params1);
        tool1.put("function", function1);
        tools.add(tool1);

        // Tool 2: Tìm theo danh mục
        Map<String, Object> tool2 = new HashMap<>();
        tool2.put("type", "function");
        Map<String, Object> function2 = new HashMap<>();
        function2.put("name", "get_products_by_category");
        function2.put("description", "Lấy toàn bộ sản phẩm thuộc về một danh mục cụ thể khi khách hỏi chung về nhóm sản phẩm đó.");

        Map<String, Object> params2 = new HashMap<>();
        params2.put("type", "object");
        Map<String, Object> props2 = new HashMap<>();
        Map<String, Object> categoryIdProp = new HashMap<>();
        categoryIdProp.put("type", "number");
        categoryIdProp.put("description", "ID danh mục: 1 nếu là Váy Cưới, 2 nếu là Vest Nam, 3 nếu là Phụ Kiện.");
        props2.put("categoryId", categoryIdProp);
        params2.put("properties", props2);
        params2.put("required", List.of("categoryId"));

        function2.put("parameters", params2);
        tool2.put("function", function2);
        tools.add(tool2);

        return tools;
    }

    private String executeProductSearch(String functionName, String argumentsJson) {
        try {
            Map<?, ?> args = objectMapper.readValue(argumentsJson, Map.class);

            if ("search_products_by_keyword".equals(functionName)) {
                String keyword = args.get("keyword").toString();
                List<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword);
                return formatProductListResult(products);
            }

            if ("get_products_by_category".equals(functionName)) {
                Long categoryId = Long.parseLong(args.get("categoryId").toString());
                List<Product> products = productRepository.findByCategory_CategoryId(categoryId);
                return formatProductListResult(products);
            }
        } catch (Exception e) {
            return "Lỗi xảy ra khi lấy dữ liệu sản phẩm trong kho.";
        }
        return "Không tìm thấy chức năng tương ứng.";
    }

    /**
     * 🌟 ĐÃ THÊM LINK ẢNH VÀO ĐÂY ĐỂ TRUYỀN LÊN CHO AI ĐỌC
     */
    private String formatProductListResult(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return "Kết quả: Hiện tại trong kho của shop không tìm thấy sản phẩm nào trùng khớp.";
        }
        StringBuilder sb = new StringBuilder("Dữ liệu thực tế tại shop:\n");
        for (Product p : products) {
            // Thay p.getImageUrl() thành tên getter chứa link ảnh thật trong file Product.java của bạn nhé
            String imgUrl = (p.getImageUrl() != null) ? p.getImageUrl() : "https://via.placeholder.com/150";

            sb.append(String.format("- Tên: %s | Giá: %,.0f VNĐ | Ảnh: %s\n",
                    p.getProductName(), p.getPrice(), imgUrl));
        }
        return sb.toString();
    }
}
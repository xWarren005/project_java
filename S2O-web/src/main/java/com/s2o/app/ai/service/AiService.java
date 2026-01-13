package com.s2o.app.ai.service;

import com.s2o.app.ai.dto.ChatbotRequest;
import com.s2o.app.entity.Product;
import com.s2o.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductRepository productRepository;

    public AiService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String answer(ChatbotRequest req) {
        String userQuestion = req.getQuestion();

        // Lấy menu từ DB để làm ngữ cảnh cho ChatGPT
        List<Product> products = productRepository.findAll();
        String menuText = products.stream()
                .limit(10)
                .map(p -> p.getName() + " - " + p.getPrice())
                .collect(Collectors.joining(", "));

        String prompt = """
        Bạn là trợ lý nhà hàng.
        Menu hiện có: %s
        Khách hỏi: %s
        Hãy trả lời ngắn gọn, dễ hiểu.
        """.formatted(menuText, userQuestion);

        return callChatGPT(prompt);
    }

    private String callChatGPT(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4.1-mini");
        body.put("messages", List.of(
                Map.of("role", "system", "content", "Bạn là trợ lý nhà hàng."),
                Map.of("role", "user", "content", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> res =
                restTemplate.postForEntity(url, entity, Map.class);

        List choices = (List) res.getBody().get("choices");
        Map message = (Map) ((Map) choices.get(0)).get("message");
        return message.get("content").toString();
    }
}

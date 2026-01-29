package com.s2o.app.ai.service;

import com.s2o.app.ai.dto.ChatbotRequest;
import com.s2o.app.entity.Product;
import com.s2o.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

// --- THÊM IMPORT CHO BIGDECIMAL ---
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        try {
            String userQuestion = req.getQuestion();

            // 1. Lấy menu từ DB
            List<Product> products = productRepository.findAll();
            String menuText = (products != null && !products.isEmpty())
                    ? products.stream()
                    .limit(20)
                    .map(p -> {
                        // 1. Lấy giá gốc (Vì trong Entity đã là BigDecimal nên gán trực tiếp)
                        BigDecimal originalPrice = p.getPrice();

                        // Xử lý trường hợp giá bị null để tránh lỗi (quan trọng)
                        if (originalPrice == null) {
                            originalPrice = BigDecimal.ZERO;
                        }

                        Double discount = p.getDiscount();

                        // 2. Kiểm tra giảm giá
                        if (discount != null && discount > 0) {
                            // Chuyển % giảm giá từ Double sang BigDecimal
                            BigDecimal discountPercent = BigDecimal.valueOf(discount);

                            // Tính hệ số: (100 - discount) / 100
                            // Ví dụ giảm 10% -> factor = 0.9
                            BigDecimal factor = BigDecimal.valueOf(100)
                                    .subtract(discountPercent)
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                            // Giá sau giảm = Giá gốc * hệ số
                            BigDecimal finalPrice = originalPrice.multiply(factor);

                            // Làm tròn tiền VNĐ (bỏ số thập phân)
                            finalPrice = finalPrice.setScale(0, RoundingMode.HALF_UP);
                            originalPrice = originalPrice.setScale(0, RoundingMode.HALF_UP);

                            // Format chuỗi
                            return String.format("%s (Gốc: %s, GIẢM %.0f%% còn: %s VND)",
                                    p.getName(),
                                    originalPrice.toString(),
                                    discount,
                                    finalPrice.toString());
                        } else {
                            // Không giảm giá - chỉ làm tròn giá gốc
                            originalPrice = originalPrice.setScale(0, RoundingMode.HALF_UP);

                            return String.format("%s (%s VND)",
                                    p.getName(),
                                    originalPrice.toString());
                        }
                    })
                    .collect(Collectors.joining("; "))
                    : "Hiện chưa có món ăn nào.";

            // 2. Tạo Prompt
            String prompt = """
            Bạn là nhân viên phục vụ nhà hàng chuyên nghiệp.
            Menu hiện có: [%s]
            Khách hỏi: "%s"
            
            Yêu cầu:
            - Nếu món ăn có giảm giá, HÃY NHẮC ĐẾN giá gốc, %% giảm và giá sau giảm.
            - Trả lời ngắn gọn (dưới 50 từ), lịch sự và thân thiện bằng tiếng Việt.
            """.formatted(menuText, userQuestion);

            return callGemini(prompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, tôi đang gặp chút sự cố. Bạn chờ lát nhé!";
        }
    }

    private String callGemini(String textPrompt) {
        // --- QUAN TRỌNG: Đã đổi về 'gemini-2.5-flash' để tránh lỗi 404 ---
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey.trim();

        Map<String, Object> part = new HashMap<>();
        part.put("text", textPrompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);

            Map responseBody = res.getBody();
            List candidates = (List) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map firstCandidate = (Map) candidates.get(0);
                Map contentMap = (Map) firstCandidate.get("content");
                List parts = (List) contentMap.get("parts");
                Map firstPart = (Map) parts.get(0);
                return firstPart.get("text").toString();
            }
            return "AI không trả lời được.";

        } catch (HttpClientErrorException e) {
            System.err.println("CHI TIẾT LỖI GOOGLE: " + e.getResponseBodyAsString());
            return "Lỗi kết nối: " + e.getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống khi gọi AI.";
        }
    }
}
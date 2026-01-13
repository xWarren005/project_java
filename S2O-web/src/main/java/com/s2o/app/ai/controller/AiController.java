package com.s2o.app.ai.controller;

import com.s2o.app.ai.dto.ChatbotRequest;
import com.s2o.app.ai.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chatbot")
    public String chatbot(@RequestBody ChatbotRequest req) {
        return aiService.answer(req);
    }
}

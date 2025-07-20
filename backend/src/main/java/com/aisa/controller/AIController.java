package com.aisa.controller;

import com.aisa.dto.AIRequest;
import com.aisa.dto.AIResponse;
import com.aisa.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/process")
    public ResponseEntity<AIResponse> processAIRequest(@RequestBody AIRequest request) {
        try {
            String response = aiService.processMessage(request.getMessage());
            return ResponseEntity.ok(new AIResponse(response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new AIResponse("AI処理中にエラーが発生しました: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("AI Service is running");
    }
}
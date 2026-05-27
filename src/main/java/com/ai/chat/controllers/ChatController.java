package com.ai.chat.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ai.chat.dto.ChatRequest;
import com.ai.chat.dto.ChatResponse;
import com.ai.chat.models.AppUser;
import com.ai.chat.models.ChatMessage;
import com.ai.chat.repository.ChatRepository;
import com.ai.chat.repository.UserRepository;
import com.ai.chat.services.SarvamAiService;

@RestController
@RequestMapping("/api/chat")

public class ChatController {

    @Autowired
    private ChatRepository chatrepo;

    @Autowired
    private UserRepository user_repo;

    @Autowired
    private SarvamAiService sarvamAiService;


    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request, Principal principal) {

        // ❗ safety check (prevents null crash)
        if (principal == null) {
            return new ChatResponse("User not authenticated");
        }

        AppUser user = user_repo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChatMessage> history = chatrepo.findByUserOrderByCreatedAtAsc(user);

        // save user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setRole("user");
        userMsg.setContent(request.getMessage());
        userMsg.setUser(user);
        chatrepo.save(userMsg);

        // AI response
        String ai_reply = sarvamAiService.askSarvam(history, request.getMessage());

        // save AI message
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setRole("assistant");
        aiMsg.setContent(ai_reply);
        aiMsg.setUser(user);
        chatrepo.save(aiMsg);

        return new ChatResponse(ai_reply);
    }

    @GetMapping("/history")
    public List<ChatMessage> history(Principal principal) {

        if (principal == null) {
            return List.of();
        }

        AppUser user = user_repo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return chatrepo.findByUserOrderByCreatedAtAsc(user);
    }
}
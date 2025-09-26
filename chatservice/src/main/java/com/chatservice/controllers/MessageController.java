package com.chatservice.controllers;

import com.chatservice.entity.ChatMessage;
import com.chatservice.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/conversation")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @RequestParam String sender,
            @RequestParam String receiver) {
        return ResponseEntity.ok(chatMessageService.getConversation(sender, receiver));
    }

    @GetMapping("/unread/{receiver}")
    public ResponseEntity<List<ChatMessage>> getUnreadMessages(@PathVariable String receiver) {
        return ResponseEntity.ok(chatMessageService.getUnreadMessages(receiver));
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        chatMessageService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}

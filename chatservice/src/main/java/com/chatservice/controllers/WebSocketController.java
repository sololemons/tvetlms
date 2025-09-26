package com.chatservice.controllers;

import com.chatservice.entity.ChatMessage;
import com.chatservice.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class WebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send")
    public void sendPublic(@Payload ChatMessage message) {
        ChatMessage saved = chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/messages", saved);
    }

    @MessageMapping("/private")
    public void sendPrivate(@Payload ChatMessage message) {
        ChatMessage saved = chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                saved
        );
    }
}

package com.chatservice.services;

import com.chatservice.entity.ChatMessage;
import com.chatservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final MessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);
        return chatMessageRepository.save(message);
    }
    public List<ChatMessage> getConversation(String sender, String receiver) {
        return chatMessageRepository.findBySenderAndReceiverOrSenderAndReceiver(
                sender, receiver, receiver, sender
        );
    }

    public List<ChatMessage> getUnreadMessages(String receiver) {
        return chatMessageRepository.findByReceiverAndReadFalse(receiver);
    }

    public void markAsRead(Long messageId) {
        chatMessageRepository.findById(messageId).ifPresent(msg -> {
            msg.setRead(true);
            chatMessageRepository.save(msg);
        });
    }
}

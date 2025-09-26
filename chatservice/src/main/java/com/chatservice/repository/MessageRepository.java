package com.chatservice.repository;

import com.chatservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByReceiverAndReadFalse(String receiver);
    List<ChatMessage> findBySenderAndReceiverOrSenderAndReceiver(
            String sender1, String receiver1,
            String sender2, String receiver2
    );
}

package com.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender")
    private String sender;
    @Column(name = "receiver")
    private String receiver;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(name = "content")
    private String content;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "is_read")
    private boolean read = false;
}

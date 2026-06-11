package com.aipo.weddingshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 🌟 BẮT BUỘC IMPORT DÒNG NÀY

@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long conversationId;

    // 🌟 THÊM ANNOTATION NÀY VÀO ĐÂY: Chặn lỗi Proxy No Session kinh điển của Hibernate Lazy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "roles", "username"})
    private User user;

    @Enumerated(EnumType.STRING)
    private ConversationStatus status;

    private String subject;

    @Column(name = "unread_customer")
    private Integer unreadCustomer = 0;

    @Column(name = "unread_admin")
    private Integer unreadAdmin = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
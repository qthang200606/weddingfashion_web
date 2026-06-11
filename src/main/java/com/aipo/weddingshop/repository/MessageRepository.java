package com.aipo.weddingshop.repository;

import com.aipo.weddingshop.entity.Message;
import com.aipo.weddingshop.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
    List<Message> findByConversation_ConversationIdOrderBySentAtAsc(Long conversationId);
}
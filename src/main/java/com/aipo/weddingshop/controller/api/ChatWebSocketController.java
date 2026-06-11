package com.aipo.weddingshop.controller.api;

import com.aipo.weddingshop.entity.Conversation;
import com.aipo.weddingshop.entity.ConversationStatus;
import com.aipo.weddingshop.entity.MessageType;
import com.aipo.weddingshop.entity.SenderType;
import com.aipo.weddingshop.entity.Message;
import com.aipo.weddingshop.repository.ConversationRepository;
import com.aipo.weddingshop.repository.MessageRepository;
import com.aipo.weddingshop.service.GroqAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final GroqAiService groqAiService;

    @MessageMapping("/chat/send/{conversationId}")
    public void processMessage(@DestinationVariable Long conversationId, Message incomingMessage) {
        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
        if (conversation == null) return;

        incomingMessage.setConversation(conversation);
        incomingMessage.setSentAt(LocalDateTime.now());
        incomingMessage.setIsRead(false);
        messageRepository.save(incomingMessage);

        conversation.setUpdatedAt(LocalDateTime.now());

        if (incomingMessage.getSenderType() == SenderType.CUSTOMER) {
            if (conversation.getStatus() == ConversationStatus.AI) {
                messagingTemplate.convertAndSend("/topic/chat/" + conversationId, incomingMessage);

                String aiResponseText = groqAiService.getAiResponse(incomingMessage.getContent());

                Message aiMessage = Message.builder()
                        .conversation(conversation)
                        .senderType(SenderType.AI)
                        .content(aiResponseText)
                        .messageType(MessageType.TEXT)
                        .isRead(true)
                        .sentAt(LocalDateTime.now())
                        .build();
                messageRepository.save(aiMessage);

                messagingTemplate.convertAndSend("/topic/chat/" + conversationId, aiMessage);
            } else {
                conversation.setUnreadAdmin(
                        conversation.getUnreadAdmin() != null ? conversation.getUnreadAdmin() + 1 : 1
                );
                conversationRepository.save(conversation);
                messagingTemplate.convertAndSend("/topic/chat/" + conversationId, incomingMessage);
                messagingTemplate.convertAndSend("/topic/admin/notification", "NEW_MESSAGE");
            }
        } else if (incomingMessage.getSenderType() == SenderType.ADMIN) {
            conversation.setUnreadCustomer(
                    conversation.getUnreadCustomer() != null ? conversation.getUnreadCustomer() + 1 : 1
            );
            conversationRepository.save(conversation);
            messagingTemplate.convertAndSend("/topic/chat/" + conversationId, incomingMessage);
        }
    }

    // =========================================================================
    // 🔔 HÀM ĐÃ ĐƯỢC LÀM SẠCH - KHÔNG TỰÝ ĐÈ TRẠNG THÁI VÀ SINH TIN NHẮN TRÙNG LẶP
    // =========================================================================
    @MessageMapping("/chat/request-admin/{conversationId}")
    public void requestAdminSupport(@DestinationVariable Long conversationId) {
        // Chỉ giữ nhiệm vụ thông báo tin hiệu cho Dashboard Admin reload danh sách phòng chat
        messagingTemplate.convertAndSend("/topic/admin/notification", "NEW_WAITING_REQUEST");
        System.out.println("Bella System: Đã đồng bộ phát loa thông báo danh sách mới lên màn hình Admin.");
    }
}
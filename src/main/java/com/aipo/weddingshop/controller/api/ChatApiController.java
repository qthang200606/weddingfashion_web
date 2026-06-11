package com.aipo.weddingshop.controller.api;

import com.aipo.weddingshop.entity.Conversation;
import com.aipo.weddingshop.entity.ConversationStatus;
import com.aipo.weddingshop.entity.Message;
import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.repository.ConversationRepository;
import com.aipo.weddingshop.repository.MessageRepository;
import com.aipo.weddingshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.aipo.weddingshop.entity.SenderType;
import com.aipo.weddingshop.entity.MessageType;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    private User getLoggedInUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        return userService.findByEmail(email);
    }

    @GetMapping("/get-or-create")
    public ResponseEntity<?> getOrCreateConversation(Principal principal) {
        User user = getLoggedInUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        Conversation conversation = conversationRepository.findAll().stream()
                .filter(c -> c.getUser() != null
                        && c.getUser().getUserId().equals(user.getUserId())
                        && c.getStatus() != ConversationStatus.CLOSED)
                .findFirst()
                .orElse(null);

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUser(user);
            conversation.setStatus(ConversationStatus.AI);
            conversation.setCreatedAt(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());

            conversation = conversationRepository.save(conversation);
        }

        return ResponseEntity.ok(Map.of(
                "conversationId", conversation.getConversationId(),
                "status", conversation.getStatus().toString()
        ));
    }

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable Long conversationId) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);

        List<Message> history = messageRepository.findByConversationOrderBySentAtAsc(conversation);
        return ResponseEntity.ok(history);
    }

    // =========================================================================
    // 🔀 API CHUYỂN ĐỔI CHẾ ĐỘ CHAT (ĐÃ SỬA LỖI TỰ ĐỘNG NHẢY TRẠNG THÁI)
    // =========================================================================
    @PostMapping("/toggle-status")
    public ResponseEntity<?> toggleConversationStatus(@RequestParam("id") Long conversationId,
                                                      @RequestParam("status") String statusStr) {
        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
        if (conversation == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            ConversationStatus newStatus = ConversationStatus.valueOf(statusStr);

            // 🌟 CHỐNG LẶP TUYỆT ĐỐI: Nếu trạng thái gửi lên giống hệt trạng thái hiện tại trong DB -> Ngắt luồng luôn, không xử lý bừa bãi
            if (conversation.getStatus() == newStatus) {
                return ResponseEntity.ok("Trạng thái đã đồng bộ, không xử lý lại.");
            }

            conversation.setStatus(newStatus);
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);

            // 🌟 Bước 1: Chỉ bắn tín hiệu đổi giao diện nút về kênh CHÍNH XÁC của phòng này
            messagingTemplate.convertAndSend("/topic/chat/status/" + conversationId, Map.of("status", statusStr));

            // 🌟 Bước 2: Phân luồng sinh tin nhắn tự động thông minh
            if (newStatus == ConversationStatus.AI) {
                // Khi quay lại luồng AI -> Tạo tin nhắn chào mừng lập tức
                Message aiReply = new Message();
                aiReply.setConversation(conversation);
                aiReply.setSenderType(SenderType.AI);
                aiReply.setMessageType(MessageType.TEXT);
                aiReply.setContent("Trợ lý ảo Bella đã quay trở lại trực luồng tư vấn rồi đây ạ! Nàng cần Bella hỗ trợ tìm thêm thông tin hay bảng giá dịch vụ cưới nào nữa không ạ? ✨");
                aiReply.setSentAt(LocalDateTime.now());

                messageRepository.save(aiReply);
                messagingTemplate.convertAndSend("/topic/chat/" + conversationId, aiReply);

            } else if (newStatus == ConversationStatus.WAITING_ADMIN) {
                // Khi khách bấm gặp nhân viên thật -> Tạo tin nhắn thông báo hàng đợi
                Message systemReply = new Message();
                systemReply.setConversation(conversation);
                systemReply.setSenderType(SenderType.AI); // Đặt lề trái màu kem cho đồng bộ
                systemReply.setMessageType(MessageType.TEXT);
                systemReply.setContent("Bella đã chuyển line kết nối đến chuyên viên tư vấn. Vui lòng đợi trong giây lát ạ! 🔔");
                systemReply.setSentAt(LocalDateTime.now());

                messageRepository.save(systemReply);
                messagingTemplate.convertAndSend("/topic/chat/" + conversationId, systemReply);
            }

            // 🌟 Bước 3: Chỉ thông báo cho Admin để cập nhật lại số lượng phòng ở Dashboard Admin
            messagingTemplate.convertAndSend("/topic/admin/notification", "Cập nhật danh sách phòng");

            return ResponseEntity.ok("Bella System: Cập nhật chế độ chat thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ.");
        }
    }
}
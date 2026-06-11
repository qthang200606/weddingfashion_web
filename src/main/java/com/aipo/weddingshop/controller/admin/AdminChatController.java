package com.aipo.weddingshop.controller.admin;

import com.aipo.weddingshop.entity.Conversation;
import com.aipo.weddingshop.entity.ConversationStatus;
import com.aipo.weddingshop.entity.Message;
import com.aipo.weddingshop.repository.ConversationRepository;
import com.aipo.weddingshop.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate; // 🌟 1. IMPORT ĐỂ ĐẨY WEBSOCKET TRẠNG THÁI
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor // Khởi tạo Constructor tự động cho các repository và template bên dưới thông qua Lombok
public class AdminChatController {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate; // 🌟 2. INJECT THÊM BIẾN ĐỂ ĐỒNG BỘ NÚT BẤM REALTIME PHÍA KHÁCH KHÁCH

    // =========================================================================
    // 🖥️ 1. ĐIỀU HƯỚNG GIAO DIỆN CHAT DASHBOARD (RENDER THEO PHÒNG CHAT ĐƯỢC CHỌN)
    // =========================================================================
    @GetMapping("/chats")
    public String chatDashboard(Model model, @RequestParam(value = "c", required = false) Long conversationId) {
        // Gắn pageTitle làm cờ hiệu 'chat' giúp đồng bộ hiệu ứng sáng menu Sidebar
        model.addAttribute("pageTitle", "chat");

        // 1. Tải toàn bộ danh sách phòng chat chưa đóng để phân loại vào các Tab
        List<Conversation> allConversations = conversationRepository.findAll();

        // Lọc danh sách phòng chat: Khách đang đợi hỗ trợ từ nhân viên
        List<Conversation> waitingConversations = allConversations.stream()
                .filter(c -> c.getStatus() == ConversationStatus.WAITING_ADMIN)
                .toList();

        // Lọc danh sách phòng chat: Nhân viên đã tiếp quản và đang trực tiếp trò chuyện
        List<Conversation> activeConversations = allConversations.stream()
                .filter(c -> c.getStatus() == ConversationStatus.ADMIN_SUPPORT)
                .toList();

        // Lọc danh sách phòng chat: Đang chạy tự động hoàn toàn bằng máy AI
        List<Conversation> aiConversations = allConversations.stream()
                .filter(c -> c.getStatus() == ConversationStatus.AI)
                .toList();

        // Đổ toàn bộ dữ liệu phân mảnh tab sang cho Thymeleaf hiển thị
        model.addAttribute("waitingConversations", waitingConversations);
        model.addAttribute("activeConversations", activeConversations);
        model.addAttribute("aiConversations", aiConversations);

        // 2. Xử lý logic tải tin nhắn chi tiết nếu Admin đang bấm vào một phòng chat cụ thể (?c=id)
        if (conversationId != null) {
            Conversation currentChat = conversationRepository.findById(conversationId).orElse(null);
            if (currentChat != null && currentChat.getStatus() != ConversationStatus.CLOSED) {
                model.addAttribute("currentChat", currentChat);

                // Lấy toàn bộ lịch sử hội thoại của phòng này xếp tuần tự từ cũ đến mới
                List<Message> messages = messageRepository.findByConversationOrderBySentAtAsc(currentChat);
                model.addAttribute("messages", messages);
            }
        } else {
            // Nếu vào trang chủ /admin/chats mà chưa chọn ai, gán null để giao diện hiện màn hình Inbox trống mặc định
            model.addAttribute("currentChat", null);
            model.addAttribute("messages", null);
        }

        // Trả về đúng đường dẫn file cấu trúc: templates/admin/chat/dashboard.html
        return "/admin/chat/dashboard";
    }

    // =========================================================================
    // 🤝 2. API TIẾP QUẢN PHÒNG CHAT: CHUYỂN TRẠNG THÁI KHÔNG LOAD LẠI TRANG THÔ BẠO
    // =========================================================================
    @PostMapping("/chat/takeover/{id}")
    @ResponseBody // Chuyển hóa trả về dữ liệu text/JSON thuần tuý phục vụ xử lý mượt bằng AJAX ở giao diện html
    public ResponseEntity<?> takeoverRoom(@PathVariable Long id) {
        Conversation conversation = conversationRepository.findById(id).orElse(null);
        if (conversation != null) {
            // Chuyển dòng trạng thái phòng từ WAITING_ADMIN hoặc AI sang ADMIN_SUPPORT
            conversation.setStatus(ConversationStatus.ADMIN_SUPPORT);
            conversationRepository.save(conversation);

            // 🌟 3. BẮN WEBSOCKET ÉP KHUNG CHAT PHÍA KHÁCH HÀNG ĐỔI THÀNH "AI tư vấn 🤖" THỜI GIAN THỰC
            messagingTemplate.convertAndSend("/topic/chat/status/" + id, Map.of("status", "ADMIN_SUPPORT"));

            // 🌟 4. BẮN THÊM THÔNG BÁO CHO KÊNH NOTIFICATION ĐỂ CÁC MÀN HÌNH ADMIN KHÁC CŨNG TỰ CẬP NHẬT LẠI SIDEBAR LẬP TỨC
            messagingTemplate.convertAndSend("/topic/admin/notification", "Cập nhật danh sách phòng");

            return ResponseEntity.ok("Bella System: Nhân viên đã tiếp quản phòng chat thành công!");
        }
        return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy mã phòng hội thoại hợp lệ.");
    }
}
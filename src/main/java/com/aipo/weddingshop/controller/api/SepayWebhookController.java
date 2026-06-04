package com.aipo.weddingshop.controller.api;

import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sepay-webhook") // Khớp chính xác với URL cấu hình trên SePay của bạn
public class SepayWebhookController {

    private final OrderRepository orderRepository;

    @Value("${sepay.webhook.secret:tam_thoi_de_trong}")
    private String webhookSecret;

    public SepayWebhookController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<?> receiveWebhook(@RequestBody Map<String, Object> payload,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 1. Kiểm tra mã xác thực Token lớp bảo mật (Nếu bạn có cấu hình API Key trên SePay)
            if (authHeader != null && !authHeader.isEmpty()) {
                String token = authHeader.replace("Bearer ", "").trim();
                // Nếu không khớp mã bí mật, từ chối luôn
                if (!token.equals(webhookSecret)) {
                    return ResponseEntity.status(401).body("Mã xác thực Webhook không hợp lệ!");
                }
            }

            // 2. Lấy nội dung chuyển khoản an toàn từ Map (Tránh lỗi Mapping DTO)
            String content = (String) payload.get("content"); // Giá trị sẽ là: "486D606050VSVJG8  SEVQR DH4"
            if (content == null || content.isEmpty()) {
                return ResponseEntity.badRequest().body("Nội dung giao dịch trống!");
            }

            System.out.println("=== [SePay Webhook] Nhận nội dung: " + content);
            content = content.toUpperCase();

            // 3. Quét kiểm tra mã đơn hàng
            if (content.contains("SEVQR")) {
                // Lấy danh sách các đơn hàng đang chờ xử lý
                var pendingOrders = orderRepository.findByStatus("PENDING"); // Hãy đảm bảo trong OrderRepository đã viết hàm List<Order> findByStatus(String status)

                for (Order order : pendingOrders) {
                    // Nếu nội dung chứa mã paymentCode (Ví dụ: "DH4")
                    if (content.contains(order.getPaymentCode().toUpperCase())) {

                        // Khớp lệnh thành công! Cập nhật trạng thái đơn hàng sang PAID
                        order.setStatus("PAID");
                        orderRepository.save(order);

                        System.out.println("=== [SePay] Đơn hàng " + order.getPaymentCode() + " đã tự động đổi trạng thái sang PAID! ===");

                        // Trả về 200 OK cho SePay để hệ thống ghi nhận thành công
                        return ResponseEntity.ok(Map.of("success", true, "message", "Đã xử lý đơn hàng thành công"));
                    }
                }
            }

            // Trả về 200 nhưng báo không tìm thấy đơn khớp để SePay không bắn đi bắn lại lệnh này nữa
            return ResponseEntity.ok(Map.of("success", false, "message", "Không tìm thấy đơn hàng khớp mã"));

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi hệ thống, trả về lỗi 500 để debug
            return ResponseEntity.status(500).body("Lỗi xử lý nội bộ: " + e.getMessage());
        }
    }
}
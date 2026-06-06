package com.aipo.weddingshop.controller.admin;

import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderRepository orderRepository;

    public AdminOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 1. Hiển thị danh sách đơn hàng cho Admin
     */
    @GetMapping
    public String showAdminOrdersPage(Model model) {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
        model.addAttribute("orders", orders);
        return "admin/order/orders";
    }

    /**
     * 2. Xử lý cập nhật trạng thái đơn hàng theo quy trình logic nâng cao
     * Đường dẫn: GET http://localhost:8080/admin/orders/update-status?id=...&status=...
     */
    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam("id") Long orderId,
                                    @RequestParam("status") String newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        String currentStatus = order.getStatus();

        // CHẶN LOGIC 1: Nếu đơn hàng đã ĐÃ GIAO hoặc ĐÃ HỦY thì khóa chết, không cho bẻ trạng thái nữa
        if ("DELIVERED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            return "redirect:/admin/orders?error=order-is-locked";
        }

        // CHẶN LOGIC 2: Quy trình chuyển đổi trạng thái tuần tự nếu đi tiếp (Ngoại trừ hành động HỦY ĐƠN)
        if (!"CANCELLED".equals(newStatus)) {
            boolean isValidTransition = false;

            if ("PENDING".equals(currentStatus) && "CONFIRMED".equals(newStatus)) isValidTransition = true;
            else if ("PAID".equals(currentStatus) && "CONFIRMED".equals(newStatus)) isValidTransition = true; // Hỗ trợ nếu hệ thống có cổng tự động đổi PAID
            else if ("CONFIRMED".equals(currentStatus) && "PREPARING".equals(newStatus)) isValidTransition = true;
            else if ("PREPARING".equals(currentStatus) && "SHIPPING".equals(newStatus)) isValidTransition = true;
            else if ("SHIPPING".equals(currentStatus) && "DELIVERED".equals(newStatus)) isValidTransition = true;

            if (!isValidTransition) {
                return "redirect:/admin/orders?error=invalid-status-flow";
            }
        }

        // Nếu vượt qua các vòng kiểm tra trên -> Tiến hành cập nhật Database
        order.setStatus(newStatus);
        orderRepository.save(order);

        return "redirect:/admin/orders";
    }
}
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

    // Sử dụng Constructor Injection để Spring tự động inject OrderRepository vào
    public AdminOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 1. Hiển thị danh sách tất cả đơn hàng cho Admin
     * Đường dẫn: GET http://localhost:8080/admin/orders
     */
    @GetMapping
    public String showAdminOrdersPage(Model model) {
        // Lấy tất cả đơn hàng và sắp xếp giảm dần (Desc) theo trường orderDate (Đơn hàng mới nhất lên đầu)
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));

        // Đẩy danh sách đơn hàng sang cho file Thymeleaf orders.html hiển thị
        model.addAttribute("orders", orders);

        // Trả về đúng đường dẫn cấu trúc thư mục: templates/admin/order/orders.html
        return "admin/order/orders";
    }

    /**
     * 2. Xử lý cập nhật trạng thái đơn hàng (Duyệt đơn / Hủy đơn)
     * Đường dẫn: GET http://localhost:8080/admin/orders/update-status?id=...&status=...
     */
    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam("id") Long orderId,
                                    @RequestParam("status") String newStatus) {
        // Tìm đơn hàng trong DB xem có tồn tại không
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        // CHẶN LOGIC: Nếu đơn hàng đã bị hủy (CANCELLED) từ trước, Admin không thể bẻ trạng thái thành CONFIRMED được nữa
        if ("CANCELLED".equals(order.getStatus())) {
            return "redirect:/admin/orders?error=cannot-update-cancelled-order";
        }

        // Cập nhật trạng thái mới
        order.setStatus(newStatus);

        // Lưu lại thay đổi vào Database
        orderRepository.save(order);

        // Quay trở lại trang danh sách đơn hàng để cập nhật giao diện mới
        return "redirect:/admin/orders";
    }
}
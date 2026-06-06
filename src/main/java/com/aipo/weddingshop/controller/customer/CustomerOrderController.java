package com.aipo.weddingshop.controller.customer;


import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderController {

    private final OrderRepository orderRepository;

    public CustomerOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 1. Xem lịch sử đơn hàng
     * Trả về file: templates/customer/order-history.html
     */
    @GetMapping("/history")
    public String showOrderHistory(@RequestParam(value = "status", defaultValue = "ALL") String status,
                                   Model model) {
        List<Order> orders;

        if ("ALL".equals(status)) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findAll().stream()
                    .filter(o -> status.equals(o.getStatus()))
                    .toList();
        }

        model.addAttribute("orders", orders);
        // Map trực tiếp vào thư mục customer theo ảnh {5A72EED5-C53D-4B65-AD99-46B85A4E23F2}.png
        return "customer/order-history";
    }

    /**
     * 2. Xem chi tiết tiến độ đơn hàng
     * Trả về file: templates/customer/order-detail.html
     */
    @GetMapping("/detail/{id}")
    public String showOrderDetail(@PathVariable("id") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại."));

        model.addAttribute("order", order);
        // Map trực tiếp vào thư mục customer
        return "customer/order-detail";
    }

    /**
     * 3. Xử lý gửi đánh giá đơn hàng
     */
    @PostMapping("/submit-review")
    public String receiveProductReview(@RequestParam("orderId") Long orderId,
                                       @RequestParam("rating") int rating,
                                       @RequestParam("comment") String comment) {
        // Logic xử lý lưu vào database của bạn...
        System.out.println("Đơn hàng " + orderId + " - Đánh giá: " + rating + " sao - Nội dung: " + comment);

        return "redirect:/customer/orders/history?review-success=true";
    }
}
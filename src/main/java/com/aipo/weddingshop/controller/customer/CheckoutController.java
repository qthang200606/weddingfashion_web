package com.aipo.weddingshop.controller.customer;

import com.aipo.weddingshop.entity.*;
import com.aipo.weddingshop.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/customer/checkout")
public class CheckoutController {

    private final UserService userService;
    private final OrderService orderService;

    public CheckoutController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/submit")
    public String processOrder(@RequestParam("receiverName") String receiverName,
                               @RequestParam("receiverPhone") String receiverPhone,
                               @RequestParam("shippingAddress") String shippingAddress,
                               @RequestParam("paymentMethod") String paymentMethod,
                               Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(principal.getName());
        Order order = orderService.createOrder(user, receiverName, receiverPhone, shippingAddress, paymentMethod);
        return "redirect:/customer/checkout/success?id=" + order.getOrderId();
    }

    @GetMapping("/success")
    public String successPage(@RequestParam(value = "id", required = false) Long orderId, Model model) {
        if (orderId == null) {
            return "redirect:/customer/home";
        }
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return "redirect:/customer/home";
            }
            model.addAttribute("order", order);
        } catch (Exception e) {
            return "redirect:/customer/home";
        }
        return "customer/checkout-success";
    }

    /**
     * 🌟 API BỔ SUNG: Cho phép JavaScript gọi liên tục (Realtime) để check trạng thái đơn hàng
     * URL gọi từ Frontend: GET http://localhost:8080/customer/checkout/api/status/{id}
     */
    @GetMapping("/api/status/{id}")
    @ResponseBody
    public ResponseEntity<String> checkOrderStatus(@PathVariable("id") Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                // Trả về chuỗi trạng thái trong Database (Ví dụ: "PENDING", "PAID")
                return ResponseEntity.ok(order.getStatus());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }
}
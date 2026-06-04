package com.aipo.weddingshop.controller.customer; // Sửa lại dòng này cho đúng tên package thực tế của bạn

import com.aipo.weddingshop.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutApiController {

    private final OrderRepository orderRepository;

    public CheckoutApiController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/check-status/{id}")
    public ResponseEntity<String> checkStatus(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> ResponseEntity.ok(order.getStatus()))
                .orElse(ResponseEntity.notFound().build());
    }
}
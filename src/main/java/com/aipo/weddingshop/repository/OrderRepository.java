package com.aipo.weddingshop.repository;

import com.aipo.weddingshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Spring Boot sẽ tự động lo hết các hàm save(), findById(), delete()... cho bạn

    Optional<Order> findByPaymentCode(String paymentCode);
    List<Order> findByStatus(String status);

    // Tìm tất cả đơn hàng của User dựa trên ID và sắp xếp đơn mới nhất lên đầu
    List<Order> findByUser_UserIdOrderByOrderDateDesc(Long userId);

    // Lọc đơn hàng của User theo cả ID và Trạng thái (PENDING, CONFIRMED,...) từ Admin
    List<Order> findByUser_UserIdAndStatusOrderByOrderDateDesc(Long userId, String status);
}
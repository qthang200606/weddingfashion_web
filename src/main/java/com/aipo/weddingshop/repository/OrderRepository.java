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
}
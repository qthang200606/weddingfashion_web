package com.aipo.weddingshop.repository;

import com.aipo.weddingshop.entity.Cart;
import com.aipo.weddingshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Tìm giỏ hàng dựa vào đối tượng User đăng nhập
    Optional<Cart> findByUser(User user);
}
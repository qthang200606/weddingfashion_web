package com.aipo.weddingshop.repository;

import com.aipo.weddingshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Không cần viết gì thêm, JpaRepository đã cung cấp đủ các hàm cơ bản CRUD
}
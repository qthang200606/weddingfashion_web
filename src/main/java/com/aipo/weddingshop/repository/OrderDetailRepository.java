package com.aipo.weddingshop.repository;

import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {


}
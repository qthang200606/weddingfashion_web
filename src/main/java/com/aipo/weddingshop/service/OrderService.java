package com.aipo.weddingshop.service;

import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.entity.User;
import java.util.List;

public interface OrderService {

    // 1. Hàm khởi tạo đơn hàng hiện tại của bạn
    Order createOrder(User user, String receiverName, String receiverPhone, String shippingAddress, String paymentMethod);

    // 2. Hàm tìm kiếm thông tin đơn hàng theo ID (Đã đồng bộ tên hàm getOrderById)
    Order getOrderById(Long orderId);

    // 3. BỔ SUNG: Lấy tất cả đơn hàng của một khách hàng cụ thể
    List<Order> getOrdersByUserId(Long userId);

    // 4. BỔ SUNG: Lọc đơn hàng của khách hàng theo trạng thái cập nhật từ Admin (ALL, PENDING, CONFIRMED...)
    List<Order> getOrdersByUserIdAndStatus(Long userId, String status);

    long countAllOrders();

    // Hàm tính tổng doanh thu từ các đơn hàng thành công (DELIVERED)
    double calculateTotalRevenue();
}
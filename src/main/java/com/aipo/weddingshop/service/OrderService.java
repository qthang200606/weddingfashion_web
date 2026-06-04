package com.aipo.weddingshop.service;

import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.entity.User;

public interface OrderService {

    // Hàm khởi tạo đơn hàng hiện tại của bạn
    Order createOrder(User user, String receiverName, String receiverPhone, String shippingAddress, String paymentMethod);

    /**
     * Hàm bổ sung: Tìm kiếm thông tin đơn hàng theo ID
     * Phục vụ cho việc hiển thị mã QR và số tiền tại trang checkout-success
     *
     * @param orderId ID của đơn hàng cần tìm
     * @return Đối tượng Order nếu tìm thấy, hoặc null/ném ngoại lệ nếu không tồn tại
     */
    Order getOrderById(Long orderId);
}
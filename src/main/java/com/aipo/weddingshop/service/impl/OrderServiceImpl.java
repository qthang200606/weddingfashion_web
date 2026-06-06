package com.aipo.weddingshop.service.impl;

import com.aipo.weddingshop.entity.*;
import com.aipo.weddingshop.repository.CartItemRepository;
import com.aipo.weddingshop.repository.CartRepository;
import com.aipo.weddingshop.repository.OrderRepository;
import com.aipo.weddingshop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public Order createOrder(User user, String receiverName, String receiverPhone, String shippingAddress, String paymentMethod) {
        // 1. Kiểm tra giỏ hàng của khách
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng rỗng"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng của bạn đang trống, không thể đặt hàng!");
        }

        // 2. Khởi tạo đối tượng đơn hàng ban đầu
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING"); // Mặc định là chờ thanh toán

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();

        // 3. Duyệt danh sách sản phẩm trong giỏ để chuyển thành chi tiết đơn hàng
        for (CartItem item : cart.getCartItems()) {
            BigDecimal itemPrice = BigDecimal.valueOf(item.getProduct().getPrice());
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal subtotal = itemPrice.multiply(quantity);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(itemPrice);
            detail.setSubtotal(subtotal);

            details.add(detail);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setOrderDetails(details);
        order.setTotalAmount(totalAmount);

        // 🌟 BƯỚC NÂNG CẤP TỰ ĐỘNG HÓA CHO SEPAY & ADMIN:
        // Lưu lần 1 xuống database để MySQL sinh tự động ra `order_id`
        Order savedOrder = orderRepository.save(order);

        // Sinh mã thanh toán duy nhất dựa trên ID (Ví dụ: DH105) dùng làm nội dung chuyển khoản QR
        String generatedPaymentCode = "DH" + savedOrder.getOrderId();
        savedOrder.setPaymentCode(generatedPaymentCode);

        // Lưu lần 2 để cập nhật mã `payment_code` chính thức vào DB
        savedOrder = orderRepository.save(savedOrder);

        // 4. Đặt hàng thành công thì dọn sạch các item trong giỏ hàng và lưu lại trạng thái giỏ rỗng
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    /**
     * Hàm tìm kiếm đơn hàng theo ID (Bổ sung để đồng bộ với CheckoutController)
     * Giúp lấy thông tin đơn hàng (mã thanh toán, tổng tiền) để tạo mã QR VietinBank tại trang checkout-success.html
     */
    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với mã hệ thống: " + orderId));
    }

    /**
     * Lấy toàn bộ danh sách lịch trình đơn hàng của khách hàng (Đã xếp mới nhất lên đầu)
     */
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUser_UserIdOrderByOrderDateDesc(userId);
    }

    /**
     * Lọc danh sách đơn hàng dựa trên trạng thái (Ví dụ: Chỉ xem những đơn ĐANG GIAO VÁY)
     * Giúp đồng bộ tức thì khi Admin thay đổi trạng thái bên trang quản trị
     */
    @Override
    public List<Order> getOrdersByUserIdAndStatus(Long userId, String status) {
        return orderRepository.findByUser_UserIdAndStatusOrderByOrderDateDesc(userId, status);
    }
}
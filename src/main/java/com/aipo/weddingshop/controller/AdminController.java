package com.aipo.weddingshop.controller;

import com.aipo.weddingshop.service.ProductService;
import com.aipo.weddingshop.service.OrderService;
import com.aipo.weddingshop.service.AppointmentService; // Hãy chắc chắn bạn đã tạo Interface/Service này
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor // Tự động tạo Constructor để nhúng các Service bên dưới vào
public class AdminController {

    // Tiêm các Service để gọi dữ liệu từ Database
    private final ProductService productService;
    private final OrderService orderService;
    private final AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 1. Đồng bộ để làm sáng đèn menu "Tổng quan Dashboard" trên Sidebar
        model.addAttribute("pageTitle", "dashboard");

        // 2. Lấy số lượng sản phẩm thật (Sử dụng hàm bạn vừa cấu hình ở bước trước)
        long totalProducts = productService.countAllProducts();
        model.addAttribute("totalProducts", totalProducts);

        // 3. Lấy tổng số đơn hàng thật
        long totalOrders = orderService.countAllOrders();
        model.addAttribute("totalOrders", totalOrders);

        // 4. Lấy doanh thu thực tế (Xử lý kiểm tra null ngay tại đây để an toàn)
        Double totalRevenue = orderService.calculateTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        // 5. Lấy số lượng lịch hẹn thử váy trong ngày hôm nay
        long totalAppointments = appointmentService.countTodayAppointments();
        model.addAttribute("totalAppointments", totalAppointments);

        // Trả về file giao diện tại: src/main/resources/templates/admin/dashboard.html
        return "admin/dashboard";
    }
}
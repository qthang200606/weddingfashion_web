package com.aipo.weddingshop.controller.api;

import com.aipo.weddingshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/analytics")
public class RevenueApiController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueData(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "filter", required = false) String filter, // Dự phòng trường hợp cache hoặc lệch tên biến
            @RequestParam(value = "year", required = false) Integer year) {

        // 🌟 TỐI ƯU: Gom cả 2 biến về một mối để không bao giờ bị lệch luồng
        String activeType = (type != null) ? type : filter;
        if (activeType == null) {
            activeType = "week"; // Mặc định nếu không truyền gì lên
        }

        Map<String, Object> response = new HashMap<>();
        List<String> labels;
        List<Double> data;

        // Bắt bài tất cả các từ khóa liên quan đến "Năm"
        if ("year_current".equalsIgnoreCase(activeType) || "year".equalsIgnoreCase(activeType)) {
            // Hiển thị 12 tháng của năm nay chuẩn chỉnh
            labels = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12");
            data = orderRepository.getRevenueByMonthsInCurrentYear();

        } else if ("select_year".equalsIgnoreCase(activeType) && year != null) {
            // Hiển thị 12 tháng của năm được chọn từ Combobox cụ thể
            labels = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12");
            data = orderRepository.getRevenueByMonthsInSelectedYear(year);

        } else {
            // Mặc định: Hiển thị 7 ngày của tuần này
            labels = List.of("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật");
            data = orderRepository.getRevenueByDaysInCurrentWeek();
        }

        response.put("labels", labels);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
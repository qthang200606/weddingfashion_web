package com.aipo.weddingshop.repository;

import com.aipo.weddingshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    Double sumTotalAmountByStatus(@Param("status") String status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status IN ('DELIVERED', 'APPROVED')")
    Double getTotalRevenueForCard();
    // =========================================================================
    // 🌟 2 HÀM THỐNG KÊ DOANH THU THỰC TẾ ĐỒNG BỘ THEO DB CỦA BELLA COUTURE
    // =========================================================================

    /**
     * Lấy doanh thu thực tế theo từng thứ trong tuần hiện tại (Từ Thứ 2 -> Chủ Nhật)
     * Đã đổi sang cột total_amount và order_date chuẩn theo thực thể của nàng
     */
    @Query(value = "SELECT COALESCE(SUM(o.total_amount), 0) " +
            "FROM ( " +
            "    SELECT 2 AS day_num UNION SELECT 3 UNION SELECT 4 " +
            "    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 1 " +
            ") days " +
            "LEFT JOIN orders o ON DAYOFWEEK(o.order_date) = days.day_num " +
            "AND YEARWEEK(o.order_date, 1) = YEARWEEK(CURDATE(), 1) " +
            "GROUP BY days.day_num " +
            "ORDER BY FIELD(days.day_num, 2, 3, 4, 5, 6, 7, 1)", nativeQuery = true)
    List<Double> getRevenueByDaysInCurrentWeek();

    /**
     * LUỒNG 2: Lấy doanh thu 12 tháng trong NĂM NAY (Tháng 1 -> Tháng 12)
     */
    /**
     * 2. LẤY DOANH THU CÁC THÁNG TRONG NĂM NAY (Chỉ tính đơn hàng CONFIRMED)
     */
    /**
     * LẤY DOANH THU CÁC THÁNG TRONG NĂM NAY (Đã đồng bộ trạng thái)
     */
    @Query(value = "SELECT COALESCE(SUM(o.total_amount), 0) " +
            "FROM ( " +
            "    SELECT 1 AS month_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
            "    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 " +
            "    UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 " +
            ") months " +
            "LEFT JOIN orders o ON MONTH(o.order_date) = months.month_num " +
            "AND YEAR(o.order_date) = YEAR(CURDATE()) " +
            "AND o.status IN ('DELIVERED', 'APPROVED') " + // 🌟 Thay bằng các trạng thái được tính tiền của nàng
            "GROUP BY months.month_num " +
            "ORDER BY months.month_num ASC", nativeQuery = true)
    List<Double> getRevenueByMonthsInCurrentYear();

    /**
     * 3. LẤY DOANH THU CÁC THÁNG THEO NĂM ĐƯỢC CHỌN TỪ COMBOBOX
     */
    @Query(value = "SELECT COALESCE(SUM(o.total_amount), 0) " +
            "FROM ( " +
            "    SELECT 1 AS month_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
            "    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 " +
            "    UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 " +
            ") months " +
            "LEFT JOIN orders o ON MONTH(o.order_date) = months.month_num " +
            "AND YEAR(o.order_date) = :year " +
            "AND o.status = 'CONFIRMED' " + // 🌟 THÊM ĐIỀU KIỆN NÀY: Đồng bộ bộ lọc
            "GROUP BY months.month_num " +
            "ORDER BY months.month_num ASC", nativeQuery = true)
    List<Double> getRevenueByMonthsInSelectedYear(@Param("year") int year);
}
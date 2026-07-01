package com.aipo.weddingshop.repository;
import com.aipo.weddingshop.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Hàm tự động tìm kiếm danh sách lịch hẹn dựa theo userId của khách hàng
    // Sắp xếp theo ngày hẹn mới nhất lên đầu (OrderByAppointmentDateDesc)
    List<Appointment> findByUser_UserIdOrderByAppointmentDateDesc(Long userId);

    long countByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
}
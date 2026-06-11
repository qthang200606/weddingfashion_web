package com.aipo.weddingshop.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Khớp với kiểu DATETIME trong DB của bạn (Gộp cả ngày và giờ)
    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    // Khớp chính xác với cột 'note TEXT' trong DB
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "status", length = 50)
    private String status; // PENDING, CONFIRMED, CANCELLED
}
package com.aipo.weddingshop.service;

import com.aipo.weddingshop.entity.Appointment;
import com.aipo.weddingshop.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    Appointment createAppointment(
            User user,
            LocalDateTime appointmentDate,
            String note
    );

    List<Appointment> getAppointmentsByUserId(Long userId);

    void cancelAppointment(Long appointmentId, Long userId);

    // THÊM
    List<Appointment> getAllAppointments();

    Optional<Appointment> getAppointmentById(Long id);

    Appointment saveAppointment(Appointment appointment);
}
package com.aipo.weddingshop.service.impl;

import com.aipo.weddingshop.entity.Appointment;
import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.repository.AppointmentRepository;
import com.aipo.weddingshop.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public Appointment createAppointment(User user, LocalDateTime appointmentDate, String note) {
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setNote(note);
        appointment.setStatus("PENDING"); // Mặc định chờ Admin duyệt
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsByUserId(Long userId) {
        return appointmentRepository.findByUser_UserIdOrderByAppointmentDateDesc(userId);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn!"));

        if (!appointment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền huỷ lịch này!");
        }

        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    @Transactional
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
}
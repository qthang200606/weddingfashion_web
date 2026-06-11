package com.aipo.weddingshop.controller.admin;

import com.aipo.weddingshop.entity.Appointment;
import com.aipo.weddingshop.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping({"", "/list"})
    public String listAppointments(Model model) {

        model.addAttribute("pageTitle", "appointment");

        List<Appointment> allAppointments =
                appointmentService.getAllAppointments();

        List<Appointment> pendingList = allAppointments.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        List<Appointment> confirmedList = allAppointments.stream()
                .filter(a -> "CONFIRMED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        List<Appointment> cancelledList = allAppointments.stream()
                .filter(a -> "CANCELLED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("pendingAppointments", pendingList);
        model.addAttribute("confirmedAppointments", confirmedList);
        model.addAttribute("cancelledAppointments", cancelledList);

        model.addAttribute("pendingCount", pendingList.size());
        model.addAttribute("confirmedCount", confirmedList.size());
        model.addAttribute("cancelledCount", cancelledList.size());

        return "admin/appointment/list";
    }

    @PostMapping("/approve/{id}")
    public String approveAppointment(@PathVariable("id") Long appointmentId) {

        try {

            Appointment appointment = appointmentService
                    .getAppointmentById(appointmentId)
                    .orElseThrow(() ->
                            new RuntimeException("Không tìm thấy lịch hẹn"));

            appointment.setStatus("CONFIRMED");

            appointmentService.saveAppointment(appointment);

            return "redirect:/admin/appointments?success=approved";

        } catch (Exception e) {

            return "redirect:/admin/appointments?error="
                    + e.getMessage();
        }
    }

    @PostMapping("/reject/{id}")
    public String rejectAppointment(@PathVariable("id") Long appointmentId) {

        try {

            Appointment appointment = appointmentService
                    .getAppointmentById(appointmentId)
                    .orElseThrow(() ->
                            new RuntimeException("Không tìm thấy lịch hẹn"));

            appointment.setStatus("CANCELLED");

            appointmentService.saveAppointment(appointment);

            return "redirect:/admin/appointments?success=rejected";

        } catch (Exception e) {

            return "redirect:/admin/appointments?error="
                    + e.getMessage();
        }
    }
}
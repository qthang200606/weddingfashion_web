package com.aipo.weddingshop.controller.customer;

import com.aipo.weddingshop.entity.Appointment;
import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.service.AppointmentService;
import com.aipo.weddingshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    // ================= KHU VỰC ĐIỀU HƯỚNG GIAO DIỆN TRUYỀN THỐNG =================

    @GetMapping("/book")
    public String showBookingForm(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "customer/book-appointment";
    }

    @PostMapping("/book")
    public String processBooking(@RequestParam("appointmentDateOnly") String dateStr,
                                 @RequestParam("appointmentTimeOnly") String timeStr,
                                 @RequestParam(value = "note", required = false) String note,
                                 Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByEmail(principal.getName());

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);
        LocalDateTime fullDateTime = LocalDateTime.of(date, time);

        appointmentService.createAppointment(user, fullDateTime, note);
        return "redirect:/appointments/my-list";
    }

    @GetMapping("/my-list")
    public String showMyAppointments(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("appointments", appointmentService.getAppointmentsByUserId(user.getUserId()));
        return "customer/my-appointments";
    }

    @PostMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Long appointmentId, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByEmail(principal.getName());
        try {
            appointmentService.cancelAppointment(appointmentId, user.getUserId());
        } catch (Exception e) {
            return "redirect:/appointments/my-list?error=" + e.getLocalizedMessage();
        }
        return "redirect:/appointments/my-list?success=true";
    }


    // ================= API AJAX MỚI (PHỤC VỤ TAB MODAL TRÊN TRANG CHỦ) =================

    /**
     * API nhận dữ liệu đặt lịch hẹn từ trang chủ ngầm (Không reload trang)
     */
    @PostMapping("/api/book")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processBookingAjax(@RequestParam("appointmentDateOnly") String dateStr,
                                                                  @RequestParam("appointmentTimeOnly") String timeStr,
                                                                  @RequestParam(value = "note", required = false) String note,
                                                                  Principal principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal == null) {
            response.put("status", "error");
            response.put("message", "Nàng vui lòng đăng nhập trước khi thực hiện đặt lịch hẹn nhé!");
            return ResponseEntity.status(401).body(response);
        }

        try {
            User user = userService.findByEmail(principal.getName());
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime fullDateTime = LocalDateTime.of(date, time);

            // Gọi Service tạo lịch hẹn xuống DB
            appointmentService.createAppointment(user, fullDateTime, note);

            response.put("status", "success");
            response.put("message", "Đặt lịch thành công! Chuyển sang tab bên cạnh để theo dõi trạng thái nàng nhé.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Hệ thống bận: " + e.getLocalizedMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * API bắn dữ liệu danh sách lịch lịch trình để nạp động vào tab "Lịch hẹn của tôi"
     */
    @GetMapping("/api/my-list")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getMyBookingListAjax(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByEmail(principal.getName());
        // Lấy danh sách thực tế của User từ hàm có sẵn trong Service của bạn
        List<Appointment> userAppointments = appointmentService.getAppointmentsByUserId(user.getUserId());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Appointment app : userAppointments) {
            Map<String, Object> map = new HashMap<>();
            map.put("appointmentId", app.getAppointmentId());
            // Trả về chuỗi ISO String để Javascript tự parse định dạng ra lịch Việt Nam sinh động
            map.put("appointmentDate", app.getAppointmentDate().toString());
            map.put("status", app.getStatus() != null ? app.getStatus().toString() : "PENDING");
            map.put("fullName", user.getFullName());
            map.put("note", app.getNote());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }
}
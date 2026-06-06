package com.aipo.weddingshop.controller.admin;

import com.aipo.weddingshop.entity.Banner;
import com.aipo.weddingshop.service.BannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/banners") // Đường dẫn vào trang quản trị banner
public class AdminBannerController {

    private final BannerService bannerService;

    public AdminBannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    // 1. Hiển thị trang danh sách banner
    @GetMapping
    public String listBanners(Model model) {
        model.addAttribute("banners", bannerService.getAllBanners());
        return "admin/banner/list"; // Trỏ tới file list.html trong thư mục templates/admin/banner
    }

    // 2. Hiển thị trang thêm mới banner
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("banner", new Banner());
        return "admin/banner/add";
    }

    // 3. Xử lý lưu banner (Thêm mới / Cập nhật)
    @PostMapping("/save")
    public String saveBanner(@ModelAttribute("banner") Banner banner) {
        bannerService.saveBanner(banner);
        return "redirect:/admin/banners";
    }

    // 4. Hiển thị trang sửa banner
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("banner", bannerService.getBannerById(id));
        return "admin/banner/edit";
    }

    // 5. Xử lý xóa banner
    @GetMapping("/delete/{id}")
    public String deleteBanner(@PathVariable("id") Long id) {
        bannerService.deleteBanner(id);
        return "redirect:/admin/banners";
    }
}
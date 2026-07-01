package com.aipo.weddingshop.controller.admin;

import com.aipo.weddingshop.entity.Product;
import com.aipo.weddingshop.service.ProductService;
import com.aipo.weddingshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // 1. SỬA TẠI ĐÂY: Thêm danh mục vào trang danh sách để Popup hiển thị dữ liệu
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll()); // Đã bổ sung dòng này!
        return "admin/product/list";
    }

    // 2. SỬA TẠI ĐÂY: Chuyển edit cũ sang API REST (Trả về JSON cho JavaScript gọi AJAX)
    @GetMapping("/api/{id}")
    @ResponseBody
    public Product getProductApi(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam("file") MultipartFile file) throws Exception {

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String uploadDir = "uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, file.getBytes());

            product.setImageUrl("/uploads/" + fileName);
        } else if (product.getProductId() != null) {
            // Mẹo nhỏ: Khi cập nhật sản phẩm mà người dùng KHÔNG chọn lại ảnh mới
            // Chúng ta giữ lại đường dẫn ảnh cũ từ cơ sở dữ liệu để tránh bị ghi đè thành rỗng
            Product oldProduct = productService.findById(product.getProductId());
            product.setImageUrl(oldProduct.getImageUrl());
        }

        productService.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
}
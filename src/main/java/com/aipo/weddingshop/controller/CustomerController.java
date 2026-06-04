package com.aipo.weddingshop.controller;

import com.aipo.weddingshop.entity.Category;
import com.aipo.weddingshop.entity.Product;
import com.aipo.weddingshop.service.CategoryService;
import com.aipo.weddingshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor // Tự động tạo Constructor cho các thuộc tính có từ khóa 'final'
public class CustomerController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // 1. TRANG CHỦ KHÁCH HÀNG
    @GetMapping("/home")
    public String home(Model model){
        model.addAttribute("products", productService.findAll());
        return "customer/home";
    }

    // 2. TRANG DANH SÁCH TẤT CẢ DANH MỤC THIẾT KẾ
    @GetMapping("/categories")
    public String showAllCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "customer/categories";
    }

    // 3. TRANG DANH SÁCH SẢN PHẨM THUỘC MỘT DANH MỤC CỤ THỂ
    @GetMapping("/categories/{id}")
    public String showProductsByCategory(@PathVariable("id") Long categoryId, Model model) {
        Category currentCategory = categoryService.findById(categoryId);
        model.addAttribute("currentCategory", currentCategory);

        List<Product> products = productService.findByCategoryId(categoryId);
        model.addAttribute("products", products);

        return "customer/category-products";
    }

    // 4. TRANG CHI TIẾT SẢN PHẨM (Đã sửa lỗi gọi Service thay vì Repository)
    @GetMapping("/products/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        // ĐÃ SỬA: Dùng productService.findById(id) thay vì gọi trực tiếp repository
        Product product = productService.findById(id);

        model.addAttribute("product", product);
        return "customer/product-detail";
    }

    @PostMapping("/customer/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("productSize") String productSize,
                            @RequestParam("quantity") Integer quantity) {
        // Xử lý lưu sản phẩm, size đã chọn (S,M,L) và số lượng vào database/session giỏ hàng
        return "redirect:/customer/cart";
    }
}
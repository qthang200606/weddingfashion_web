package com.aipo.weddingshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.aipo.weddingshop.entity.Category;
import com.aipo.weddingshop.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model){
        model.addAttribute(
                "categories",
                categoryService.findAll());
        return "admin/category/list";
    }

    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute(
                "category",
                new Category());
        return "admin/category/form";
    }

    // 🌟 ĐÃ SỬA CHỖ NÀY: Tách riêng biệt thành API trả về dữ liệu JSON cho JS gọi AJAX
    @GetMapping("/api/{id}")
    @ResponseBody // Bắt buộc phải có để ép đối tượng sang dữ liệu JSON thô, không trả về HTML
    public Category getCategoryApi(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Category category){
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model){
        model.addAttribute(
                "category",
                categoryService.findById(id));
        return "admin/category/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id){
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}
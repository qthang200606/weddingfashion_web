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
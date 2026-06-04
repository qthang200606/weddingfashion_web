package com.aipo.weddingshop.controller;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.aipo.weddingshop.dto.RegisterDTO;
import com.aipo.weddingshop.service.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String registerPage(Model model){

        model.addAttribute("user", new RegisterDTO());

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute("user")
            RegisterDTO dto){

        authService.register(dto);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(){

        return "login";
    }
}
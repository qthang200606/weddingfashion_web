package com.aipo.weddingshop.controller;


import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginSuccessController {

    private final UserRepository userRepository;

    @GetMapping("/redirect")
    public String redirect(Principal principal){

        User user = userRepository
                .findByEmail(principal.getName())
                .get();

        if(user.getRole().name().equals("ADMIN")){
            return "redirect:/admin/dashboard";
        }

        return "redirect:/customer/home";
    }
}
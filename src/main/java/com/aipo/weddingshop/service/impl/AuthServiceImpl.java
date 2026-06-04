package com.aipo.weddingshop.service.impl;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aipo.weddingshop.dto.RegisterDTO;
import com.aipo.weddingshop.entity.Role;
import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.repository.UserRepository;
import com.aipo.weddingshop.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDTO dto) {

        if(userRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
    }
}
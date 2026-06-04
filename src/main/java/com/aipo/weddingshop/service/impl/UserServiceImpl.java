package com.aipo.weddingshop.service.impl;

import com.aipo.weddingshop.entity.Order;
import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.repository.UserRepository;
import com.aipo.weddingshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại với email: " + email));
    }


}
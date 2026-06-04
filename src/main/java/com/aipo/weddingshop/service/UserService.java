package com.aipo.weddingshop.service;

import com.aipo.weddingshop.entity.User;

public interface UserService {
    User findByEmail(String email);
}
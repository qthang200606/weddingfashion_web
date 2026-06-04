package com.aipo.weddingshop.service;

import com.aipo.weddingshop.entity.Cart;
import com.aipo.weddingshop.entity.User;

public interface CartService {
    Cart getCartByUser(User user);
    void addToCart(User user, Long productId, String size, Integer quantity);
    void updateQuantity(Long cartItemId, String action);
    void deleteCartItem(Long cartItemId);
}
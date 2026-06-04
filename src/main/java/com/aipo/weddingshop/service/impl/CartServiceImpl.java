package com.aipo.weddingshop.service.impl;

import com.aipo.weddingshop.entity.Cart;
import com.aipo.weddingshop.entity.CartItem;
import com.aipo.weddingshop.entity.Product;
import com.aipo.weddingshop.entity.User;
import com.aipo.weddingshop.repository.CartItemRepository;
import com.aipo.weddingshop.repository.CartRepository;
import com.aipo.weddingshop.repository.ProductRepository; // Giả định bạn đã có
import com.aipo.weddingshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart getCartByUser(User user) {
        // Nếu user chưa từng có giỏ hàng, tự động tạo mới một giỏ trống cho họ
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional
    public void addToCart(User user, Long productId, String size, Integer quantity) {
        Cart cart = getCartByUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        // Logic kiểm tra: Nếu váy cưới này cùng mẫu VÀ cùng SIZE đã có trong giỏ hàng chưa
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId)
                        && item.getProductSize().equalsIgnoreCase(size))
                .findFirst();

        if (existingItem.isPresent()) {
            // Đã có trong giỏ -> Cộng dồn số lượng đặt
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // Chưa có -> Tạo mới bản ghi cart_item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setProductSize(size);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long cartItemId, String action) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ"));

        if ("increase".equals(action)) {
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else if ("decrease".equals(action)) {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartItemRepository.save(item);
            } else {
                // Nếu số lượng giảm về 0 thì tự động xóa sản phẩm đó khỏi giỏ luôn
                cartItemRepository.delete(item);
            }
        }
    }

    @Override
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
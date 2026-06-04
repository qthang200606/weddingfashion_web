package com.aipo.weddingshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items") // Khớp với tên bảng: cart_items
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Khớp với khóa chính: id BIGINT PRIMARY KEY
    private Long id;

    // Khóa ngoại liên kết tới bảng carts (cart_id) - Đã lược bỏ referencedColumnName để an toàn tuyệt đối
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // Khóa ngoại liên kết tới bảng products (product_id) - Đã lược bỏ referencedColumnName để an toàn tuyệt đối
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Lưu trữ size váy cưới (S, M, L, XL) mà cô dâu chọn
    @Column(name = "product_size")
    private String productSize;

    @Column(name = "quantity")
    private Integer quantity;

    // --- Hệ thống Getter và Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getProductSize() { return productSize; }
    public void setProductSize(String productSize) { this.productSize = productSize; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
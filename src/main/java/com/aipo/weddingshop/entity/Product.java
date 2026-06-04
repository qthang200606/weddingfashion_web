package com.aipo.weddingshop.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private Integer stockQuantity;

    private String status;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
}
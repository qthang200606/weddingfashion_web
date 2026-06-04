package com.aipo.weddingshop.service;
import java.util.List;
import com.aipo.weddingshop.entity.Product;
public interface ProductService {

    List<Product> findAll();

    Product findById(Long id);

    Product save(Product product);

    void delete(Long id);
    List<Product> findByCategoryId(Long categoryId);
}
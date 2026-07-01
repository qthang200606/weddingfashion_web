package com.aipo.weddingshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aipo.weddingshop.entity.Product;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ProductRepository
        extends JpaRepository<Product,Long>{
    List<Product> findByCategory_CategoryId(Long categoryId);
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
}
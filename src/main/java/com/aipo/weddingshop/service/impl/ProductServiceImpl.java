package com.aipo.weddingshop.service.impl;
import java.util.List;

import com.aipo.weddingshop.entity.Product;
import com.aipo.weddingshop.service.ProductService;
import org.springframework.stereotype.Service;

import com.aipo.weddingshop.entity.Category;
import com.aipo.weddingshop.repository.ProductRepository;
import com.aipo.weddingshop.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl
        implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        // Hãy đảm bảo bạn đã tiêm productRepository ở đầu class bằng Lombok hoặc Autowired
        // Và trong ProductRepository đã khai báo hàm findByCategory_CategoryId hoặc findByCategory_Id
        return productRepository.findByCategory_CategoryId(categoryId);
    }
    @Override
    public long countAllProducts() {
        return productRepository.count(); // Hàm count() này có sẵn trong JpaRepository
    }
    public List<Product> searchByName(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }



}
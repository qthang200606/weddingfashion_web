package com.aipo.weddingshop.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.aipo.weddingshop.entity.Category;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

}
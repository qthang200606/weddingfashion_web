package com.aipo.weddingshop.service;

import java.util.List;
import com.aipo.weddingshop.entity.Category;

public interface CategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category save(Category category);

    void delete(Long id);
}
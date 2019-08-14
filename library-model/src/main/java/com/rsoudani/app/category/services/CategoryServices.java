package com.rsoudani.app.category.services;

import com.rsoudani.app.category.exception.CategoryExistentException;
import com.rsoudani.app.category.exception.CategoryNotFoundException;
import com.rsoudani.app.category.model.Category;
import com.rsoudani.app.common.exception.FieldNotValidException;

import java.util.List;

public interface CategoryServices {
    Category add (Category category) throws FieldNotValidException, CategoryExistentException;

    void update(Category category) throws FieldNotValidException, CategoryNotFoundException;

    Category findById(Long id);

    List<Category> findAll();
}

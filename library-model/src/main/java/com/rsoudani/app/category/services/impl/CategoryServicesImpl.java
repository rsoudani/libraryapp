package com.rsoudani.app.category.services.impl;

import com.rsoudani.app.category.exception.CategoryExistentException;
import com.rsoudani.app.category.exception.CategoryNotFoundException;
import com.rsoudani.app.category.model.Category;
import com.rsoudani.app.category.repository.CategoryRepository;
import com.rsoudani.app.category.services.CategoryServices;
import com.rsoudani.app.common.exception.FieldNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CategoryServicesImpl implements CategoryServices {

    CategoryRepository categoryRepository;
    Validator validator;

    @Override
    public Category add(Category category) {
        validate(category);
        return categoryRepository.add(category);
    }

    @Override
    public void update(Category category) {
        validate(category);

        if (!categoryRepository.existById(category.getId())){
            throw new CategoryNotFoundException();
        }
        categoryRepository.update(category);
    }

    @Override
    public Category findById(Long id) {

        Category category = categoryRepository.findById(id);
        if (category == null){
            throw new CategoryNotFoundException();
        }
        return category;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll("name");
    }

    private void validate(Category category) {
        validateCategoryFields(category);
        validateCategoryExistence(category);
    }

    private void validateCategoryFields(Category category) {
        final Set<ConstraintViolation<Category>> errors = validator.validate(category);
        final Iterator<ConstraintViolation<Category>> iterator = errors.iterator();
        if (iterator.hasNext()){
            ConstraintViolation<Category> violation = iterator.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }

    private void validateCategoryExistence(Category category) {
        if (categoryRepository.alreadyExists(category)) {
            throw new CategoryExistentException();
        }
    }
}

package com.rsoudani.app.category.services.impl;

import com.rsoudani.app.category.exception.CategoryExistentException;
import com.rsoudani.app.category.exception.CategoryNotFoundException;
import com.rsoudani.app.category.model.Category;
import com.rsoudani.app.category.repository.CategoryRepository;
import com.rsoudani.app.category.services.CategoryServices;
import com.rsoudani.app.common.exception.FieldNotValidException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

import static com.rsoudani.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class CategoryServicesUTest {
    private CategoryServices categoryServices;
    private CategoryRepository categoryRepository;
    private Validator validator;

    @Before
    public void initTestCase(){
        categoryServices = new CategoryServicesImpl();
        categoryRepository = mock(CategoryRepository.class);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        ((CategoryServicesImpl) categoryServices).validator = validator;
        ((CategoryServicesImpl) categoryServices).categoryRepository = categoryRepository;
    }

    @Test
    public void addCategoryWithNullName() {
        addCategoryWithInvalidName(null);
    }

    @Test
    public void addCategoryWithShortName() {
        addCategoryWithInvalidName("a");
    }

    @Test(expected = CategoryExistentException.class)
    public void addCategoryWithExistentName(){
        when(categoryRepository.alreadyExists(java())).thenReturn(true);
        categoryServices.add(java());
    }

    @Test
    public void addValidCategory(){
        when(categoryRepository.alreadyExists(java())).thenReturn(false);
        when(categoryRepository.add(java())).thenReturn(categoryWithId(java(), 1L));

        final Category categoryAdded =categoryServices.add(java());
        assertThat(categoryAdded.getId(), is(equalTo(1l)));
    }

    @Test
    public void addCategoryWithLongName() {
        addCategoryWithInvalidName("This is a long name that will cause an exception");
    }

    @Test
    public void findCategoryById(){
        when(categoryRepository.findById(1L)).thenReturn(categoryWithId(java(), 1L));
        Category result = categoryServices.findById(1L);
        assertThat(result, is(notNullValue()));
        assertThat(categoryWithId(java(), 1L), is(equalTo(result)));
    }

    @Test(expected = CategoryNotFoundException.class)
    public void findCategoryByIdNotFound(){
        when(categoryRepository.findById(1L)).thenReturn(null);
        categoryServices.findById(1L);
        fail("An error should have been thrown");
    }

    private void addCategoryWithInvalidName(String name) {
        try {
            categoryServices.add(new Category(name));
            fail("An error should have been thrown");
        }catch (FieldNotValidException e){
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }

    @Test
    public void findAllNoCategories(){
        when(categoryRepository.findAll("name")).thenReturn(new ArrayList<>());
        List<Category> categories = categoryServices.findAll();
        assertThat(categories.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void findAllCategories(){
        when(categoryRepository.findAll("name")).thenReturn(allCategories());
        List<Category> categories = categoryServices.findAll();
        assertThat(categories, is(equalTo(allCategories())));

    }

    @Test
    public void updateCategoryWithNullName() {
        updateCategoryWithInvalidName(null);
    }

    @Test
    public void updateCategoryWithShortName() {
        updateCategoryWithInvalidName("a");
    }

    @Test
    public void updateCategoryWithLongName() {
        updateCategoryWithInvalidName("This is a long name that will cause an exception");
    }

    @Test(expected = CategoryExistentException.class)
    public void updateCategoryWithExistentName(){
        when(categoryRepository.alreadyExists(java())).thenReturn(true);
        categoryServices.update(java());
    }

    @Test(expected = CategoryNotFoundException.class)
    public  void updateNonExistenceCategory(){
        when(categoryRepository.existById(1L)).thenReturn(false);
        categoryServices.update(categoryWithId(java(), 1L));
    }



    @Test
    public void updateValidCategory(){
        when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(categoryRepository.existById(1L)).thenReturn(true);
        categoryServices.update(categoryWithId(java(), 1L));
        verify(categoryRepository).update(categoryWithId(java(), 1L));

    }

    private void updateCategoryWithInvalidName(String name) {
        try {
            categoryServices.update(new Category(name));
            fail("An error should have been thrown");
        }catch (FieldNotValidException e){
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }
}

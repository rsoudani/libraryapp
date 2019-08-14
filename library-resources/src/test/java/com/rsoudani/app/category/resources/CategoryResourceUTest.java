package com.rsoudani.app.category.resources;

import com.rsoudani.app.category.exception.CategoryExistentException;
import com.rsoudani.app.category.exception.CategoryNotFoundException;
import com.rsoudani.app.category.model.Category;
import com.rsoudani.app.category.services.CategoryServices;
import com.rsoudani.app.common.exception.FieldNotValidException;
import com.rsoudani.app.common.model.HttpCode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static com.rsoudani.app.commontests.category.CategoryForTestsRepository.categoryWithId;
import static com.rsoudani.app.commontests.category.CategoryForTestsRepository.java;
import static com.rsoudani.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.rsoudani.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.rsoudani.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class CategoryResourceUTest {
    private CategoryResource categoryResource;

    private static final String PATH_RESOURCE_CATEGORIES = "categories";

    @Mock
    private CategoryServices categoryServices;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
        categoryResource = new CategoryResource();

        categoryResource.categoryServices = categoryServices;
        categoryResource.categoryJsonConverter = new CategoryJsonConverter();
    }


    @Test
    public void addValidCategory() {
        when(categoryServices.add(java())).thenReturn(categoryWithId(java(), 1L));
        final Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE_CATEGORIES, "newCategory.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addCategoryWithNulName() {
        when(categoryServices.add(java())).thenThrow(new FieldNotValidException("name", "may not be null"));
        final Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE_CATEGORIES, "newCategory.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }

    @Test
    public void addExistentCategory() {
        when(categoryServices.add(java())).thenThrow(new CategoryExistentException());
        final Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE_CATEGORIES, "newCategory.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }

    @Test
    public void updateValidCategory() {
        Response response = categoryResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE_CATEGORIES, "category.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity(), is(equalTo("")));

        verify(categoryServices).update(categoryWithId(java(), 1L));
    }

    @Test
    public void updateCategoryNotFound() {
        doThrow(new CategoryNotFoundException()).when(categoryServices).update(categoryWithId(java(), 1L));
        Response response = categoryResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE_CATEGORIES, "category.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
        assertJsonResponseWithFile(response, "categoryNotFound.json");
    }

    @Test
    public void updateCategoryWithNameBelongingToOtherCategory() {
        doThrow(new CategoryExistentException()).when(categoryServices).update(categoryWithId(java(), 1L));
        Response response = categoryResource.update(1L, readJsonFile(getPathFileRequest(PATH_RESOURCE_CATEGORIES, "category.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }

    @Test
    public void updateCategoryWithNullName() {
        doThrow(new FieldNotValidException("name", "may not be null")).when(categoryServices).update(
                categoryWithId(new Category(), 1L));

        final Response response = categoryResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE_CATEGORIES, "categoryWithNullName.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }

    @Test
    public void findCategory(){
        when(categoryServices.findById(1L)).thenReturn(categoryWithId(java(), 1L));
        Response response = categoryResource.findById(1L);
        assertThat(response.getStatus(), equalTo(HttpCode.OK.getCode()));
        assertJsonResponseWithFile(response, "categoryFound.json");
    }

    @Test
    public void findCategoryNotFound(){
        when(categoryServices.findById(1L)).thenThrow(new CategoryNotFoundException());
        Response response = categoryResource.findById(1L);
        assertThat(response.getStatus(), equalTo(HttpCode.NOT_FOUND.getCode()));
        assertJsonResponseWithFile(response, "categoryNotFound.json");
    }
    private void assertJsonResponseWithFile(Response response, String filename) {
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE_CATEGORIES, filename));

    }

}

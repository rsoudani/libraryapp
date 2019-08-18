package com.rsoudani.app.category.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rsoudani.app.category.exception.CategoryExistentException;
import com.rsoudani.app.category.exception.CategoryNotFoundException;
import com.rsoudani.app.category.model.Category;
import com.rsoudani.app.category.services.CategoryServices;
import com.rsoudani.app.common.exception.FieldNotValidException;
import com.rsoudani.app.common.json.JsonUtils;
import com.rsoudani.app.common.json.JsonWriter;
import com.rsoudani.app.common.json.OperationResultJsonWriter;
import com.rsoudani.app.common.model.HttpCode;
import com.rsoudani.app.common.model.OperationResult;
import com.rsoudani.app.common.model.ResourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

import java.util.List;

import static com.rsoudani.app.common.model.StandardsOperationResults.*;

public class CategoryResource {
    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");
    CategoryServices categoryServices;
    CategoryJsonConverter categoryJsonConverter;

    private Logger log = LoggerFactory.getLogger(getClass());
    OperationResult result;

    public Response add(final String body) {
        log.debug("Adding new category with body: {}", body);
        Category category = categoryJsonConverter.convertFrom(body);

        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result = null;

        try {
            category = categoryServices.add(category);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
        } catch (CategoryExistentException e) {
            log.error("There's already category for the given name", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultExistent(
                    RESOURCE_MESSAGE, "name"
            );
        } catch (FieldNotValidException e) {
            log.error("Field not valid", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultInvalidField(
                    RESOURCE_MESSAGE, e
            );
        }

        log.debug("Returning result after adding category: {}", result);
        String entity = OperationResultJsonWriter.toJson(result);
        Response build = Response.status(httpCode.getCode()).entity(entity).build();
        return build;
    }

    public Response update(Long id, String body) {
        log.debug("updating category with id: {} and body: {}", id, body);
        Category category = categoryJsonConverter.convertFrom(body);
        category.setId(id);
        HttpCode httpCode = HttpCode.OK;
        OperationResult result;

        try {
            categoryServices.update(category);
            result = OperationResult.success();
        } catch (CategoryExistentException e) {
            log.error("There's already category for the given name", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        } catch (FieldNotValidException e) {
            log.error("Field not valid", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (CategoryNotFoundException e) {
            log.error("Category not found", e);
            httpCode = HttpCode.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();

    }

    public Response findById(Long id) {
        log.debug("Find category: {}", id);
        HttpCode httpCode;
        OperationResult result;
        try {
            Category category = categoryServices.findById(id);
            result = OperationResult.success(category);
            httpCode = HttpCode.OK;
        } catch (CategoryNotFoundException e){
            log.error("Category not found {}", e);
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
            httpCode = HttpCode.NOT_FOUND;
        }
        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    public Response findAll() {
        log.debug("Returning all categories");
        List<Category> categories = categoryServices.findAll();
        log.debug("Found {} categories", categories.size());

        JsonElement jsonWithPagingAndEntries = getJsonElementWithPagingAndEntries(categories);
        result = OperationResult.success(jsonWithPagingAndEntries);

        return Response.status(HttpCode.OK.getCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries)).build();
    }

    private JsonElement getJsonElementWithPagingAndEntries(List<Category> categories) {
        JsonObject jsonWithPagingAndEntries = new JsonObject();

        JsonObject jsonPaging = new JsonObject();
        jsonPaging.addProperty("totalRecords", categories.size());

        jsonWithPagingAndEntries.add("paging", jsonPaging);
        jsonWithPagingAndEntries.add("entries", categoryJsonConverter.convertToJsonElement(categories));

        return jsonWithPagingAndEntries;
    }
}

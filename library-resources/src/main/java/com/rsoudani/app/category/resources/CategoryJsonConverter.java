package com.rsoudani.app.category.resources;

import com.google.gson.JsonObject;
import com.rsoudani.app.category.model.Category;

import static com.rsoudani.app.common.json.JsonReader.getStringOrNull;
import static com.rsoudani.app.common.json.JsonReader.readAsJsonObject;

public class CategoryJsonConverter {
    public Category convertFrom(final String json){
        final JsonObject jsonObject = readAsJsonObject(json);

        Category category = new Category();
        category.setName(getStringOrNull(jsonObject, "name"));
        return category;
    }
}

package com.rsoudani.app.category.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rsoudani.app.category.model.Category;

import java.util.List;

import static com.rsoudani.app.common.json.JsonReader.getStringOrNull;
import static com.rsoudani.app.common.json.JsonReader.readAsJsonObject;

public class CategoryJsonConverter {
    public Category convertFrom(final String json) {
        final JsonObject jsonObject = readAsJsonObject(json);

        Category category = new Category();
        category.setName(getStringOrNull(jsonObject, "name"));
        return category;
    }

    public JsonElement convertToJsonElement(Category category) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", category.getId());
        jsonObject.addProperty("name", category.getName());
        return jsonObject;
    }


    public JsonElement convertToJsonElement(List<Category> categories) {
        JsonArray jsonArray = new JsonArray();

        categories.forEach(element -> jsonArray.add(convertToJsonElement(element)));

        return jsonArray;
    }
}

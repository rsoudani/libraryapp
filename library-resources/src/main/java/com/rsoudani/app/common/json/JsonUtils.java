package com.rsoudani.app.common.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {

    private JsonUtils(){}

    public static JsonElement getJsonElementWithId(Long id){
        JsonObject idJson = new JsonObject();
        idJson.addProperty("id", id);
        return idJson;
    }
}

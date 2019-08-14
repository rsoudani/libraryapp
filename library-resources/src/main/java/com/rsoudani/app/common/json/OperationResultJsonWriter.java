package com.rsoudani.app.common.json;

import com.google.gson.JsonObject;
import com.rsoudani.app.common.model.OperationResult;

public class OperationResultJsonWriter {

    private OperationResultJsonWriter() {
    }

    public static String toJson(OperationResult operationResult) {
        Object jsonObject = getJsonObject(operationResult);
        return JsonWriter.writeToString(jsonObject);
    }

    private static Object getJsonObject(OperationResult operationResult) {
        if (operationResult.isSuccess()) {
            return getJsonSuccess(operationResult);
        } else {
            return getJsonError(operationResult);
        }
    }

    private static Object getJsonSuccess(OperationResult operationResult) {
        return operationResult.getEntity();
    }

    private static JsonObject getJsonError(OperationResult operationResult) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorIdentification", operationResult.getErrorIdentification());
        jsonObject.addProperty("errorDescription", operationResult.getErrorDescription());
        return jsonObject;
    }
}

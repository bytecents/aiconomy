package com.se.aiconomy.common.utils;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Read a JSON file and parse it into an Object (supports Map, List, nested structures)
     *
     * @param filePath JSON file path
     * @return Parsed JSON data
     */
    public static Object readJson(String filePath) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return convertJsonElement(jsonElement);
        }
    }

    /**
     * Read JSON and return as a JSON string
     *
     * @param filePath JSON file path
     * @return JSON string
     */
    public static String readJsonAsString(String filePath) throws IOException {
        Object jsonData = readJson(filePath);
        return GSON.toJson(jsonData);
    }

    /**
     * Write an object to a JSON file
     *
     * @param filePath Target JSON file path
     * @param data     Object to write
     */
    public static void writeJson(String filePath, Object data) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            GSON.toJson(data, writer);
        }
    }

    /**
     * Recursively convert JSONElement to Java Map or List
     *
     * @param jsonElement JSON element
     * @return Converted Java object
     */
    private static Object convertJsonElement(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            Map<String, Object> map = new LinkedHashMap<>();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), convertJsonElement(entry.getValue()));
            }
            return map;
        } else if (jsonElement.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                list.add(convertJsonElement(element));
            }
            return list;
        } else if (jsonElement.isJsonNull()) {
            return null;
        } else {
            return jsonElement.getAsString();
        }
    }
}

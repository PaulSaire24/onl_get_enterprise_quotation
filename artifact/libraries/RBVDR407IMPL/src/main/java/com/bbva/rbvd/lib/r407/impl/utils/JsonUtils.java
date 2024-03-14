package com.bbva.rbvd.lib.r407.impl.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {

    public static final JsonUtils INSTANCE = new JsonUtils();

    private final Gson gson;

    private JsonUtils() {
        this.gson = new GsonBuilder().create();
    }

    public static JsonUtils getInstance() { return INSTANCE; }

    public String serialization(Object o) { return this.gson.toJson(o); }


}

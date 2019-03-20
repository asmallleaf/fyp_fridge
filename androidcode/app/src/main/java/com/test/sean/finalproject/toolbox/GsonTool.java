package com.test.sean.finalproject.toolbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class GsonTool<T>{
    private Gson gson;
    private T objectclass;

    public GsonTool(T objectclass){
        gson = new Gson();
        this.objectclass = objectclass;
    }

    public T getJsonList(String json,Type type ){
        this.objectclass = gson.fromJson(json,type);
        return objectclass;
    }
}

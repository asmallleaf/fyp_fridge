package com.test.sean.finalproject.toolbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;

// it is a toolbox for Gson, a json decoder developed by Google
// it can materialize for a specific type
public class GsonTool<T>{
    private Gson gson;
    private T objectclass;

    public GsonTool(T objectclass){
        gson = new Gson();
        this.objectclass = objectclass;
    }

    // it is used to resolve data from json file
    // to use it, it can be programed as followed
    // GsonTool<xx> gsonTool = new GsonTool<>(xx);
    // gsonTool.getJsonList(jsonData, xx.class)
    // or gsonTool.getJsonList(jsonData, new TypeToken(List<xx>){}.getType()) for lists
    public T getJsonList(String json,Type type ){
        this.objectclass = gson.fromJson(json,type);
        return objectclass;
    }
}

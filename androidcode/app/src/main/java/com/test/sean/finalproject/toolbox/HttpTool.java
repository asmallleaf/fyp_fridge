package com.test.sean.finalproject.toolbox;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpTool {
    public class PostBody{
        private Map<String,String> body = new HashMap<>();

        public PostBody add(String key,String value){
            this.body.put(key,value);
            return this;
        }
        public PostBody clear(){
            this.body.clear();
            return this;
        }
        public PostBody set(String key,String value){
            this.body.remove(key);
            this.body.put(key,value);
            return this;
        }
        public PostBody delete(String key){
            this.body.remove(key);
            return this;
        }
        public Map<String,String> getBody(){
            return this.body;
        }
    }


    public static void sendHttpRequest(String address, Map<String,String> map, okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        for(Map.Entry<String,String> entry:map.entrySet()){
            formBody.add(entry.getKey(),entry.getValue());
        }
        RequestBody requestBody = formBody.build();

        Request request = new Request.Builder().
                url(address).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}

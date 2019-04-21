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

// this is a toolbox for HTTP behaviors
public class HttpTool {
    // PostBody is a class defined as the body content in a POST request
    public class PostBody{
        // the hash map is used to store the arguments transferred into body
        private Map<String,String> body = new HashMap<>();

        // add is used to add a pair of new key and value
        public PostBody add(String key,String value){
            this.body.put(key,value);
            return this;
        }
        // clear is used to clear the stored content in body
        public PostBody clear(){
            this.body.clear();
            return this;
        }
        // set is used to update a value of a key
        public PostBody set(String key,String value){
            this.body.remove(key);
            this.body.put(key,value);
            return this;
        }
        // delete is used to delete a pair of value from the body
        public PostBody delete(String key){
            this.body.remove(key);
            return this;
        }
        // the getter of hash map
        public Map<String,String> getBody(){
            return this.body;
        }
    }

    // it is used to send a HTTP request in method of POST
    // to used it a Callback intent should be passed
    // in the Callback class, onFailure and onResponse method should be implemented
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

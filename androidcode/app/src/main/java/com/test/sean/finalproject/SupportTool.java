package com.test.sean.finalproject;

import android.nfc.Tag;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.test.sean.finalproject.config.Config;
import com.test.sean.finalproject.logAndSign.Login;
import com.test.sean.finalproject.models.Items;
import com.test.sean.finalproject.models.SEMsg;
import com.test.sean.finalproject.models.UserMsg;
import com.test.sean.finalproject.toolbox.DbTool;
import com.test.sean.finalproject.toolbox.GsonTool;
import com.test.sean.finalproject.toolbox.HttpTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;



public class SupportTool {
    private Config config = new Config();
    private HttpTool.PostBody postBody = new HttpTool().new PostBody();
    private static final String TAG = "MainActivity";
    private SEMsg seMsg = new SEMsg();
    private UserMsg userMsg = new UserMsg();
    private int state_code;
    private List<Items> itemList = new ArrayList<>();
    public enum Instance{
        SeMsg, UserMsg
    }

    public void askGetList(String key,String token){
        this.itemList.clear();
        config.setApi("/getlist");
        config.setHostUrl(config.TESTHOST);
        postBody.add("itemListNum",key).add("token",token);
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody()
                , new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if(response.code()>=300){
                    GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                    seMsg = gsonTool.getJsonList(responseData, SEMsg.class);
                }
                else {
                    GsonTool<List<Items>> gsonTool = new GsonTool<>(itemList);
                    itemList = gsonTool.getJsonList(responseData, new TypeToken<List<Items>>() {
                    }.getType());
                }
            }
        });
    }

    public void askSignin(String name,String passwd,String passwd2,String fridge){
        this.seMsg= new SEMsg();
        config.setApi("/signin").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("userName",name).add("password",passwd).add("password2",passwd2)
                .add("fridgeNum",fridge);
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(),config.getApi()),postBody.getBody(),new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String reponseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(reponseData,SEMsg.class);
            }
        });
    }

    public void askLogin(String name,String password){
        this.seMsg = new SEMsg();
        config.setApi("/login").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("userName",name).add("password",password);
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(),config.getApi()),postBody.getBody(),new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(responseData,SEMsg.class);
            }
        });
    }

    public void askTemp(String token,String fridgeNum){
        this.seMsg = new SEMsg();
        config.setApi("/getinf").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("token",token).add("fridgeNum",fridgeNum);
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(responseData,SEMsg.class);
            }
        });
    }

    public void askGetUser(String token){
        this.userMsg = new UserMsg();
        config.setApi("/getuser").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("token",token);
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<UserMsg> gsonTool = new GsonTool<>(userMsg);
                userMsg = gsonTool.getJsonList(responseData,UserMsg.class);
            }
        });
    }

    public void askSendInf(String token,String fridgeNum,Boolean isShow){
        this.seMsg = new SEMsg();
        config.setApi("/sendinf").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("token",token).add("fridgeNum",fridgeNum).add("isShow",DbTool.bool2str(isShow));
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(responseData,SEMsg.class);
            }
        });
    }

    public String getListCode() {
        return seMsg.getListCode();
    }

    public String getToken(){
        return seMsg.getListCode();
    }

    public String getTab(){
        return seMsg.getTab();
    }

    public String getState(){
        return seMsg.getState();
    }

    public String getState(Instance instance){
        if(instance==Instance.SeMsg)
            return seMsg.getState();
        else if(instance==Instance.UserMsg)
            return userMsg.getState();
        else
            return null;
    }

    public List<Items> getItemList(){
        return this.itemList;
    }

    public int getStateCode(){ return state_code;}

    public SEMsg getSeMsg(){
        return this.seMsg;
    }

    public UserMsg getUserMsg() {
        return userMsg;
    }
}
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

// it is a support tool for toolbox
// it will provided different methods to send request to different API routes.
public class SupportTool {
    // the configuration
    private Config config = new Config();
    // the body of POST request, it need to be cleared if needed
    private HttpTool.PostBody postBody = new HttpTool().new PostBody();
    // the environment to output error
    private static final String TAG = "MainActivity";
    private SEMsg seMsg = new SEMsg();
    private UserMsg userMsg = new UserMsg();
    private int state_code;
    private List<Items> itemList = new ArrayList<>();
    // this enum class is used to get result respectively from two message classes
    public enum Instance{
        SeMsg, UserMsg
    }

    // this is a method to send request to getlist API route
    public void askGetList(String key,String token){
        // clear the list of items
        this.itemList.clear();
        // load configuration
        config.setApi("/getlist");
        config.setHostUrl(config.TESTHOST);
        // load post body
        postBody.add("itemListNum",key).add("token",token);
        // send http request
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody()
                , new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
            // deal with a successful response
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                // if the response state code is more than 300, which is failed but not in a bad link
                if(response.code()>=300){
                    // resolve the json file with seMsg class
                    GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                    seMsg = gsonTool.getJsonList(responseData, SEMsg.class);
                }
                else {
                    // resolve json file with list of Items class
                    GsonTool<List<Items>> gsonTool = new GsonTool<>(itemList);
                    itemList = gsonTool.getJsonList(responseData, new TypeToken<List<Items>>() {
                    }.getType());
                }
            }
        });
    }

    // this is a method to send request to signin API route
    public void askSignin(String name,String passwd,String passwd2,String fridge){
        // clear the seMsg
        this.seMsg= new SEMsg();
        // load configuration
        config.setApi("/signin").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("userName",name).add("password",passwd).add("password2",passwd2)
                .add("fridgeNum",fridge);
        // send http request
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(),config.getApi()),postBody.getBody(),new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                // resolve the json file with seMsg class no matter it is successful or not
                String reponseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(reponseData,SEMsg.class);
            }
        });
    }

    // this is a method to send request to login API route
    public void askLogin(String name,String password){
        // clear the seMsg
        this.seMsg = new SEMsg();
        // load configuration
        config.setApi("/login").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("userName",name).add("password",password);
        // send http request
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(),config.getApi()),postBody.getBody(),new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                // resolve the json file with seMsg class no matter it is successful or not
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(responseData,SEMsg.class);
            }
        });
    }

    // this is a method to send request to getinf API route
    public void askTemp(String token,String fridgeNum){
        // clear the seMsg
        this.seMsg = new SEMsg();
        // load configuration
        config.setApi("/getinf").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("token",token).add("fridgeNum",fridgeNum);
        // send http request
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // resolve the json file with seMsg class no matter it is successful or not
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(responseData,SEMsg.class);
            }
        });
    }

    // this is a method to send request to getuser API route
    public void askGetUser(String token){
        // clear userMsg intent
        this.userMsg = new UserMsg();
        // load configuration
        config.setApi("/getuser").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("token",token);
        // send http request
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // resolve the json file with userMsg no matter the response is failed or not
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<UserMsg> gsonTool = new GsonTool<>(userMsg);
                userMsg = gsonTool.getJsonList(responseData,UserMsg.class);
            }
        });
    }

    // this is a method to send request to sendinf API route
    public void askSendInf(String token,String fridgeNum,Boolean isShow){
        // clear the seMsg
        this.seMsg = new SEMsg();
        // load configuration
        config.setApi("/sendinf").setHostUrl(config.TESTHOST);
        postBody.clear();
        postBody.add("token",token).add("fridgeNum",fridgeNum).add("isShow",DbTool.bool2str(isShow));
        // send http request
        HttpTool.sendHttpRequest(DbTool.getApi(config.getHostUrl(), config.getApi()), postBody.getBody(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // resolve the json file with seMsg class no matter it is successful or not
                String responseData = response.body().string();
                state_code = response.code();
                GsonTool<SEMsg> gsonTool = new GsonTool<>(seMsg);
                seMsg = gsonTool.getJsonList(responseData,SEMsg.class);
            }
        });
    }

    // the getters and setters
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

    // the getState is overloaded, it can choose to get the state in SeMsg class or UserMsg class
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
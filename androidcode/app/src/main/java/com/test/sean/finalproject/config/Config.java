package com.test.sean.finalproject.config;

import com.test.sean.finalproject.toolbox.HttpTool;

import java.util.Collections;
import java.util.Map;

public class Config {
    private String  hostUrl;
    private String api;
    public final String LOCALHOST = "http://127.0.0.1;5000";
    public final String TESTHOST = "http://47.101.45.81";

    public String getHostUrl() {
        return hostUrl;
    }

    public Config setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
        return this;
    }

    public String getApi() {
        return api;
    }

    public Config setApi(String api) {
        this.api = api;
        return this;
    }

}

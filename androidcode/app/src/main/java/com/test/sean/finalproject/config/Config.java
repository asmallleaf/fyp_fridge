package com.test.sean.finalproject.config;

// it is a class of configuration, some basic constant value will be
// defined in the class
// for host uri and api, it has a getter and setter
// the whole route can be built by combining the hostUrl and api

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

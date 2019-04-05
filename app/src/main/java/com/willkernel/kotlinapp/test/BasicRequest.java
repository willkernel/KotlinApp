package com.willkernel.kotlinapp.test;

/**
 * Created by willkernel
 * on 2019/3/31.
 */
public class BasicRequest {
    public String token = (String) SharedPreferencesHelper.getToken();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

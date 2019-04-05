package com.willkernel.kotlinapp.test;

import android.util.Log;

import javax.inject.Inject;

/**
 * Created by willkernel
 * on 2019/3/31.
 * A 依赖B,B实例化后由外部传入
 */

public class UserStore {
    private static final String TAG = "UserStore";

    @Inject
    public UserStore(String url){
        Log.e(TAG,"url "+url);
    }

    public void login() {
        Log.i(TAG, "login");
    }
}

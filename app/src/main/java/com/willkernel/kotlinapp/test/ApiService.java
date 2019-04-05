package com.willkernel.kotlinapp.test;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by willkernel
 * on 2019/3/31.
 */
public class ApiService {
    private static final String TAG = "ApiService";
    private Context context;
    @Inject
    public ApiService(Context context) {
        this.context = context;
    }

    public ApiService(String url) {
        Log.e(TAG, "url " + url);
    }

    public void register() {
        Log.i(TAG, "register " + context);
    }
}

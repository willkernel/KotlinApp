package com.willkernel.kotlinapp.test;

import android.app.Application;

/**
 * Created by willkernel
 * on 2019/4/1.
 */
public class MyApp extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
//        全局单例
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}

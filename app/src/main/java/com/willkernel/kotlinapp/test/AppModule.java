package com.willkernel.kotlinapp.test;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by willkernel
 * on 2019/4/1.
 * 全局单例
 */
@Module
public class AppModule {
    private MyApp myApp;

    public AppModule(MyApp myApp) {
        this.myApp = myApp;
    }

    @Singleton
    @Provides
    public ApiService provideApiService(){
        return new ApiService(myApp);
    }
}

package com.willkernel.kotlinapp.test;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by willkernel
 * on 2019/4/1.
 * 全局单例
 * Singleton 的组件不能依赖其他 scope 的组件，只能其他 scope 的组件依赖 Singleton的组件 如下： AppComponent
 * 已经用@Singleton 修饰就不能再去依赖（dependencies=XXX.class）别的 Component
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    ApiService getApiService();
}

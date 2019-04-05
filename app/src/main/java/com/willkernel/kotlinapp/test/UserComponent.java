package com.willkernel.kotlinapp.test;

import android.util.Log;
import dagger.Component;

import javax.inject.Singleton;

/**
 * inject 方法接收父类型参数，而调用时传入的是子类型对象则无法注入。比如你想作用 BaseActivity，inject() 就传入 BaseActivity,
 * 但是只能作用 BaseActivity 不能作用子类MainActivity。反之亦然
 * <p>
 * LoginComponent 和UserComponent 为两个不同的@Component，@Singleton的生命周期依附于
 * component同一个 module provide singleton ,不同 component 也是不一样
 */
@PerActivity
@Component(modules = {UserModule.class}, dependencies = AppComponent.class)
public interface UserComponent {
    void inject(RetrofitTestActivity retrofitTestActivity);

    void inject(LoginActivity loginActivity);
}

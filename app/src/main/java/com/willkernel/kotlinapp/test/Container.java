package com.willkernel.kotlinapp.test;

import dagger.Lazy;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by willkernel
 * on 2019/4/1.
 */
public class Container {
    @Inject
    Lazy<User> lazyUser; //注入Lazy元素
    @Inject
    Provider<User> providerUser; //注入Provider元素
    public void init(){
//        DaggerComponent.create().inject(this);
        User user1=lazyUser.get();
//在这时才创建user1,以后每次调用get会得到同一个user1对象

        User user2=providerUser.get();
//在这时创建user2，以后每次调用get会再强制调用Module的Provides方法一次，
//根据Provides方法具体实现的不同，可能返回跟user2是同一个对象，也可能不是。
    }
}

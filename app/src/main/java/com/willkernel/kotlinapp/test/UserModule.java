package com.willkernel.kotlinapp.test;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Modules 类里面的方法专门提供依赖，所以我们定义一个类，用@Module 注解，这样 Dagger 在构造类的实例时候，就知道从哪里去找到需要的依赖
 * {@link javax.inject.Qualifier}
 *
 * 上面也提到了，作用是用来区分不同对象的实例的。平时我们可能会对一个类创建不同的构造方法以满足不同的需求，
 * 假设现在现在 ApiSevice 有2个构造方法，根据不同情况调用不同方法。这时就要用到@Named 标签(@Named 是@Qualifier 的一种实现)
 *
 * 友情提示：我刚学习的时候就总搞不懂总以为@Scope，@Singleton，@Qualifier，@Named 是4个不同作用的操作符，其实他就是两两一对的，
 * {@link javax.inject.Named} 是@Qualifier 具体实现，@Singleton 是@Scope 的具体实现；@Scope 和@Qualifier 类似不同作用注解的关键字
 *
 * 注意：
 * module 的 provide 方法使用了 scope ，那么 component 就必须使用同一个注解
 * {@link javax.inject.Singleton} 的生命周期依附于 component，同一个 module 被不同的@Component 依赖结果也不一样 @Singleton 分为 Activity 级别单例生命周期和全局的生命周期单例
 *
 */
@Module
public class UserModule {
    private Context context;

    public UserModule() {
    }

    public UserModule(Context context) {
        this.context = context;
    }

    /**
     * 我们定义的方法是用这个注解，以此来告诉Dagger我们想要构造对象并提供这些依赖
     */
//    @Named("release")
    @PerActivity
    @Release
    @Provides
    public ApiService provideApiService() {
//        return new ApiService();
        return new ApiService(context);
    }

//    @Named("dev")
    @Dev
    @Provides
    public ApiService provideApiServiceWithUrl(String url) {
        return new ApiService(url);
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    public String provideUrl() {
        return "www.ai.com";
    }

    @Provides
    public UserManager provideUserManager(ApiService apiService, UserStore store) {
        return new UserManager(apiService, store);
    }
}

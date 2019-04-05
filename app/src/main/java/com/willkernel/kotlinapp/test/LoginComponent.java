package com.willkernel.kotlinapp.test;

import dagger.Component;
import dagger.Module;

import javax.inject.Singleton;

/**
 * 可以看到第4步我们自定义@Scope 注解 PerActivity，因为 component 的 dependencies 与 component 自身的 scope 不能相同，
 * 即组件之间的 scope 不同。所以我们自己定义
 * Singleton 的组件不能依赖其他 scope 的组件，只能其他 scope 的组件依赖 Singleton的组件
 * AppComponent 已经用@Singleton 修饰就不能再去依赖（dependencies=XXX.class）别的 Componen
 *
 * @Subcomponent 作用有些类似 Component 中的 dependencies 作用。特点：
 * Subcomponent 同时具备两种不同生命周期的 scope, SubComponent 具备了父 Component 拥有的 Scope，也具备了自己的 Scope。
 * SubComponent 的 Scope 范围小于父 Component
 * <p>
 * <p>
 * 注意事项(重要)分析
 * componet 的 inject 方法接收父类型参数，而调用时传入的是子类型对象则无法注入
 * component 关联的 modules 中不能有重复的 provide
 * module 的 provide 方法使用了 scope ，那么 component 就必须使用同一个注解
 * module 的 provide 方法没有使用 scope ，那么 component 和 module 是否加注解都无关紧要，可以通过编译
 * component 的 dependencies 与 component 自身的 scope 不能相同，即组件之间的scope 不同
 * Singleton 的组件不能依赖其他 scope 的组件，只能其他 scope 的组件依赖 Singleton的组件
 * 没有 scope 的 component 不能依赖有 scope 的 component
 * 一个 component 不能同时有多个 scope ( Subcomponent 除外)
 * @Singleton 的生命周期依附于component，同一个 module provide singleton ,不同component 也是不一样
 * Component 注入的 Activity 在其他 Component 中不能再去注入
 * dagger2 是跟着生命周期的绑定 Activity（Fragment）onDestory 对象也会销毁
 * 创建实例的方法和引用实例都不能用private修饰
 * 刚开始使用一定总会遇到很多错误，遇到错误不要着急。如果注意事项中的错误没有犯的话一定会减少很多错误
 */
//@Singleton
@PerActivity
@Component(modules = UserModule.class, dependencies = AppComponent.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}

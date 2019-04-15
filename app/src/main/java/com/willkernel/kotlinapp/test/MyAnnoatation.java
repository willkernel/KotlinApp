package com.willkernel.kotlinapp.test;

import java.lang.annotation.*;

/**
 * 自定义注解中定义成员变量的规则：
 * <p>
 * 其定义是以无形参的方法形式来声明的。即：
 * 注解方法不带参数，比如name()，website()；
 * 注解方法返回值类型：基本类型、String、Enums、Annotation以及前面这些类型的数组类型
 * 注解方法可有默认值，比如default "hello"，默认website=”hello”
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MyAnnoatation {
    String name();

    String website() default "hello";

    int revision() default 1;
}

package com.willkernel.kotlinapp.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;

/**
 * Created by willkernel
 * on 2019/4/12.
 */
public class ReflectionTest {
    public static void main(String[] args) {
        try {
            Class class1 = Class.forName("com.willkernel.kotlinapp.test.Woman");

            base(class1);

            instance(class1);

            proxy();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void proxy() {
        RealSubject realSubject = new RealSubject();
        InvocationHandler invocationHandler = new ProxyHandler(realSubject);

        Subject proxyInstance = (Subject) Proxy.newProxyInstance(realSubject.getClass().getClassLoader(), realSubject.getClass().getInterfaces(), invocationHandler);
        proxyInstance.request();

    }

    private static void instance(Class class1) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        System.out.println("通过Java反射生成并操作对象");
        Constructor constructor1 = class1.getDeclaredConstructor();
        constructor1.setAccessible(true);
        Constructor constructor2 = class1.getConstructor(String.class);
        Woman woman1 = (Woman) constructor1.newInstance();
        woman1.name = "test";

        Woman woman2 = (Woman) constructor2.newInstance("name");
        System.out.println("woman1 " + woman1);
        System.out.println("woman2 " + woman2);

        Method method = class1.getDeclaredMethod("setAge", int.class);
        method.setAccessible(true);
        method.invoke(woman1, 20);
        System.out.println(woman1);

        Field field = class1.getDeclaredField("age");
        field.setAccessible(true);
        field.setInt(woman1, 18);
        System.out.println(woman1);

        System.out.println("getAge " + field.getInt(woman1));

    }

    private static void base(Class class1) throws NoSuchFieldException, NoSuchMethodException {
        System.out.println(Arrays.toString(class1.getFields()));//获取class对象的public属性
        System.out.println(Arrays.toString(class1.getDeclaredFields()));//获取class对象的所有属性
        System.out.println(class1.getField("city"));//获取class指定的public属性


        System.out.println(".获取class对象的方法");
        System.out.println(Arrays.toString(class1.getDeclaredMethods()));//获取class对象的所有声明方法
        System.out.println(Arrays.toString(class1.getMethods()));//获取class对象的所有public方法 包括父类的方法

        System.out.println("=========================");
        System.out.println(class1.getMethod("getCity", String.class));//返回次Class对象对应类的、带指定形参列表的public方法
        System.out.println(class1.getDeclaredMethod("getCity"));//返回次Class对象对应类的、带指定形参列表的方法

        System.out.println("获取class对象的构造函数");

        System.out.println(Arrays.toString(class1.getDeclaredConstructors()));//获取class对象的所有声明构造函数
        System.out.println(Arrays.toString(class1.getConstructors()));//获取class对象的所有声明构造函数
        System.out.println(class1.getDeclaredConstructor(String.class));//获取class对象的所有声明构造函数

        System.out.println("其他方法");
        System.out.println(Arrays.toString((Annotation[]) class1.getDeclaredAnnotations()));//获取class对象的所有注解
        System.out.println(class1.getAnnotation(Deprecated.class));//获取class对象指定注解
        System.out.println(class1.getGenericSuperclass());//直接超类
        System.out.println(class1.getGenericInterfaces());//接口 type集合

//            获取class对象的信息
        boolean isPrimitive = class1.isPrimitive();//判断是否是基础类型
        boolean isArray = class1.isArray();//判断是否是集合类
        boolean isAnnotation = class1.isAnnotation();//判断是否是注解类
        boolean isInterface = class1.isInterface();//判断是否是接口类
        boolean isEnum = class1.isEnum();//判断是否是枚举类
        boolean isAnonymousClass = class1.isAnonymousClass();//判断是否是匿名内部类
        boolean isAnnotationPresent = class1.isAnnotationPresent(Deprecated.class);//判断是否被某个注解类修饰
        String className = class1.getName();//获取class名字 包含包名路径
        Package aPackage = class1.getPackage();//获取class的包信息
        String simpleName = class1.getSimpleName();//获取class类名
        int modifiers = class1.getModifiers();//获取class访问权限
        Class<?>[] declaredClasses = class1.getDeclaredClasses();//内部类
        Class<?> declaringClass = class1.getDeclaringClass();//外部类
    }

}

interface Subject {
    void request();
}

class ProxyHandler implements InvocationHandler {

    private Subject subject;

    public ProxyHandler(Subject subject) {
        this.subject = subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("invoke start");

        Object result=method.invoke(subject, args);
        System.out.println("invoke end");


        return result;
    }
}

class RealSubject implements Subject {

    @Override
    public void request() {
        System.out.println("real request");
    }
}

@Deprecated
class Woman {
    @NonNull
    String name;
    @Nullable
    public String city;
    private int age;

    public Woman(String name) {
        this.name = name;
    }

    private Woman() {
    }

    @Deprecated
    private int getAge() {
        return age - 10;
    }

    public String getName() {
        return name;
    }

    public String getCity(String city) {
        return "city: " + city;
    }

    private String getCity() {
        return city;
    }

    private void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Woman{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", age=" + age +
                '}';
    }


}

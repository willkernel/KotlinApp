package com.willkernel.kotlinapp.test;

/**
 * Created by willkernel
 * on 2019/4/15.
 */
public class JavaTest {///运行时，JVM把TestB的类信息全部放入方法区

    public static void main(String[] args) {//main方法本身是静态方法，放入方法区
        ///obj1 和 obj2都是对象引用，所以放到栈区，这个‘new Sample("xxx")’是自定义对象应该放到堆区
        Obj obj1 = new Obj("A");
        Obj obj2 = new Obj("A");
        obj1.printName();
        obj2.printName();
        //  这里，两个实例中的size成员都是int（基本类型），所以，这个“3”最终存在于栈区（而不是堆区），并供obj1和obj2共用。
        obj1.size = 3;
        obj2.size = 3;
        int A = 4;
        int B = 4;
        System.out.println(obj1.getName() == obj2.getName());
        System.out.println(obj1.size == obj2.size);
        System.out.println(obj1 == obj2);
        System.out.println(A == B);
    }
}

/**
 * 自定义类：Obj
 * 运行时，JVM把Obj的类信息全部放入方法区
 */
class Obj {
    private String name;//new出一个Obj实例后，‘name’这个引用放入了栈区，而给‘name’的赋值的是字面值"A"而不是一个newString("A")，则这个"A"会存在栈中，所以，obj1.name和obj2.name共用这个栈中的"A"
    public int size;//虽然size是基本数据类型的对象，但是它是跟随这Obj类初始化加载的，所以上面obj1和obj2两个对象的size指向的地址不同，由于此时赋予给他们的“3”在两个不同存储位置。

    public Obj(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void printName() {///printName方法本身放入方法区中
        System.out.println(this.name);
    }
}
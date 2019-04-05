package com.willkernel.kotlinapp.test;

/**
 注意事项
 接口的必要性
 可能有的同学会问，为什么要写一个 MVPView 的接口，直接把 Activity 本身传入到 Presenter 不行吗？这当然是可行的，
 这里使用接口主要是为了代码的复用，试想一下，如果直接传入 Activity，那么这个 Presenter 就只能为这一个 Activity 服务。举个例子，
 假设有个 App 已经开发完成了，可以在手机上正常使用，现在要求做平板上的适配，在平板上的界面显示效果有所变化，
 TextView 并不是直接在 Activity 中的，而是在 Fragment 里面，如果没有使用 View 的接口的话，那就需要再写一个针对 Fragment 的 Presenter，
 然后把整个过程再来一遍。但是使用 View 的接口就很简单了，直接让 Fragment 实现这个接口，然后复写接口里面的方法，
 Presenter 和 Model 层都不需要做任何改动。同理，Model 层也可以采用接口的方式来写。

 防止内存泄漏
 其实上面的代码存在内存泄漏的风险。试想一下，如果在点击 Button 之后，Model 获取到数据之前，退出了 Activity，
 此时由于 Activity 被 Presenter 引用，而 Presenter 正在进行耗时操作，会导致 Activity 的对象无法被回收，造成了内存泄漏，
 解决的方式很简单，在 Activity 退出的时候，把 Presenter 对中 View 的引用置为空即可

 这里的 BasePresenterTest 采用了泛型，为什么要这么做呢？主要是因为 Presenter 必须同时持有 View 和 Model 的引用，
 但是在底层接口中无法确定他们的类型，只能确定他们是 BaseView 和 BaseModel 的子类，所以采用泛型的方式来引用，就巧妙的解决了这个问题，
 在 BasePresenterTest 的子类中只要定义好 View 和 Model 的类型，就会自动引用他们的对象了。Presenter 中的通用的方法主要就是
 attachView 和 detachView，分别用于创建 View 对象和把 View 的对象置位空，前面已经说过，置空是为了防止内存泄漏
 */
public abstract class BasePresenterTest<V extends BaseView,M extends BaseModel> {
    protected V view;
    protected M model;

    public BasePresenterTest() {
        model=createModel();
    }

    public void attachView(V view){
        this.view=view;
    }

    public void detachView(){
        this.view=null;
    }

    public abstract M createModel();
}

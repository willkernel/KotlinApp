package com.willkernel.kotlinapp.test;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by willkernel
 * on 2019/4/1.
 */
public interface LoginContract {
    interface Model extends BaseModel{
        void getData1(CallbackT callback);
        void getData2(CallbackT callback);
        void getData3(CallbackT callback);
        Observable<String> request();
    }

    interface View extends BaseView{
        void updateUI1();
        void updateUI2();
        void updateUI3();
    }

    abstract class Presenter extends BasePresenterTest<View,Model> {
        final String TAG=this.getClass().getSimpleName();
        CompositeDisposable compositeDisposable=new CompositeDisposable();

        abstract void request1();
        abstract void request2();
        void request3(){
            model.getData3(new CallbackT() {
                @Override
                public void onResult(String text) {
                    view.show(text);
                    view.updateUI3();
                }
            });
        }
    }
}

package com.willkernel.kotlinapp.test;

import android.util.Log;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by willkernel
 * on 2019/4/1.
 */
public class LoginPresenter extends LoginContract.Presenter {
    @Override
    void request1() {
        model.getData1(new CallbackT() {
            @Override
            public void onResult(String text) {
                view.show(text);
            }
        });
    }

    @Override
    void request2() {
        Disposable disposable = model.request().subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                view.show(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
        compositeDisposable.add(disposable);
    }

    @Override
    public LoginContract.Model createModel() {
        return new LoginModel();
    }

    @Override
    public void attachView(LoginContract.View view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        super.detachView();
        compositeDisposable.clear();
    }
}

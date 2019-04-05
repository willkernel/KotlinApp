package com.willkernel.kotlinapp.test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by willkernel
 * on 2019/4/1.
 */
public class LoginModel implements LoginContract.Model {

    @Override
    public void getData1(CallbackT callback) {
        callback.onResult("login data 1");
    }

    @Override
    public void getData2(CallbackT callback) {
        callback.onResult("login data 2");
    }

    @Override
    public void getData3(CallbackT callback) {
        callback.onResult("login data 3");
    }

    @Override
    public Observable<String> request() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext("observable data");
                observableEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}

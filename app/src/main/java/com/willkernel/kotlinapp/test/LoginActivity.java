package com.willkernel.kotlinapp.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.willkernel.kotlinapp.R;

import javax.inject.Inject;


public class LoginActivity extends Activity implements LoginContract.View {
    private static final String TAG = "LoginActivity";

    //    @Release
    @Inject
    ApiService apiService1;

    //    @Release
    @Inject
    ApiService apiService2;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);

//        LoginActivity 中也引用 UserComponent 而不去引用 LogingComponent 不能保证单例
//        在 UserComponet 在 LoginActivity和 MainActivity 中会创建2个不同的实例，当然会创建2个不同的 mApiService 了。
//        如果实现全局单例就要用到自定义@Scope注解
//        DaggerUserComponent.builder().userModule(new UserModule(this)).build().inject(this);


        DaggerLoginComponent.builder().appComponent(((MyApp) getApplication()).getAppComponent()).build().inject(this);
        Log.e(TAG, "apiService " + apiService1);
        Log.e(TAG, "apiService " + apiService2);

        loginPresenter=new LoginPresenter();
        loginPresenter.attachView(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPresenter.request1();
                loginPresenter.request2();
            }
        });
    }

    @Override
    public void updateUI1() {

    }

    @Override
    public void updateUI2() {

    }

    @Override
    public void updateUI3() {

    }

    @Override
    public void show(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.detachView();
    }
}

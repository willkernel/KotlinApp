package com.willkernel.kotlinapp.test;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.willkernel.kotlinapp.R;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 为什么引入token机制呢
 * 保证安全性
 * 减小服务器压力
 * <p>
 * token的验证流程并非唯一的，至于使用怎样的验证流程可以自行确定。本文中采用token+refreshToken验证
 * 主要步骤如下：
 * <p>
 * 通过用户名和密码登录成功获取token和refreshToken并保存到本地。
 * <p>
 * token的有效期为2小时，refreshToken的有效期为15天。
 * <p>
 * 每次网络请求都需要带上token，而不必带上refreshToken。
 * <p>
 * 如果服务器端判断token过期，则返回对应的错误码，客户端判断错误码后调用刷新token接口,重新获取token和refreshToken并存储。
 * <p>
 * 如果连续15天未使用app或者用户修改了密码，则refreshToken过期，需要重新登录获取token和refreshToken
 */
public class RetrofitTestActivity extends BaseActivity {
    private static final String TAG = "RetrofitTestActivity";
    private RetrofitUtil retrofitUtil;
    EditText name, age, job;
    Button btn;
    private CompositeDisposable compositeDisposable;

    /**
     * {@link Inject} 注解的对象不能用 private 修饰
     */
//    @Named("dev")
    @Dev
    @Inject
    ApiService serviceDev;

    //    @Named("release")
//    @Release
    @Inject
    ApiService service1;

//    @Release
    @Inject
    ApiService service2;

    @Inject
    UserManager userManager;

    private boolean isDev = true;
    private Button jump2Login;

    private void daggerTest() {

//        DaggerUserComponent.create().inject(this);
//        DaggerUserComponent.builder().build().inject(this);
//        DaggerUserComponent.builder().userModule(new UserModule(this)).build().inject(this);
        DaggerUserComponent.builder().appComponent(((MyApp) getApplication()).getAppComponent()).build().inject(this);
        userManager.register();
        Log.d(TAG, "serviceDev= " + serviceDev);
//        2个对象其实是一个实例
        Log.d(TAG, "service1= " + service1);//ApiService@5979e8e
        Log.d(TAG, "service2= " + service2);//ApiService@5979e8e
        if (isDev) {
            serviceDev.register();
        } else {
            service1.register();
            service2.register();
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        retrofitTest();
        daggerTest();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rxjava;
    }

    private void initView() {

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        job = (EditText) findViewById(R.id.job);
        btn = (Button) findViewById(R.id.btn);
        jump2Login = (Button) findViewById(R.id.jump2Login);
        /*
         * 为每个EditText设置被观察者，用于发送监听事件
         * 说明：
         * 1. 此处采用了RxBinding：RxTextView.textChanges(name) = 对控件数据变更进行监听（功能类似TextWatcher）
         * 2. 传入EditText控件，点击任1个EditText撰写时，都会发送数据事件 = Function3（）的返回值（下面会详细说明）
         * 3. 采用skip(1)原因：跳过 一开始EditText无任何输入时的空值
         **/
        Observable<CharSequence> nameObservable = RxTextView.textChanges(name).skip(1);
        Observable<CharSequence> ageObservable = RxTextView.textChanges(age).skip(1);
        Observable<CharSequence> jobObservable = RxTextView.textChanges(job).skip(1);


        //第一次都为空时,不发送事件  内容都发生改变后才会发送事件
        Disposable disposable = Observable
                .combineLatest(nameObservable, ageObservable, jobObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence nameText, CharSequence ageText, CharSequence jobText) throws Exception {
                        Log.e(TAG, "nameText " + nameText);
                        Log.e(TAG, "ageText " + ageText);
                        Log.e(TAG, "jobText " + jobText);
                        boolean isUserNameValid = !TextUtils.isEmpty(nameText);
                        boolean isAgeValid = !TextUtils.isEmpty(ageText);
                        boolean isJobValid = !TextUtils.isEmpty(jobText);
                        return isUserNameValid && isAgeValid && isJobValid;
                    }
                })
//        使用Rxjava时就可以通过以下代码去管理生命周期
                .compose(bindToLifecycle())
//                .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "aBoolean " + aBoolean);
                        btn.setEnabled(aBoolean);
                        btn.setClickable(aBoolean);
                    }
                });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RetrofitTestActivity.this, "login", Toast.LENGTH_SHORT).show();
            }
        });
        jump2Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RetrofitTestActivity.this, LoginActivity.class));
            }
        });
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }

    private void retrofitTest() {
        retrofitUtil = RetrofitUtil.getInstance();
//        retrofitUtil.translate("auto", "auto", "hello world");

//        retrofitUtil.translateYd("愚人节");

        retrofitUtil.translateCiBaRxJava("I love you");

        retrofitUtil.translate2Ciba("UFO", "Money");

        retrofitUtil.mergeTest();
        retrofitUtil.zipTest();
        retrofitUtil.memoryDisk();

        File file = new File("");
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("phone", "123")
                .addFormDataPart("password", "123")
                .addFormDataPart("uploadFile", file.getName(), imageBody);
        List<MultipartBody.Part> parts = builder.build().parts();
        retrofitUtil.register(parts);


        retrofitUtil.uploadFiles("pictures", imageBody, imageBody);

        File picFile = new File("");
        RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), picFile);
        MultipartBody.Part body1 = MultipartBody.Part
                .createFormData("uploadFile", picFile.getName(), requestFile1);

        RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), picFile);
        MultipartBody.Part body2 = MultipartBody.Part
                .createFormData("uploadFile", picFile.getName(), requestFile2);
        Map<String, RequestBody> map = new HashMap<>();
        map.put("文件1", requestFile1);
        map.put("文件2", requestFile2);
        retrofitUtil.uploadFiles("pic", map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}

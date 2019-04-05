package com.willkernel.kotlinapp.test;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created by willkernel
 * on 2019/3/31.
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init(savedInstanceState);
    }

    protected abstract void init(Bundle savedInstanceState);

    protected abstract int getLayoutId();
}

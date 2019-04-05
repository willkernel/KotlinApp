package com.willkernel.kotlinapp.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.trello.rxlifecycle2.components.RxFragment;

/**
 * Created by willkernel
 * on 2019/3/31.
 */
public abstract class BaseFragment extends RxFragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(this.getLayoutId(), container, false);
            init(savedInstanceState);
        }
        ViewGroup viewGroup= (ViewGroup) rootView.getParent();
        if(viewGroup!=null){
            viewGroup.removeView(rootView);
        }
        return rootView;
    }

    protected abstract int getLayoutId();

    protected abstract void init(Bundle savedInstanceState);
}

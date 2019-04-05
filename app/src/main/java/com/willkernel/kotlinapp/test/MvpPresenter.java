package com.willkernel.kotlinapp.test;

import androidx.annotation.UiThread;

/**
 * Created by willkernel
 * on 2019/4/1.
 */
public interface MvpPresenter<V extends MvpView> {
    /**
     * Set or attach the view to this presenter
     */
    @UiThread
    void attachView(V v);

    /**
     * Will be called if the view has been destroyed. Typically this method will be invoked from
     * <code>Activity.detachView()</code> or <code>Fragment.onDestroyView()</code>
     */
    @UiThread
    void detachView(boolean retainInstance);
}

package com.willkernel.kotlinapp.base

import androidx.annotation.NonNull
import com.willkernel.kotlinapp.mvp.contract.SearchContract
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by willkernel
 * on 2019/4/2.
 */

abstract class BasePresenter<V : IBaseView> : IBasePresenter<V> {
    protected var TAG=javaClass.simpleName
    var mRootView: V? = null
        private set

    override fun subscribe(v: V) {
        mRootView = v
    }

    @NonNull
    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        mCompositeDisposable.clear()
    }

    fun addSubscription(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    abstract fun result(requestCode: Int, resultCode: Int)

    private val isViewAttached: Boolean
        get() = mRootView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }
    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")

}
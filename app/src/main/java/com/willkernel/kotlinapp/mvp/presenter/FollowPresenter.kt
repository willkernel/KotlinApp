package com.willkernel.kotlinapp.mvp.presenter

import com.willkernel.kotlinapp.base.BasePresenter
import com.willkernel.kotlinapp.mvp.contract.FollowContract
import com.willkernel.kotlinapp.mvp.model.FollowModel
import com.willkernel.kotlinapp.net.exception.ExceptionHandle

class FollowPresenter : BasePresenter<FollowContract.View>(), FollowContract.Presenter {
    override fun result(requestCode: Int, resultCode: Int) {
    }

    private val followModel by lazy { FollowModel() }

    private var nextPageUrl:String?=null

    /**
     *  请求关注数据
     */
    override fun requestFollowList() {
        checkViewAttached()
        mRootView?.showLoading()
        val disposable = followModel.requestFollowList()
            .subscribe({ issue ->
                mRootView?.apply {
                    dismissLoading()
                    nextPageUrl = issue.nextPageUrl
                    setFollowInfo(issue)
                }
            }, { throwable ->
                mRootView?.apply {
                    //处理异常
                    showError(ExceptionHandle.handleException(throwable),ExceptionHandle.errorCode)
                }
            })
        addSubscription(disposable)
    }

    /**
     * 加载更多
     */
    override fun loadMoreData(){
        val disposable = nextPageUrl?.let {
            followModel.loadMoreData(it)
                .subscribe({ issue->
                    mRootView?.apply {
                        nextPageUrl = issue.nextPageUrl
                        setFollowInfo(issue)
                    }

                },{ t ->
                    mRootView?.apply {
                        showError(ExceptionHandle.handleException(t),ExceptionHandle.errorCode)
                    }
                })


        }
        if (disposable != null) {
            addSubscription(disposable)
        }
    }
}
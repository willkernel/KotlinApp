package com.willkernel.kotlinapp.mvp.presenter

import android.util.Log
import com.willkernel.kotlinapp.base.BasePresenter
import com.willkernel.kotlinapp.mvp.contract.SearchContract
import com.willkernel.kotlinapp.mvp.model.SearchModel
import com.willkernel.kotlinapp.net.exception.ExceptionHandle
import com.willkernel.kotlinapp.ui.activity.SearchActivity


/**
 * Created by willkernel
 * on 2019/4/2.
 */
class SearchPresenter : BasePresenter<SearchContract.View>(), SearchContract.Presenter {
    private val mSearchModel: SearchModel by lazy { SearchModel() }
    private var nextPageUrl: String? = null

    override fun requestHotWordData() {
        checkViewAttached()
        mRootView?.apply {
            closeSoftKeyboard()
            showLoading()
        }
        addSubscription(
            mSearchModel.requestHotWord()
                .subscribe({
                    mRootView?.apply {
                        setHotWordData(it)
                    }
                }, {
                    mRootView?.apply {
                        showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)
                    }
                })
        )
    }

    override fun querySearchData(words: String) {
        checkViewAttached()
        mRootView?.apply {
            closeSoftKeyboard()
            showLoading()
            (this as SearchActivity).clearSearchResult()
        }
        addSubscription(
            mSearchModel.queryWords(words)
                .subscribe({
                    mRootView?.apply {
                        dismissLoading()
                        if (it.count > 0 && it.itemList.size > 0) {
                            nextPageUrl = it.nextPageUrl
                            setSearchResult(it)
                        } else {
                            setEmptyView()
                        }
                    }
                }, {
                    mRootView?.apply {
                        dismissLoading()
                        showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)
                    }
                })
        )
    }

    override fun result(requestCode: Int, resultCode: Int) {
    }

    override fun loadMoreData() {
        Log.e(TAG, "loadMoreData")
        checkViewAttached()
        nextPageUrl?.let {
            addSubscription(
                mSearchModel.loadMoreData(it).subscribe({ issue ->
                    mRootView?.apply {
                        nextPageUrl = issue.nextPageUrl
                        setSearchResult(issue)
                    }
                }, {
                    mRootView?.apply {
                        showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)
                    }
                })
            )
        }
    }
}
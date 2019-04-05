package com.willkernel.kotlinapp.mvp.presenter

import com.willkernel.kotlinapp.base.BasePresenter
import com.willkernel.kotlinapp.mvp.contract.CategoryContract
import com.willkernel.kotlinapp.mvp.model.CategoryModel
import com.willkernel.kotlinapp.net.exception.ExceptionHandle

class CategoryPresenter : BasePresenter<CategoryContract.View>(), CategoryContract.Presenter {
    override fun result(requestCode: Int, resultCode: Int) {
    }

    private val categoryModel: CategoryModel by lazy {
        CategoryModel()
    }

    /**
     * 获取分类
     */
    override fun getCategoryData() {
        checkViewAttached()
        mRootView?.showLoading()
        val disposable = categoryModel.getCategoryData()
            .subscribe({ categoryList ->
                mRootView?.apply {
                    dismissLoading()
                    showCategory(categoryList)
                }
            }, { t ->
                mRootView?.apply {
                    dismissLoading()
                    //处理异常
                    showError(ExceptionHandle.handleException(t),ExceptionHandle.errorCode)
                }

            })

        addSubscription(disposable)
    }
}
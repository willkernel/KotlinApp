package com.willkernel.kotlinapp.mvp.contract

import com.willkernel.kotlinapp.base.IBasePresenter
import com.willkernel.kotlinapp.base.IBaseView
import com.willkernel.kotlinapp.mvp.model.CategoryBean

interface CategoryContract {
    interface View : IBaseView {
        /**
         * 显示分类的信息
         */
        fun showCategory(categoryList: ArrayList<CategoryBean>)

    }

    interface Presenter:IBasePresenter<View>{
        /**
         * 获取分类的信息
         */
        fun getCategoryData()
    }
}

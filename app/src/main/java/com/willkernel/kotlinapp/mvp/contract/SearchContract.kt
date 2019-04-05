package com.willkernel.kotlinapp.mvp.contract

import com.willkernel.kotlinapp.base.IBasePresenter
import com.willkernel.kotlinapp.base.IBaseView
import com.willkernel.kotlinapp.mvp.model.HomeBean
import java.util.ArrayList

interface SearchContract {

    interface View : IBaseView {
        /**
         * 设置热门关键词数据
         */
        fun setHotWordData(datas: ArrayList<String>)

        /**
         * 设置搜索关键词返回的结果
         */
        fun setSearchResult(issue: HomeBean.Issue)
        /**
         * 关闭软件盘
         */
        fun closeSoftKeyboard()

        /**
         * 设置空 View
         */
        fun setEmptyView()

    }


    interface Presenter : IBasePresenter<View> {
        /**
         * 获取热门关键字的数据
         */
        fun requestHotWordData()

        /**
         * 查询搜索
         */
        fun querySearchData(words:String)

        /**
         * 加载更多
         */
        fun loadMoreData()
    }
}
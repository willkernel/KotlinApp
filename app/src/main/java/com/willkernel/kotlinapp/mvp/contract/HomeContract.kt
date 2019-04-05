package com.willkernel.kotlinapp.mvp.contract

import com.willkernel.kotlinapp.base.IBaseView
import com.willkernel.kotlinapp.mvp.model.HomeBean

/**
 * Created by willkernel
 * on 2019/4/2.
 */
interface HomeContract {
    interface View : IBaseView {
        /**
         * 设置第一次请求的数据
         */
        fun setHomeData(homeBean: HomeBean)

        /**
         * 设置加载更多的数据
         */
        fun setMoreData(itemList: ArrayList<HomeBean.Issue.Item>)
    }

    interface Presenter {
        /**
         * 获取首页精选数据
         */
        fun requestHomeData(num: Int)

        /**
         * 加载更多数据
         */
        fun loadMoreData()
    }
}
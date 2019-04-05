package com.willkernel.kotlinapp.mvp.contract

import com.willkernel.kotlinapp.base.IBasePresenter
import com.willkernel.kotlinapp.base.IBaseView
import com.willkernel.kotlinapp.mvp.model.HomeBean

interface FollowContract {
    interface View : IBaseView {
        /**
         * 设置关注信息数据
         */
        fun setFollowInfo(issue: HomeBean.Issue)
    }


    interface Presenter : IBasePresenter<View> {
        /**
         * 获取List
         */
        fun requestFollowList()

        /**
         * 加载更多
         */
        fun loadMoreData()
    }

}

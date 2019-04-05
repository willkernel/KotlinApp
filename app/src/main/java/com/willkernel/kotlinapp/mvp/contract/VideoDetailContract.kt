package com.willkernel.kotlinapp.mvp.contract

import com.willkernel.kotlinapp.base.IBasePresenter
import com.willkernel.kotlinapp.base.IBaseView
import com.willkernel.kotlinapp.mvp.model.HomeBean

class VideoDetailContract {
    interface View :IBaseView{

        /**
         * 设置视频播放源
         */
        fun setVideo(url: String)

        /**
         * 设置视频信息
         */
        fun setVideoInfo(itemInfo: HomeBean.Issue.Item)

        /**
         * 设置背景
         */
        fun setBackground(url: String)

        /**
         * 设置最新相关视频
         */
        fun setRecentRelatedVideo(itemList: ArrayList<HomeBean.Issue.Item>)

        /**
         * 设置错误信息
         */
        fun setErrorMsg(errorMsg: String)
    }
    interface Presenter : IBasePresenter<View> {

        /**
         * 加载视频信息
         */
        fun loadVideoInfo(itemInfo: HomeBean.Issue.Item)

        /**
         * 请求相关的视频数据
         */
        fun requestRelatedVideo(id: Long)

    }
}

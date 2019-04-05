package com.willkernel.kotlinapp.mvp.presenter

import android.app.Activity
import android.util.Log
import com.willkernel.kotlinapp.base.BasePresenter
import com.willkernel.kotlinapp.dataFormat
import com.willkernel.kotlinapp.mvp.contract.VideoDetailContract
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.mvp.model.VideoDetailModel
import com.willkernel.kotlinapp.net.exception.ExceptionHandle
import com.willkernel.kotlinapp.showToast
import com.willkernel.kotlinapp.utils.DisplayManager
import com.willkernel.kotlinapp.utils.NetworkUtils

class VideoDetailPresenter : BasePresenter<VideoDetailContract.View>(), VideoDetailContract.Presenter {
    private val mVideoDetailModel: VideoDetailModel by lazy { VideoDetailModel() }
    override fun requestRelatedVideo(id: Long) {
        Log.e(TAG, "requestRelatedVideo $id")
        mRootView?.showLoading()
        mVideoDetailModel.requestReleatedVideo(id).subscribe({
            mRootView?.apply {
                dismissLoading()
                mRootView?.setRecentRelatedVideo(it.itemList)
            }
        }, {
            mRootView?.apply {
                dismissLoading()
                showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)
            }
        })

    }

    /**
     * 加载视频相关的数据
     */
    override fun loadVideoInfo(itemInfo: HomeBean.Issue.Item) {
        Log.e(TAG, "loadVideoInfo $itemInfo")
        val playInfo = itemInfo.data?.playInfo
        val netType = NetworkUtils.isWifi()
        checkViewAttached()

        if (playInfo!!.size > 1) {
            // 当前网络是 Wifi环境下选择高清的视频
            if (netType) {
                for (info in playInfo) {
                    if (info.type == "high") {
                        val playUrl = info.url
                        mRootView?.setVideo(playUrl)
                        break
                        //不能使用foreeach+ return 程序逻辑错误
                    }
                }
            } else {
                //否则就选标清的视频
                for (info in playInfo) {
                    if (info.type == "normal") {
                        val playUrl = info.url
                        mRootView?.setVideo(playUrl)
                        (mRootView as Activity).showToast(
                            "本次消耗${(mRootView as Activity)
                                .dataFormat(info.urlList[0].size)}流量"
                        )
                        break
                    }
                }
            }
        } else {
            mRootView?.setVideo(itemInfo.data.playUrl)
        }

        //http://img.kaiyanapp.com/7f94ce329b6f78cd6fea4997cbdaca8d.jpeg?imageMogr2/quality/60/format/jpg/thumbnail/1024x768
        val backgroundUrl =
            itemInfo.data.cover.blurred + "/thumbnail/${DisplayManager.getScreenHeight()!! - DisplayManager.dip2px(250f)!!}x${DisplayManager.getScreenWidth()}"
        backgroundUrl.let { mRootView?.setBackground(backgroundUrl) }
        mRootView?.setVideoInfo(itemInfo)
    }

    override fun result(requestCode: Int, resultCode: Int) {
    }

}

package com.willkernel.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.willkernel.kotlinapp.net.RetrofitManager
import io.reactivex.Observable

/**
 * Created by willkernel
 * on 2019/4/5.
 */
class VideoDetailModel {
    fun requestReleatedVideo(id: Long): Observable<HomeBean.Issue> {
        return RetrofitManager.service.getReleatedData(id)
            .compose(SchedulerUtils.ioToMain())
    }
}
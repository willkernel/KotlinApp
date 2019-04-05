package com.willkernel.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.willkernel.kotlinapp.net.RetrofitManager
import io.reactivex.Observable


class HomeModel {
    fun requestHomeData(num: Int): Observable<HomeBean> {
        return RetrofitManager.service.getFirstHomeData(num)
            .compose(SchedulerUtils.ioToMain())
    }

    fun loadMoreData(url: String): Observable<HomeBean> {
        return RetrofitManager.service.getMoreHomeData(url)
            .compose(SchedulerUtils.ioToMain())
    }
}
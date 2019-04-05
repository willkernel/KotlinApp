package com.willkernel.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.willkernel.kotlinapp.net.RetrofitManager
import io.reactivex.Observable

class FollowModel {

    /**
     * 获取关注信息
     */
    fun requestFollowList(): Observable<HomeBean.Issue> {

        return RetrofitManager.service.getFollowInfo()
            .compose(SchedulerUtils.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url:String): Observable<HomeBean.Issue> {
        return RetrofitManager.service.getIssueData(url)
            .compose(SchedulerUtils.ioToMain())
    }


}

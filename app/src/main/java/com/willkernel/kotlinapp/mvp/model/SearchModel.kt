package com.willkernel.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.willkernel.kotlinapp.net.RetrofitManager
import io.reactivex.Observable

/**
 * Created by willkernel
 * on 2019/4/4.
 */
class SearchModel {
    fun requestHotWord(): Observable<ArrayList<String>> {
        return RetrofitManager.service.getHotWord()
            .compose(SchedulerUtils.ioToMain())
    }

    fun queryWords(words:String): Observable<HomeBean.Issue> {
        return RetrofitManager.service.getSearchData(words)
            .compose(SchedulerUtils.ioToMain())
    }

    fun loadMoreData(url: String): Observable<HomeBean.Issue> {
        return RetrofitManager.service.getIssueData(url)
            .compose(SchedulerUtils.ioToMain())
    }
}
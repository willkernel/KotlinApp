package com.willkernel.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.willkernel.kotlinapp.net.RetrofitManager
import io.reactivex.Observable

class CategoryModel {
    /**
     * 获取分类信息
     */
    fun getCategoryData(): Observable<ArrayList<CategoryBean>> {
        return RetrofitManager.service.getCategory()
            .compose(SchedulerUtils.ioToMain())
    }
}

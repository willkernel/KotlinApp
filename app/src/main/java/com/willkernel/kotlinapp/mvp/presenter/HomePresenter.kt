package com.willkernel.kotlinapp.mvp.presenter

import android.os.Build.VERSION_CODES.P
import android.util.Log
import com.willkernel.kotlinapp.base.BasePresenter
import com.willkernel.kotlinapp.mvp.contract.HomeContract
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.mvp.model.HomeModel
import com.willkernel.kotlinapp.net.exception.ExceptionHandle
import com.willkernel.kotlinapp.ui.fragment.HomeFragment

/**
 * Created by willkernel
 * on 2019/4/2.
 */
class HomePresenter : BasePresenter<HomeContract.View>(), HomeContract.Presenter {

    private val mHomeModel: HomeModel by lazy { HomeModel() }
    private var bannerHomeBean: HomeBean? = null
    private var nextPageUrl: String? = null

    override fun result(requestCode: Int, resultCode: Int) {
    }

    /**获取首页精选数据 banner 加 一页数据*/
    override fun requestHomeData(num: Int) {
        checkViewAttached()

        addSubscription(
            mHomeModel.requestHomeData(num)
                .flatMap { homeBean ->
                    val bannerItemList = homeBean.issueList[0].itemList
                    Log.e(TAG, "homeBean $homeBean")
                    Log.e(TAG, "bannerItemList $bannerItemList")
                    bannerItemList.filter { item ->
                        item.type == "banner2" || item.type == "horizontalScrollCard"
                    }.forEach { item ->
                        bannerItemList.remove(item)
                    }
                    Log.e(TAG, "bannerItemList remove $bannerItemList")
                    Log.e(TAG, "homeBean remove $homeBean")
                    bannerHomeBean = homeBean

                    mHomeModel.loadMoreData(homeBean.nextPageUrl)
                }
                .subscribe({ homeBean ->
                    mRootView?.apply {
                        nextPageUrl = homeBean.nextPageUrl
                        //过滤掉 Banner2(包含广告,等不需要的 Type)
                        val newBannerItemList = homeBean.issueList[0].itemList
                        newBannerItemList.filter { item ->
                            item.type == "banner2" || item.type == "horizontalScrollCard"
                        }.forEach { item ->
                            newBannerItemList.remove(item)
                        }

                        bannerHomeBean?.issueList!![0].count = bannerHomeBean!!.issueList[0].itemList.size
                        bannerHomeBean?.issueList!![0].itemList.addAll(newBannerItemList)
                        Log.e(TAG, "newBannerItemList  $newBannerItemList")
                        Log.e(TAG, "bannerHomeBean  $bannerHomeBean")
                        setHomeData(bannerHomeBean!!)
                        dismissLoading()
                    }
                }, {
                    mRootView?.apply {
                        dismissLoading()
                        showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)
                    }
                })
        )
    }

    override fun loadMoreData() {
        nextPageUrl?.let {
            addSubscription(
                mHomeModel.loadMoreData(it)
                    .subscribe({ homeBean ->
                        mRootView?.apply {
                            nextPageUrl = homeBean.nextPageUrl
                            val newBannerList = homeBean.issueList[0].itemList
                            newBannerList.filter { item ->
                                item.type == "banner2" || item.type == "horizontalScrollCard"
                            }.forEach { item ->
                                newBannerList.remove(item)
                            }
                            Log.e(TAG, "loadMoreList  $newBannerList")
                            setMoreData(newBannerList)
                        }
                    }, {
                        mRootView?.apply {
                            showError(ExceptionHandle.handleException(it), ExceptionHandle.errorCode)
                        }
                    })
            )
        }
    }
}
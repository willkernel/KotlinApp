package com.willkernel.kotlinapp.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.willkernel.kotlinapp.Constants
import com.willkernel.kotlinapp.MyApplication
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseActivity
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.ui.adapter.WatchHistoryAdapter
import com.willkernel.kotlinapp.utils.WatchHistoryUtils
import kotlinx.android.synthetic.main.layout_watch_history.*
import java.util.*

class WatchHistoryActivity : BaseActivity() {
    private var itemListData = ArrayList<HomeBean.Issue.Item>()

    companion object {
        private const val HISTORY_MAX = 20
    }


    override fun initData() {
        multipleStatusView_history.showLoading()
        itemListData = queryWatchHistory()
    }

    private fun queryWatchHistory(): ArrayList<HomeBean.Issue.Item> {
        val watchList = ArrayList<HomeBean.Issue.Item>()
        val hisAll = WatchHistoryUtils.getAll(Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context) as Map<*, *>
        //将key排序升序
        val keys = hisAll.keys.toTypedArray()
        Arrays.sort(keys)
        val keyLength = keys.size
        //这里计算 如果历史记录条数是大于 可以显示的最大条数，则用最大条数做循环条件，防止历史记录条数-最大条数为负值，数组越界
        val hisLength = Math.min(HISTORY_MAX, keyLength)
        // 反序列化和遍历 添加观看的历史记录
        (1..hisLength).mapTo(watchList) {
            WatchHistoryUtils.getObject(
                Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context,
                keys[keyLength - it] as String
            ) as HomeBean.Issue.Item
        }

        return watchList
    }

    override fun initView() {
        //返回
        toolbar_history.setNavigationOnClickListener { finish() }

        val mAdapter = WatchHistoryAdapter(this, itemListData, R.layout.item_video_small_card)
        mRecyclerView_history.adapter = mAdapter
        mRecyclerView_history.layoutManager = LinearLayoutManager(this)

        if (itemListData.size > 0) {
            multipleStatusView_history.showContent()
        } else {
            multipleStatusView_history.showEmpty()
        }
    }

    override fun initListener() {

    }

    override fun getLayoutId(): Int = R.layout.layout_watch_history

}

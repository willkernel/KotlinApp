package com.willkernel.kotlinapp.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import com.willkernel.kotlinapp.Constants.Companion.ARGUMENTS_TITLE
import com.willkernel.kotlinapp.Constants.Companion.HOME_PAGE
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseFragment
import com.willkernel.kotlinapp.mvp.contract.HomeContract
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.mvp.presenter.HomePresenter
import com.willkernel.kotlinapp.ui.activity.SearchActivity
import com.willkernel.kotlinapp.ui.adapter.HomeAdapter
import com.willkernel.kotlinapp.utils.AppUtils
import com.willkernel.kotlinapp.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : BaseFragment(), HomeContract.View {

    private val mPresenter by lazy {
        Log.e(TAG, "mPresenter lazy")
        HomePresenter()
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }
    private val simpleDateFormat by lazy {
        SimpleDateFormat("- MMM. dd, 'Brunch' -", Locale.ENGLISH)
    }

    private var mHomeAdapter: HomeAdapter? = null
    private var isRefreshing = false
    private var loadingMore = false
    private var mTitle: String? = null
    private var mMaterialHeader: MaterialHeader? = null

    private var mContext: Context? = null

    init {
        Log.e(TAG, "init()")
        mContext = context
        mTitle = arguments?.getString(ARGUMENTS_TITLE)
        mPresenter.subscribe(this)
    }

    companion object {
        const val ID = 0
        fun newInstance(title: String): HomeFragment {
            Log.e("HomeFragment", "newInstance")
            val bundle = Bundle()
            bundle.putString(ARGUMENTS_TITLE, title)
            val fragment = HomeFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun initView() {
        Log.e(TAG, "mPresenter=$mPresenter")
//        mPresenter.subscribe(this)
        mLayoutStatusView = multipleStatusView
//        mMaterialHeader=mRefreshLayout.refreshHeader as MaterialHeader
//        //打开下拉刷新区域块背景
//        mMaterialHeader?.setShowBezierWave(true)
//        mRefreshLayout.setPrimaryColorsId(R.color.app_color_theme_1,R.color.app_color_theme_4)

        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            isRefreshing = true
            mPresenter.requestHomeData(HOME_PAGE)
        }
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val childCount = mRecyclerView.childCount
                    val itemCount = mRecyclerView.layoutManager?.itemCount
                    val firstVisibleItem =
                        (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    Log.e(TAG, "childCount=$childCount itemCount=$itemCount firstVisibleItem=$firstVisibleItem")
                    if (firstVisibleItem + childCount == itemCount) {
                        if (!loadingMore) {
                            Log.e(TAG, "childCount loading more")
                            loadingMore = true
                            mPresenter.loadMoreData()
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItem == 0) {
                    toolbar.setBackgroundColor(getColor(R.color.color_translucent))
                    iv_search.setImageResource(R.mipmap.ic_action_search_white)
                    tv_header_title_home.text = ""
                } else {
                    if (mHomeAdapter?.mData!!.size > 1) {
                        toolbar.setBackgroundColor(getColor(R.color.color_title_bg))
                        iv_search.setImageResource(R.mipmap.ic_action_search_black)
                        val itemList = mHomeAdapter!!.mData
                        val item = itemList[firstVisibleItem + mHomeAdapter!!.bannerItemSize - 1]
                        Log.e(TAG, "currentVisibleItem $firstVisibleItem")
                        Log.e(TAG, " mHomeAdapter!!.bannerItemSize ${mHomeAdapter!!.bannerItemSize}")
                        Log.e(TAG, "position " + (firstVisibleItem + mHomeAdapter!!.bannerItemSize - 1))
                        Log.e(TAG, "item =$item")
                        if (item.type == "textHeader") {
                            tv_header_title_home.text = item.data?.text
                        } else {
                            tv_header_title_home.text = "test"
//                            tv_header_title.text = simpleDateFormat.format(item.data?.date)
                        }
                    }
                }
            }
        })
        iv_search.setOnClickListener {
            openSearchActivity()
        }
        activity?.let {
            StatusBarUtil.darkMode(it)
            StatusBarUtil.setPaddingSmart(it,toolbar)
        }
    }

    private fun openSearchActivity() {
        if (AppUtils.isOverBuildVersion(Build.VERSION_CODES.LOLLIPOP)) {
            val options = activity?.let {
                ActivityOptionsCompat.makeSceneTransitionAnimation(it, iv_search, iv_search.transitionName)
            }
            startActivity(Intent(activity, SearchActivity::class.java), options?.toBundle())
        } else {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
    }

    override fun lazyLoad() {
        mPresenter.requestHomeData(HOME_PAGE)
    }

    override fun getLayoutId() = R.layout.fragment_home

    override fun setHomeData(homeBean: HomeBean) {
        Logger.d(homeBean)

        mLayoutStatusView?.showContent()
        mHomeAdapter = activity?.let { HomeAdapter(it, homeBean.issueList[0].itemList) }
        mHomeAdapter?.setBannerSize(homeBean.issueList[0].count)

        mRecyclerView.adapter = mHomeAdapter
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun setMoreData(itemList: ArrayList<HomeBean.Issue.Item>) {
        loadingMore = false
        mHomeAdapter?.addItemData(itemList)
    }

    override fun showLoading() {
        if (!isRefreshing) {
            isRefreshing = true
            mLayoutStatusView?.showLoading()
        }
    }

    override fun dismissLoading() {
        isRefreshing = false
        mRefreshLayout.finishRefresh()
    }

    override fun showError(msg: String, errCode: Int) {
    }

    fun getColor(colorId: Int): Int {
        return resources.getColor(colorId, null)
    }

    override fun onDestroyView() {
        mPresenter.unSubscribe()
        super.onDestroyView()
    }
}
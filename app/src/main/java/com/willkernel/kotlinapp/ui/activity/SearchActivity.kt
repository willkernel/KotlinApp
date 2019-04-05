package com.willkernel.kotlinapp.ui.activity

import android.graphics.Typeface
import android.os.Build
import android.transition.*
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.willkernel.kotlinapp.MyApplication
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseActivity
import com.willkernel.kotlinapp.mvp.contract.SearchContract
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.mvp.presenter.SearchPresenter
import com.willkernel.kotlinapp.showToast
import com.willkernel.kotlinapp.ui.adapter.CategoryDetailAdapter
import com.willkernel.kotlinapp.ui.adapter.HotWordsAdapter
import com.willkernel.kotlinapp.utils.AppUtils
import com.willkernel.kotlinapp.utils.CleanLeakUtils
import com.willkernel.kotlinapp.view.MultipleStatusView
import com.willkernel.kotlinapp.view.ViewAnimUtils
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_home.*

class SearchActivity : BaseActivity(), SearchContract.View {
    private val mPresenter: SearchPresenter by lazy { SearchPresenter() }
    private var mTypeface: Typeface? = null
    override fun getLayoutId() = R.layout.activity_search
    private var mItemList = ArrayList<HomeBean.Issue.Item>()
    private var mLayoutStatusView: MultipleStatusView? = null
    private var mKeyWords: String? = null
    private var loadingMore = false
    private val mResultAdapter by lazy { CategoryDetailAdapter(this, mItemList, R.layout.item_category_detail) }
    private lateinit var mHotWordsAdapter: HotWordsAdapter

    init {
        mPresenter.subscribe(this)
        mTypeface = Typeface.createFromAsset(MyApplication.context.assets, "fonts/FZLanTingHeiS-L-GB-Regular.TTF")
    }

    override fun setHotWordData(datas: ArrayList<String>) {
        showHotWordView()
        mHotWordsAdapter = HotWordsAdapter(this, datas, R.layout.item_hot_text)

        val flexBoxLayoutManager = FlexboxLayoutManager(this)
        flexBoxLayoutManager.flexWrap = FlexWrap.WRAP
        flexBoxLayoutManager.flexDirection = FlexDirection.ROW
        flexBoxLayoutManager.alignItems = AlignItems.CENTER
        flexBoxLayoutManager.justifyContent = JustifyContent.FLEX_START

        mRecyclerView_hot.layoutManager = flexBoxLayoutManager
        mRecyclerView_hot.adapter = mHotWordsAdapter
        mHotWordsAdapter.setOnTagItemClickListener {
            mKeyWords = it
            Log.e(TAG, "keywords $it")
            mPresenter.querySearchData(it)
        }
    }

    override fun setSearchResult(issue: HomeBean.Issue) {
        loadingMore = false

        hideHotWordView()
        tv_search_count.visibility = View.VISIBLE
        tv_search_count.text = String.format(getString(R.string.search_result_count), mKeyWords, issue.total)

        mItemList = issue.itemList
        mResultAdapter.addData(issue.itemList)
    }

    override fun closeSoftKeyboard() {
        AppUtils.closeKeyBord(et_search_view)
    }

    override fun initData() {
        if (AppUtils.isOverBuildVersion(Build.VERSION_CODES.LOLLIPOP)) {
            setUpEnterAnim()
//            setUpExitAnim()
        } else {
            setUpAnim()
        }
    }

    private fun setUpEnterAnim() {
        val transition = TransitionInflater.from(this)
            .inflateTransition(R.transition.arc_motion)
        window.sharedElementEnterTransition = transition
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                transition?.removeListener(this)
                animateRevelShow()
            }

            override fun onTransitionResume(transition: Transition?) {
            }

            override fun onTransitionPause(transition: Transition?) {
            }

            override fun onTransitionCancel(transition: Transition?) {
            }

            override fun onTransitionStart(transition: Transition?) {
            }
        })
    }

    private fun animateRevelShow() {
        ViewAnimUtils.animateRevealShow(this, rel_frame, fab_circle.width / 2, R.color.backgroundColor,
            object : ViewAnimUtils.OnRevealAnimationListener {
                override fun onRevealHide() {
                }

                override fun onRevealShow() {
                    setUpAnim()
                }
            })
    }

    private fun animateRevelHide() {
        val anim = AnimationUtils.loadAnimation(this@SearchActivity, R.anim.anim_out)
        anim.duration = 100
        rel_container.startAnimation(anim)
        rel_container.visibility = View.GONE

        ViewAnimUtils.animateRevealHide(this, rel_frame, fab_circle.width / 2, R.color.backgroundColor,
            object : ViewAnimUtils.OnRevealAnimationListener {
                override fun onRevealHide() {
                    defaultBackPressed()
                }

                override fun onRevealShow() {
                }
            })
    }

    private fun setUpExitAnim() {
        val fade = Explode()
        fade.duration = 200
        window.returnTransition = fade
    }

    private fun setUpAnim() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_in)
        anim.duration = 300
        rel_container.startAnimation(anim)
        rel_container.visibility = View.VISIBLE
        AppUtils.openKeyBord(et_search_view)
        et_search_view.setSelection(0)
    }


    override fun initView() {
        tv_title_tip.typeface = mTypeface
        tv_hot_search_words.typeface = mTypeface
        mLayoutStatusView = multipleStatusViewSearch
        mRecyclerView_result.layoutManager = LinearLayoutManager(this)
        mRecyclerView_result.adapter = mResultAdapter

        mRecyclerView_result.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val itemCount = mRecyclerView_result.layoutManager?.itemCount
                    val lastVisibleItem =
                        (mRecyclerView_result.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (!loadingMore && lastVisibleItem == itemCount?.minus(1)) {
                        loadingMore = true
                        mPresenter.loadMoreData()
                    }
                }
            }
        })
        et_search_view.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeSoftKeyboard()
                    mKeyWords = et_search_view.text.toString().trim()
                    if (mKeyWords.isNullOrEmpty()) {
                        showToast("input words")
                        mPresenter.requestHotWordData()
                    } else {
                        mPresenter.querySearchData(mKeyWords!!)
                    }
                }
                return false
            }
        })
        tv_cancel.setOnClickListener { onBackPressed() }

        mPresenter.requestHotWordData()
    }

    fun clearSearchResult() {
        mResultAdapter.clearData()
    }

    override fun initListener() {
    }

    override fun showLoading() {
        mLayoutStatusView?.showLoading()
    }

    override fun dismissLoading() {
        mLayoutStatusView?.showContent()
    }

    override fun onBackPressed() {
        if (AppUtils.isOverBuildVersion(Build.VERSION_CODES.LOLLIPOP)) {
            animateRevelHide()
        } else {
            defaultBackPressed()
        }
    }

    private fun defaultBackPressed() {
        closeSoftKeyboard()
        super.onBackPressed()
    }

    override fun showError(msg: String, errCode: Int) {
        showToast(msg)
    }

    /**
     * 没有找到相匹配的内容
     */
    override fun setEmptyView() {
        showToast("抱歉，没有找到相匹配的内容")
        hideHotWordView()
        tv_search_count.visibility = View.GONE
        mLayoutStatusView?.showEmpty()
    }

    /**
     * 隐藏热门关键字的 View
     */
    private fun hideHotWordView() {
        layout_hot_words.visibility = View.GONE
        layout_content_result.visibility = View.VISIBLE
    }

    /**
     * 显示热门关键字的 流式布局
     */
    private fun showHotWordView() {
        layout_hot_words.visibility = View.VISIBLE
        layout_content_result.visibility = View.GONE
    }

    override fun onDestroy() {
        CleanLeakUtils.fixInputMethodManagerLeak(this)
        mPresenter.unSubscribe()
        mTypeface = null
        super.onDestroy()
    }
}

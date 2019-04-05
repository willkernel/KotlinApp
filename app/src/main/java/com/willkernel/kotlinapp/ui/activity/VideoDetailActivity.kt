package com.willkernel.kotlinapp.ui.activity

import android.content.res.Configuration
import android.os.Build
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.orhanobut.logger.Logger
import com.shuyu.gsyvideoplayer.listener.LockClickListener
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.willkernel.kotlinapp.Constants
import com.willkernel.kotlinapp.MyApplication
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseActivity
import com.willkernel.kotlinapp.mvp.contract.VideoDetailContract
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.mvp.presenter.VideoDetailPresenter
import com.willkernel.kotlinapp.showToast
import com.willkernel.kotlinapp.ui.adapter.VideoDetailAdapter
import com.willkernel.kotlinapp.utils.AppUtils
import com.willkernel.kotlinapp.utils.WatchHistoryUtils
import com.willkernel.kotlinapp.view.VideoListener
import kotlinx.android.synthetic.main.activity_video_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VideoDetailActivity : BaseActivity(), VideoDetailContract.View {


    companion object {
        const val IMG_TRANSITION = "IMG_TRANSITION"
        const val TRANSITION = "TRANSITION"
    }

    private val mPresenter by lazy { VideoDetailPresenter() }

    private val mAdapter by lazy { VideoDetailAdapter(this, itemList) }

    private val mFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()); }
    private var itemList = ArrayList<HomeBean.Issue.Item>()
    /**
     * Item 详细数据
     */
    private lateinit var itemData: HomeBean.Issue.Item
    private var isTransition: Boolean = false
    private var transition: Transition? = null
    private var orientationUtils: OrientationUtils? = null
    private var isPlay = false

    override fun initData() {
        itemData = intent.getSerializableExtra(Constants.BUNDLE_VIDEO_DATA) as HomeBean.Issue.Item
        isTransition = intent.getBooleanExtra(TRANSITION, false)

        Log.e(TAG, "itemData $itemData")
        saveWatchVideoHistoryInfo(itemData)
    }

    override fun initView() {
        mPresenter.subscribe(this)
        initTransition()
        initVideoViewConfig()
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        //设置相关视频 Item 的点击事件
        mAdapter.setOnItemDetailClick { mPresenter.loadVideoInfo(it) }

        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            loadVideoInfo()
        }
    }


    private fun initVideoViewConfig() {
        //设置旋转
        orientationUtils = OrientationUtils(this, mVideoView)
        mVideoView.isRotateViewAuto = false
        mVideoView.setIsTouchWiget(true)

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this)
            .load(itemData.data?.cover?.feed)
            .centerCrop()
            .into(imageView)
        mVideoView.thumbImageView = imageView

        mVideoView.setStandardVideoAllCallBack(object : VideoListener {
            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                //开始播放了才能旋转和全屏
                orientationUtils?.isEnable = true
                isPlay = true
            }

            override fun onAutoComplete(url: String, vararg objects: Any) {
                super.onAutoComplete(url, *objects)
                Logger.d("***** onAutoPlayComplete **** ")
            }

            override fun onPlayError(url: String, vararg objects: Any) {
                super.onPlayError(url, *objects)
                showToast("播放失败")
            }

            override fun onEnterFullscreen(url: String, vararg objects: Any) {
                super.onEnterFullscreen(url, *objects)
                Logger.d("***** onEnterFullscreen **** ")
            }

            override fun onQuitFullscreen(url: String, vararg objects: Any) {
                super.onQuitFullscreen(url, *objects)
                Logger.d("***** onQuitFullscreen **** ")
                //列表返回的样式判断
                orientationUtils?.backToProtVideo()
            }
        })

        mVideoView.backButton.setOnClickListener { onBackPressed() }
        mVideoView.fullscreenButton.setOnClickListener {
            orientationUtils?.resolveByClick()
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            mVideoView.startWindowFullscreen(this, true, true)
        }
        mVideoView.setLockClickListener(object : LockClickListener {
            override fun onClick(view: View?, lock: Boolean) {
                orientationUtils?.isEnable = !lock
            }
        })
    }

    private fun initTransition() {
        if (isTransition && AppUtils.isOverBuildVersion(Build.VERSION_CODES.LOLLIPOP)) {
            postponeEnterTransition()
            ViewCompat.setTransitionName(mVideoView, IMG_TRANSITION)
            addTransitionListener()
            startPostponedEnterTransition()
        } else {
            loadVideoInfo()
        }
    }

    private fun addTransitionListener() {
        transition = window.sharedElementEnterTransition

//        transition = TransitionInflater.from(this)
//            .inflateTransition(R.transition.arc_motion)
//        window.sharedElementEnterTransition=transition


        transition?.addListener(object : Transition.TransitionListener {
            override fun onTransitionResume(p0: Transition?) {
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionCancel(p0: Transition?) {
            }

            override fun onTransitionStart(p0: Transition?) {
            }

            override fun onTransitionEnd(p0: Transition?) {
                Logger.d("onTransitionEnd()------")

                loadVideoInfo()
                transition?.removeListener(this)
            }

        })
    }

    private var isPause = false

    override fun onResume() {
        super.onResume()
        isPause = false
        getCurPlay().onVideoResume()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        getCurPlay().onVideoPause()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils)
        }
    }

    private fun getCurPlay(): GSYVideoPlayer {
        return if (mVideoView.fullWindowPlayer != null) {
            mVideoView.fullWindowPlayer
        } else mVideoView
    }

    private fun loadVideoInfo() {
        mPresenter.loadVideoInfo(itemData)
    }

    override fun initListener() {
    }

    override fun getLayoutId(): Int = R.layout.activity_video_detail
    /**
     * 保存观看记录
     */
    private fun saveWatchVideoHistoryInfo(watchItem: HomeBean.Issue.Item) {
        //保存之前要先查询sp中是否有该value的记录，有则删除.这样保证搜索历史记录不会有重复条目
        val historyMap = WatchHistoryUtils.getAll(Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context) as Map<*, *>
        for ((key, _) in historyMap) {
            if (watchItem == WatchHistoryUtils.getObject(
                    Constants.FILE_WATCH_HISTORY_NAME,
                    MyApplication.context,
                    key as String
                )
            ) {
                WatchHistoryUtils.remove(Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context, key)
            }
        }
        WatchHistoryUtils.putObject(
            Constants.FILE_WATCH_HISTORY_NAME,
            MyApplication.context,
            watchItem,
            "" + mFormat.format(Date())
        )
    }

    override fun setVideo(url: String) {
        mVideoView.setUp(url, false, "")
        mVideoView.startPlayLogic()
    }

    override fun setVideoInfo(itemInfo: HomeBean.Issue.Item) {
        itemData = itemInfo
        mAdapter.addData(itemInfo)

        mPresenter.requestRelatedVideo(itemInfo.data?.id ?: 0)
    }

    override fun setBackground(url: String) {
        Glide.with(this)
            .load(url)
            .centerCrop()
            .format(DecodeFormat.PREFER_ARGB_8888)
            .transition(DrawableTransitionOptions().crossFade())
            .into(mVideoBackground)
    }

    override fun setRecentRelatedVideo(itemList: ArrayList<HomeBean.Issue.Item>) {
        mAdapter.addData(itemList)
        this.itemList = itemList
    }

    override fun setErrorMsg(errorMsg: String) {
    }

    override fun showError(msg: String, errCode: Int) {
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
        mRefreshLayout.finishRefresh()
    }

    override fun onBackPressed() {
        orientationUtils?.backToProtVideo()
        if (StandardGSYVideoPlayer.backFromWindowFull(this))
            return
        //释放所有
        mVideoView.setStandardVideoAllCallBack(null)
        GSYVideoPlayer.releaseAllVideos()
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) run {
            super.onBackPressed()
        } else {
            finish()
            overridePendingTransition(R.anim.anim_out, R.anim.anim_in)
        }
    }

    override fun onDestroy() {
        GSYVideoPlayer.releaseAllVideos()
        orientationUtils?.releaseListener()
        mPresenter.unSubscribe()
        super.onDestroy()
    }
}

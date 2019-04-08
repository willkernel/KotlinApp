package com.willkernel.kotlinapp.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseActivity
import com.willkernel.kotlinapp.ui.fragment.DiscoveryFragment
import com.willkernel.kotlinapp.ui.fragment.HomeFragment
import com.willkernel.kotlinapp.ui.fragment.HotFragment
import com.willkernel.kotlinapp.ui.fragment.MineFragment
import com.willkernel.kotlinapp.utils.NotificationUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {
    private val mTitles = arrayOf("每日精选", "发现", "热门", "我的")
    // 未被选中的图标
    private val mUnSelectIds = intArrayOf(
        R.mipmap.ic_home_normal,
        R.mipmap.ic_discovery_normal,
        R.mipmap.ic_hot_normal,
        R.mipmap.ic_mine_normal
    )
    // 被选中的图标
    private val mSelectIds = intArrayOf(
        R.mipmap.ic_home_selected,
        R.mipmap.ic_discovery_selected,
        R.mipmap.ic_hot_selected,
        R.mipmap.ic_mine_selected
    )
    private var mHomeFragment: HomeFragment? = null
    private var mDiscoveryFragment: DiscoveryFragment? = null
    private var mHotFragment: HotFragment? = null
    private var mMineFragment: MineFragment? = null

    private var mIndex = 0
    private var mTabEntities = ArrayList<CustomTabEntity>()
    override fun initData() {
        mTitles.indices.mapTo(mTabEntities) {
            TabEntity(mTitles[it], mSelectIds[it], mUnSelectIds[it])
        }

        tab_layout.setTabData(mTabEntities)
        tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                switchFragment(position)
            }

            override fun onTabReselect(position: Int) {
            }
        })

    }

    override fun initView() {
        tab_layout.currentTab = mIndex
        switchFragment(mIndex)

        tab_layout.postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationUtils.showTextNotification(this)
            }
            NotificationUtils.openChannelSetting(NotificationUtils.CHANNEL_ID,this)
        }, 2 * 1000)
    }


    override fun initListener() {
    }

    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragment(transaction)

        when (position) {
            HomeFragment.ID -> {
                //非空直接显示
                mHomeFragment?.let { transaction.show(it) }
                //为空时 ?:返回默认值, 创建对象,添加到布局并显示
                    ?: HomeFragment.newInstance(mTitles[position]).let {
                        mHomeFragment = it
                        transaction.add(R.id.fl_container, it)
                    }
            }
            DiscoveryFragment.ID -> {
                mDiscoveryFragment?.let { transaction.show(it) }
                    ?: DiscoveryFragment.newInstance(mTitles[position]).let {
                        mDiscoveryFragment = it
                        transaction.add(R.id.fl_container, it)
                    }
            }
            HotFragment.ID -> {
                mHotFragment?.let { transaction.show(it) }
                    ?: HotFragment.newInstance(mTitles[position]).let {
                        mHotFragment = it
                        transaction.add(R.id.fl_container, it)
                    }
            }
            MineFragment.ID -> {
                mMineFragment?.let { transaction.show(it) }
                    ?: MineFragment.newInstance(mTitles[position]).let {
                        mMineFragment = it
                        transaction.add(R.id.fl_container, it)
                    }
            }
            else -> {
            }
        }
        mIndex = position
        tab_layout.currentTab = mIndex
        transaction.commitAllowingStateLoss()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        mHomeFragment?.let { transaction.hide(it) }
        mDiscoveryFragment?.let { transaction.hide(it) }
        mHotFragment?.let { transaction.hide(it) }
        mMineFragment?.let { transaction.hide(it) }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mIndex = savedInstanceState.getInt("tabIndex")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (tab_layout != null) {
            outState.putInt("tabIndex", mIndex)
        }
    }

    override fun getLayoutId() = R.layout.activity_main
}

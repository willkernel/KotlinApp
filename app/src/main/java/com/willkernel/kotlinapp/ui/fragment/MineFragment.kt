package com.willkernel.kotlinapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import com.willkernel.kotlinapp.Constants
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseFragment
import com.willkernel.kotlinapp.ui.activity.WatchHistoryActivity
import com.willkernel.kotlinapp.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_mine.*


class MineFragment : BaseFragment() {
    companion object {
        const val ID = 3
        fun newInstance(title: String): MineFragment {
            val bundle = Bundle()
            bundle.putString(Constants.ARGUMENTS_TITLE, title)
            val fragment = MineFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        tv_watch_history.setOnClickListener {
            startActivity(Intent(activity, WatchHistoryActivity::class.java))
        }
        activity?.let {
            StatusBarUtil.darkMode(it)
            StatusBarUtil.setPaddingSmart(it,toolbar)
        }
    }

    override fun lazyLoad() {
    }


    override fun getLayoutId() = R.layout.fragment_mine
}
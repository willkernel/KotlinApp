package com.willkernel.kotlinapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.willkernel.kotlinapp.Constants
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseFragment
import com.willkernel.kotlinapp.ui.adapter.BaseFragmentAdapter
import com.willkernel.kotlinapp.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_hot.*


class DiscoveryFragment : BaseFragment() {
    private val tabList = ArrayList<String>()
    private val fragments = ArrayList<Fragment>()
    private var mTitle: String? = null


    override fun lazyLoad() {
    }

    override fun initView() {
        activity?.let {
            StatusBarUtil.darkMode(it)
            StatusBarUtil.setPaddingSmart(it,toolbar_hot)
        }

        tv_header_title_hot.text=mTitle
        tabList.add("关注")
        tabList.add("分类")
        fragments.add(FollowFragment.newInstance("关注"))
        fragments.add(CategoryFragment.newInstance("分类"))

        mViewPager.adapter= BaseFragmentAdapter(childFragmentManager, fragments, tabList)
        mTabLayout.setupWithViewPager(mViewPager)
    }

    companion object {
        const val ID = 1
        fun newInstance(title: String): DiscoveryFragment {
            val bundle = Bundle()
            bundle.putString(Constants.ARGUMENTS_TITLE, title)
            val fragment = DiscoveryFragment()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId() = R.layout.fragment_hot

}
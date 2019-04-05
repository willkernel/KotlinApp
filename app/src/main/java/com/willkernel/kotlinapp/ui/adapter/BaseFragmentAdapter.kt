package com.willkernel.kotlinapp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BaseFragmentAdapter(fm: FragmentManager, fragments: ArrayList<Fragment>, tabList: ArrayList<String>) :
    FragmentPagerAdapter(fm) {
    private var fragmentList: List<Fragment>? = ArrayList()
    private var mTitles: List<String>? = null

    init {
        setFragments(fm, fragments, tabList)
    }

    private fun setFragments(fm: FragmentManager, fragmentList: List<Fragment>, mTitles: List<String>) {
        this.mTitles = mTitles

//        if (this.fragmentList != null) {
//            val ft = fm.beginTransaction()
//            fragmentList?.forEach {
//                ft.remove(it)
//            }
//            ft?.commitAllowingStateLoss()
//            fm.executePendingTransactions()
//        }
        this.fragmentList = fragmentList
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles?.get(position) ?: ""
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList!![position]
    }

    override fun getCount(): Int {
        return fragmentList!!.size
    }

}

package com.willkernel.kotlinapp.ui.fragment

import android.os.Bundle
import com.willkernel.kotlinapp.Constants
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseFragment
import com.willkernel.kotlinapp.mvp.contract.HomeContract
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_hot.*


class HotFragment : BaseFragment(), HomeContract.View {
    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dismissLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
        activity?.let {
            StatusBarUtil.darkMode(it)
            StatusBarUtil.setPaddingSmart(it,toolbar_hot)
        }
    }

    override fun lazyLoad() {
    }

    override fun showError(msg: String, errCode: Int) {
    }

    companion object {
        const val ID = 2
        fun newInstance(title: String): HotFragment {
            val bundle = Bundle()
            bundle.putString(Constants.ARGUMENTS_TITLE, title)
            val fragment = HotFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId() = R.layout.fragment_hot
    override fun setHomeData(homeBean: HomeBean) {
    }

    override fun setMoreData(itemList: ArrayList<HomeBean.Issue.Item>) {
    }
}
package com.willkernel.kotlinapp.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.base.BaseFragment
import com.willkernel.kotlinapp.mvp.contract.CategoryContract
import com.willkernel.kotlinapp.mvp.model.CategoryBean
import com.willkernel.kotlinapp.mvp.presenter.CategoryPresenter
import com.willkernel.kotlinapp.ui.adapter.CategoryAdapter
import com.willkernel.kotlinapp.utils.DisplayManager
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_hot.*

class CategoryFragment : BaseFragment(), CategoryContract.View {

    private var mTitle: String? = null
    private var mCategoryList = ArrayList<CategoryBean>()
    private val mPresenter by lazy { CategoryPresenter() }
    private val mAdapter by lazy { activity?.let { CategoryAdapter(it, mCategoryList, R.layout.item_category) } }

    companion object {
        fun newInstance(title: String): CategoryFragment {
            val fragment = CategoryFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun showCategory(categoryList: ArrayList<CategoryBean>) {
        mCategoryList = categoryList
        mAdapter?.setData(mCategoryList)
    }


    override fun initView() {
        mPresenter.subscribe(this)
        mLayoutStatusView = multipleStatusView

        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                val offset = DisplayManager.dip2px(2f)!!

//                outRect.set(if (position % 2 == 0) 0 else offset, offset,
//                    if (position % 2 == 0) offset else 0, offset
//                )
                outRect.set(if (position % 2 == 0) 0 else offset, offset,
                    if (position % 2 == 0) offset else 0, offset)
            }

        })
    }

    override fun lazyLoad() {
        mPresenter.getCategoryData()
    }

    override fun getLayoutId(): Int = R.layout.fragment_category


    override fun showError(msg: String, errCode: Int) {

    }

    override fun showLoading() {
        multipleStatusView?.showLoading()
    }

    override fun dismissLoading() {
        multipleStatusView?.showContent()
    }

}

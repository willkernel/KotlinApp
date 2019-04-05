package com.willkernel.kotlinapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.willkernel.kotlinapp.view.MultipleStatusView

/**
 * Created by willkernel
 * on 2019/4/2.
 */
abstract class BaseFragment : Fragment() {
    protected val TAG: String = javaClass.name
    /**
     * 多种状态的 View 的切换
     */
    protected var mLayoutStatusView: MultipleStatusView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(getLayoutId(), null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        lazyLoad()
    }

    abstract fun initView()

    abstract fun lazyLoad()

    @LayoutRes
    abstract fun getLayoutId(): Int
}
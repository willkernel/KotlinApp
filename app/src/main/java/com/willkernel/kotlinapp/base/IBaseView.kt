package com.willkernel.kotlinapp.base

interface IBaseView {

    /**
     * 显示错误信息
     */
    fun showError(msg: String, errCode: Int)

    fun showLoading()

    fun dismissLoading()

}
package com.willkernel.kotlinapp.base

interface IBasePresenter<V : IBaseView> {
    fun subscribe(v: V)
    fun unSubscribe()
}
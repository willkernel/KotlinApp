package com.willkernel.kotlinapp.view.recyclerview

interface MultipleType<T> {
    fun getLayoutId(item: T, position: Int):Int
}

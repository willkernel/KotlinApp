package com.willkernel.kotlinapp.view.recyclerview

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mView: SparseArray<View> by lazy { SparseArray<View>() }

    fun <T : View> getView(viewId: Int): T {
        var view: View? = mView.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            mView.put(viewId, view)
        }
        return view as T
    }

    fun setText(viewId: Int, resId: CharSequence) {
        getView<TextView>(viewId).text = resId
    }

    fun setImageResource(viewId: Int, resId: Int) {
        getView<ImageView>(viewId).setImageResource(resId)
    }

    fun setImagePath(viewId: Int, imageLoader: ImageLoader) {
        imageLoader.loadImage(getView<ImageView>(viewId), imageLoader.path)
    }

    abstract class ImageLoader(val path: String) {
        abstract fun loadImage(iv: ImageView, path: String)

    }

    fun setViewVisiblity(viewId: Int, visible: Int): ViewHolder {
        getView<View>(viewId).visibility = visible
        return this
    }

    fun setOnItemClickListener(listener: View.OnClickListener) {
        itemView.setOnClickListener(listener)
    }

    fun setOnItemLongClickListener(listener: View.OnLongClickListener) {
        itemView.setOnLongClickListener(listener)
    }
}


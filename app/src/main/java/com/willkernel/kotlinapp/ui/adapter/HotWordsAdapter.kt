package com.willkernel.kotlinapp.ui.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayoutManager
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.view.recyclerview.ViewHolder

class HotWordsAdapter(mContext: Context, mHotList: ArrayList<String>, item_hot_text: Int) :
    CommonAdapter<String>(mContext, mHotList, item_hot_text) {
    override fun bindData(holder: ViewHolder, data: String, position: Int) {
        holder.setText(R.id.tv_title, data)
        val params = holder.getView<TextView>(R.id.tv_title).layoutParams
        if (params is FlexboxLayoutManager.LayoutParams) {
            params.flexGrow = 1.0f
        }
        holder.setOnItemClickListener(listener = View.OnClickListener {
            tagListener?.invoke(data)
        })
    }

    private var tagListener: ((tag: String) -> Unit)? = null

    /**闭包*/
    fun setOnTagItemClickListener(listener: (tag: String) -> Unit) {
        this.tagListener = listener
    }
}

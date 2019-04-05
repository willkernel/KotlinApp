package com.willkernel.kotlinapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.durationFormat
import com.willkernel.kotlinapp.glide.GlideApp
import com.willkernel.kotlinapp.goToVideoPlayer
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.view.recyclerview.ViewHolder
import kotlinx.android.synthetic.main.item_category_detail.view.*

class CategoryDetailAdapter(mContext: Context, mData: ArrayList<HomeBean.Issue.Item>, mLayId: Int) :
    CommonAdapter<HomeBean.Issue.Item>(mContext, mData, mLayId) {

    fun addData(datas: ArrayList<HomeBean.Issue.Item>) {
        this.mData.addAll(datas)
        notifyDataSetChanged()
    }

    override fun bindData(holder: ViewHolder, data: HomeBean.Issue.Item, position: Int) {
        setVideoItem(holder, data)
    }

    private fun setVideoItem(holder: ViewHolder, data: HomeBean.Issue.Item) {
        val itemData = data.data
        val cover = itemData?.cover?.feed ?: ""
        GlideApp.with(mContext)
            .load(cover)
            .apply(RequestOptions.placeholderOf(R.drawable.placeholder_banner))
            .transition(DrawableTransitionOptions().crossFade())
            .into(holder.itemView.iv_image)
        holder.setText(R.id.tv_title, itemData?.title ?: "")

        val timeFormat = durationFormat(itemData?.duration)
        holder.setText(R.id.tv_tag, "#${itemData?.category}/$timeFormat")

        holder.setOnItemClickListener(listener = View.OnClickListener {
            goToVideoPlayer(mContext as Activity, holder.getView(R.id.iv_image), data)
        })
    }

    fun clearData() {
        this.mData.clear()
    }

}

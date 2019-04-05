package com.willkernel.kotlinapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.durationFormat
import com.willkernel.kotlinapp.glide.GlideApp
import com.willkernel.kotlinapp.goToVideoPlayer
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.view.recyclerview.ViewHolder

class WatchHistoryAdapter(context: Context, dataList: ArrayList<HomeBean.Issue.Item>, layoutId: Int) :
    CommonAdapter<HomeBean.Issue.Item>(context, dataList, layoutId) {


    //绑定数据
    override fun bindData(holder: ViewHolder, data: HomeBean.Issue.Item, position: Int) {
        with(holder) {
            setText(R.id.tv_title, data.data?.title!!)
            setText(R.id.tv_tag, "#${data.data.category} / ${durationFormat(data.data.duration)}")
            setImagePath(R.id.iv_video_small_card, object : ViewHolder.ImageLoader(data.data.cover.detail) {
                override fun loadImage(iv: ImageView, path: String) {
                    GlideApp.with(mContext)
                        .load(path)
                        .placeholder(R.drawable.placeholder_banner)
                        .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions().crossFade())
                        .into(iv)
                }
            })
        }
        holder.getView<TextView>(R.id.tv_title).setTextColor(ContextCompat.getColor(mContext, R.color.color_black))
        holder.setOnItemClickListener(listener = View.OnClickListener {
            goToVideoPlayer(mContext as Activity, holder.getView(R.id.iv_video_small_card), data)
        })
    }
}
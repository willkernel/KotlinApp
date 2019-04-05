package com.willkernel.kotlinapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.bingoogolapple.bgabanner.BGABanner
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.willkernel.kotlinapp.R
import com.willkernel.kotlinapp.durationFormat
import com.willkernel.kotlinapp.glide.GlideApp
import com.willkernel.kotlinapp.goToVideoPlayer
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.mvp.model.HomeBean.Issue.Item
import com.willkernel.kotlinapp.view.recyclerview.ViewHolder
import io.reactivex.Observable

class HomeAdapter(
    context: Context,
    itemList: ArrayList<Item>
) : CommonAdapter<Item>(context, itemList, -1) {
    companion object {
        const val TAG = "HomeAdapter"
        private const val ITEM_TYPE_BANNER = 1    //Banner 类型
        private const val ITEM_TYPE_TEXT_HEADER = 2   //textHeader
        private const val ITEM_TYPE_CONTENT = 3    //item
    }

    override fun bindData(holder: ViewHolder, data: Item, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_BANNER -> {
                val bannerItemData: ArrayList<HomeBean.Issue.Item> =
                    mData.take(bannerItemSize).toCollection(ArrayList())
                val bannerFeedList = ArrayList<String>()
                val bannerTitleList = ArrayList<String>()
                Observable.fromIterable(bannerItemData).subscribe { list ->
                    bannerFeedList.add(list.data?.cover?.feed ?: "")
                    bannerTitleList.add(list.data?.title ?: "")
                }
                with(holder) {
                    getView<BGABanner>(R.id.banner).run {
                        setAutoPlayAble(bannerFeedList.size > 1)
                        setData(bannerFeedList, bannerTitleList)
                        //未使用参数 _ 代替
                        setAdapter { banner, _, feedImgUrl, position ->
                            GlideApp.with(mContext)
                                .load(feedImgUrl)
                                .placeholder(R.drawable.placeholder_banner)
                                .transition(DrawableTransitionOptions().crossFade())
                                .into(banner.getItemImageView(position))
                        }
                    }
                }
                holder.getView<BGABanner>(R.id.banner).setDelegate { _, itemView, _, position ->
                    goToVideoPlayer(mContext as Activity, itemView, bannerItemData[position])
                }
            }
            ITEM_TYPE_TEXT_HEADER -> {
                holder.setText(R.id.tvHeader, mData[position + bannerItemSize - 1].data?.text ?: "")
//                holder.itemView.tvHeader.text = mData[position + bannerItemSize - 1].data?.text ?: ""
            }
            ITEM_TYPE_CONTENT -> {
                setVideoItem(holder, mData[position + bannerItemSize - 1])
            }
        }
    }

    private fun setVideoItem(holder: ViewHolder, item: HomeBean.Issue.Item) {
        val itemData = item.data
        val defAvatar = R.mipmap.default_avatar
        val cover = itemData?.cover?.feed
        var avatar = itemData?.author?.icon
        var tagText = "#"
        // 作者出处为空，就显获取提供者的信息
        if (avatar.isNullOrEmpty()) {
            avatar = itemData?.provider?.icon
        }
        // 加载封页图
        GlideApp.with(mContext)
            .load(cover)
            .placeholder(R.drawable.placeholder_banner)
            .transition(DrawableTransitionOptions().crossFade())
            .into(holder.getView<ImageView>(R.id.iv_cover_feed))

        // 如果提供者信息为空，就显示默认
        if (avatar.isNullOrEmpty()) {
            GlideApp.with(mContext)
                .load(defAvatar)
                .placeholder(R.mipmap.default_avatar).circleCrop()
                .transition(DrawableTransitionOptions().crossFade())
                .into(holder.getView<ImageView>(R.id.iv_avatar))
        } else {
            GlideApp.with(mContext)
                .load(avatar)
                .placeholder(R.mipmap.default_avatar).circleCrop()
                .transition(DrawableTransitionOptions().crossFade())
                .into(holder.getView<ImageView>(R.id.iv_avatar))
        }
        holder.setText(R.id.tv_title, itemData?.title ?: "")
        itemData?.tags?.take(4)?.forEach { tagText += (it.name + "/") }
        val timeFormat = durationFormat(itemData?.duration)
        tagText += timeFormat
        holder.setText(R.id.tv_tag, tagText)
        holder.setText(R.id.tv_category, "#" + itemData?.category)
        holder.setOnItemClickListener(listener = View.OnClickListener {
            goToVideoPlayer(mContext as Activity, holder.getView(R.id.iv_cover_feed), item)
        })
    }

    var bannerItemSize = 0

    fun setBannerSize(count: Int) {
        bannerItemSize = count
        Log.e(TAG, "bannerItemSize $bannerItemSize")
    }

    fun addItemData(itemList: List<Item>) {
        this.mData.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_TYPE_BANNER -> {
                ViewHolder(inflateView(R.layout.item_home_banner, parent))
            }
            ITEM_TYPE_CONTENT -> {
                ViewHolder(inflateView(R.layout.item_home_content, parent))
            }
            else -> {
                ViewHolder(inflateView(R.layout.item_home_header, parent))
            }
        }
    }

    private fun inflateView(id: Int, parent: ViewGroup): View {
        return mInflater?.inflate(id, parent, false) ?: View(parent.context)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 ->
                ITEM_TYPE_BANNER
            mData[position + bannerItemSize - 1].type == "textHeader" -> {
                Log.e(
                    TAG,
                    "position $position  mData[position + bannerItemSize - 1]=${mData[position + bannerItemSize - 1]}"
                )
                ITEM_TYPE_TEXT_HEADER
            }
            else ->
                ITEM_TYPE_CONTENT
        }
    }

    override fun getItemCount(): Int {
        return when {
            mData.size > bannerItemSize -> mData.size - bannerItemSize + 1
            mData.isEmpty() -> 0
            else -> 1
        }
    }
}

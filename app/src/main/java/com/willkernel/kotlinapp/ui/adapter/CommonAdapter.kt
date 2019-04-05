package com.willkernel.kotlinapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willkernel.kotlinapp.view.recyclerview.MultipleType
import com.willkernel.kotlinapp.view.recyclerview.ViewHolder
import com.willkernel.kotlinapp.view.recyclerview.adapter.OnItemClickListener
import com.willkernel.kotlinapp.view.recyclerview.adapter.OnItemLongClickListener


abstract class CommonAdapter<T>(var mContext: Context, var mData: ArrayList<T>, var mLayoutId: Int) :
    RecyclerView.Adapter<ViewHolder>() {
    protected var TAG:String=javaClass.simpleName
    protected var mInflater: LayoutInflater? = null
    private var mTypeSupport: MultipleType<T>? = null
    //使用接口回调点击事件
    private var mItemClickListener: OnItemClickListener? = null

    //使用接口回调点击事件
    private var mItemLongClickListener: OnItemLongClickListener? = null

    init {
        mInflater = LayoutInflater.from(mContext)
    }

    constructor(mContext: Context, mData: ArrayList<T>, typeSupport: MultipleType<T>) : this(mContext, mData, -1) {
        this.mTypeSupport = typeSupport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mTypeSupport != null) {
            mLayoutId = viewType
        }
        val view = mInflater?.inflate(mLayoutId, parent,false)
        return ViewHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {
        return mTypeSupport?.getLayoutId(mData[position], position) ?: super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindData(holder, mData[position], position)

        mItemClickListener?.let {
            holder.itemView.setOnClickListener { mItemClickListener!!.onItemClick(mData[position], position) }
        }
        mItemLongClickListener?.let {
            holder.itemView.setOnLongClickListener {
                mItemLongClickListener!!.onItemLongClick(
                    mData[position],
                    position
                )
            }
        }
    }

    protected abstract fun bindData(holder: ViewHolder, data: T, position: Int)

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

    fun setOnItemLongClickListener(itemLongClickListener: OnItemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener
    }
}
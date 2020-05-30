package com.snowman.graduationprojectclient.adapter.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

open class BaseItemBean

abstract class BaseRecyclerViewAdapter<D : BaseItemBean, V : BaseViewHolder<D>>(
    var mContext: Context,
    var dataList: ArrayList<D>
) :
    RecyclerView.Adapter<V>() {

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: V, position: Int) {
        holder.data = dataList[position]
        bindData(holder, dataList[position])
    }

    abstract fun bindData(holder: V, data: D)


}
package com.snowman.graduationprojectclient.adapter.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<D>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var data: D? = null

    init {
        initView(itemView)
    }

    abstract fun initView(itemView: View)

    fun setOnClickListener(view: View, invoke: (view: View, data: D) -> Unit) {
        view.setOnClickListener {
            invoke(view, data!!)
        }
    }
}
package com.snowman.graduationprojectclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snowman.graduationprojectclient.R
import com.snowman.graduationprojectclient.bean.BasePastTimeData
import com.snowman.graduationprojectclient.bean.PastTimeData
import com.snowman.graduationprojectclient.bean.PastTimeHumData
import com.snowman.graduationprojectclient.bean.PastTimeTemData
import com.snowman.graduationprojectclient.ui.PastTimeDeviceDataActivity

class PastTimeDataAdapter(
    var mContext: Context,
    var dataList: List<BasePastTimeData>,
    var dataType: PastTimeDeviceDataActivity.PastTimeDataType
) :
    RecyclerView.Adapter<PastTimeDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = when (dataType) {
            PastTimeDeviceDataActivity.PastTimeDataType.PICTURE -> {
                LayoutInflater.from(mContext).inflate(R.layout.past_time_data_picture_item, null)
            }
            PastTimeDeviceDataActivity.PastTimeDataType.HUMIDITY -> {
                LayoutInflater.from(mContext)
                    .inflate(R.layout.past_time_data_hum_or_ter_item, parent, false)
            }
            PastTimeDeviceDataActivity.PastTimeDataType.TEMPERATURE -> {
                LayoutInflater.from(mContext)
                    .inflate(R.layout.past_time_data_hum_or_ter_item, parent, false)
            }
        }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (dataType) {
            PastTimeDeviceDataActivity.PastTimeDataType.TEMPERATURE -> {
                val temperature = dataList[position] as PastTimeTemData
                holder.mTime.text = "${temperature.time}"
                holder.mData.text = "${temperature.temperature}度"
            }
            PastTimeDeviceDataActivity.PastTimeDataType.HUMIDITY -> {
                val humidity = dataList[position] as PastTimeHumData
                holder.mTime.text = "${humidity.time}"
                holder.mData.text = "${humidity.humidity}%"
            }
            else -> {
            }
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var mTime: TextView
        lateinit var mData: TextView

        init {
            initView(itemView)
        }

        private fun initView(itemView: View) {
            itemView.let {
                mTime = it.findViewById(R.id.tv_past_data_item_time)
                mData = it.findViewById(R.id.tv_past_data_item_data)
            }
        }
    }
}

package com.snowman.graduationprojectclient.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.snowman.graduationprojectclient.R
import com.snowman.graduationprojectclient.adapter.base.BaseRecyclerViewAdapter
import com.snowman.graduationprojectclient.adapter.base.BaseViewHolder
import com.snowman.graduationprojectclient.bean.*
import com.snowman.graduationprojectclient.manager.UserManager
import com.snowman.graduationprojectclient.remote.remoteservice.CallBack
import com.snowman.graduationprojectclient.remote.remoteservice.RemoteService
import com.snowman.graduationprojectclient.ui.DeviceListActivity
import com.snowman.graduationprojectclient.ui.PastTimeDeviceDataActivity
import com.snowman.graduationprojectclient.ui.PastTimeDeviceDataActivity.Companion.PAST_TIME_DEVICE_ID
import com.snowman.graduationprojectclient.ui.PastTimeDeviceDataActivity.Companion.PAST_TIME_TYPE
import com.snowman.graduationprojectclient.ui.base.BaseActivity
import com.snowman.graduationprojectclient.utils.log
import com.snowman.graduationprojectclient.view.DeviceManagerDialog
import com.snowman.graduationprojectclient.view.InputMsgDialog

class SnowManRvAdapter(
    mContext: Context,
    dataList: ArrayList<DataWrapper<Any>>,
    private var operateType: DeviceListActivity.OperateType
) :
    BaseRecyclerViewAdapter<DataWrapper<Any>, SnowManRvAdapter.SelectedDeviceViewHolder>(
        mContext,
        dataList
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedDeviceViewHolder {
        return when (operateType) {
            DeviceListActivity.OperateType.SELECT_DEVICE_SHOW_DATA -> SelectedDeviceViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.select_device_item, null)
            )
            DeviceListActivity.OperateType.MANAGER_DEICE -> SelectedDeviceViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.device_manager_item, null)
            )
            DeviceListActivity.OperateType.GET_ADMIN_APPLY_LIST -> SelectedDeviceViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.admin_apply_item, null)
            )
            DeviceListActivity.OperateType.GET_ADD_DEVICE_APPLY_LIST -> SelectedDeviceViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.add_device_apply_item, null)
            )
            DeviceListActivity.OperateType.GET_DEVICE -> SelectedDeviceViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.show_device_list_item, null)
            )
        }
    }

    override fun getItemCount() = dataList.size

    inner class SelectedDeviceViewHolder(itemView: View) :
        BaseViewHolder<DataWrapper<Any>>(itemView) {
        lateinit var mTvName: TextView

        private lateinit var mBtDeviceManager: Button
        lateinit var mTvAddDeviceDetail: TextView
        lateinit var mTvAddDeviceDescription: TextView

        private lateinit var mBtAgreeAdminApply: Button
        private lateinit var mBtNotAgreeAdminApply: Button

        private lateinit var mBtAgreeAddDevice: Button
        private lateinit var mBtNotAgreeAddDevice: Button
        lateinit var mTvApplyMsg: TextView

        private lateinit var mBtAddDevice: Button
        private lateinit var mBtShowRealTimeDeviceData: Button
        private lateinit var mBtShowPastTimeHDeviceData: Button
        private lateinit var mBtShowPastTimeTDeviceData: Button
        private lateinit var mBtShowPastTimePDeviceData: Button
        override fun initView(itemView: View) {
            when (operateType) {
                DeviceListActivity.OperateType.SELECT_DEVICE_SHOW_DATA -> {
                    mTvName = itemView.findViewById(R.id.tv_manager_device_item_device_name)
                    mBtShowRealTimeDeviceData =
                        itemView.findViewById(R.id.iv_select_device_show_real_time_data)
                    mBtShowPastTimeHDeviceData =
                        itemView.findViewById(R.id.iv_select_device_show_past_time_h_data)
                    mBtShowPastTimeTDeviceData =
                        itemView.findViewById(R.id.iv_select_device_show_past_time_t_data)
                    mBtShowPastTimePDeviceData =
                        itemView.findViewById(R.id.iv_select_device_show_past_time_p_data)
                    setOnClickListener(mBtShowRealTimeDeviceData) { _, deviceWrapper ->
                        log("发送回去的devid = ${(deviceWrapper.data as DeviceManagerBean).devid}")
                        UserManager.instance.nowDisplayDeviceId.postValue((deviceWrapper.data as DeviceManagerBean).devid)
                        (mContext as Activity).finish()
                    }
                    setOnClickListener(mBtShowPastTimeHDeviceData) { _, deviceWrapper ->
                        getPastTimeData(
                            (deviceWrapper.data as DeviceManagerBean).devid,
                            PastTimeDeviceDataActivity.PastTimeDataType.HUMIDITY
                        )
                    }
                    setOnClickListener(mBtShowPastTimeTDeviceData) { _, deviceWrapper ->
                        getPastTimeData(
                            (deviceWrapper.data as DeviceManagerBean).devid,
                            PastTimeDeviceDataActivity.PastTimeDataType.TEMPERATURE
                        )
                    }
                    setOnClickListener(mBtShowPastTimePDeviceData) { _, deviceWrapper ->
                        getPastTimeData(
                            (deviceWrapper.data as DeviceManagerBean).devid,
                            PastTimeDeviceDataActivity.PastTimeDataType.PICTURE
                        )
                    }
                }
                DeviceListActivity.OperateType.MANAGER_DEICE -> {
                    mTvName = itemView.findViewById(R.id.tv_manager_device_item_device_name)
                    mBtDeviceManager =
                        itemView.findViewById(R.id.bt_manager_device_item_device_manager)

                    setOnClickListener(mBtDeviceManager) { _, deviceWrapper ->
                        val data = deviceWrapper.data as DeviceManagerBean
                        DeviceManagerDialog.Companion.Builder(mContext).setTitle("设备管理")
                            .setList("温度", "湿度")
                            .setCommitButtonText("提交") { item, max, min ->
                                RemoteService.updateDeviceConfig(
                                    UserManager.instance.userInfo.uuid,
                                    data.devid, item, max, min,
                                    object : CallBack<GeneralResponse> {
                                        override fun success(data: GeneralResponse?) {
                                        }

                                        override fun failed(res: String?) {
                                        }
                                    })
                            }.create().show()
                    }
                }
                DeviceListActivity.OperateType.GET_ADMIN_APPLY_LIST -> {
                    itemView.apply {
                        mTvName = findViewById(R.id.tv_admin_apply_item_uuid)
                        mTvApplyMsg = findViewById(R.id.tv_admin_apply_item_apply_msg)
                        mBtAgreeAdminApply =
                            findViewById(R.id.bt_admin_apply_item_agree_admin_apply)
                        mBtNotAgreeAdminApply =
                            findViewById(R.id.bt_admin_apply_item_not_agree_admin_apply)
                    }
                    setOnClickListener(mBtAgreeAdminApply) { _, adminApply ->
                        handleAdminApply(adminApply, true)
                    }
                    setOnClickListener(mBtNotAgreeAdminApply) { _, adminApply ->
                        handleAdminApply(adminApply, false)
                    }
                }
                DeviceListActivity.OperateType.GET_ADD_DEVICE_APPLY_LIST -> {
                    itemView.apply {
                        mTvAddDeviceDetail = findViewById(R.id.tv_add_device_item_name_devid)
                        mTvAddDeviceDescription = findViewById(R.id.tv_add_device_item_description)
                        mBtAgreeAddDevice = findViewById(R.id.bt_device_item_agree_add_device)
                        mBtNotAgreeAddDevice = findViewById(R.id.bt_device_item_no_agree_add_device)
                    }

                    setOnClickListener(mBtAgreeAddDevice) { _, dataWrapper ->
                        handleAddDeviceApply(dataWrapper, true)
                    }
                    setOnClickListener(mBtNotAgreeAddDevice) { _, dataWrapper ->
                        handleAddDeviceApply(dataWrapper, false)
                    }
                }
                DeviceListActivity.OperateType.GET_DEVICE -> {
                    mTvName = itemView.findViewById(R.id.tv_manager_device_item_device_name)
                    mBtAddDevice = itemView.findViewById(R.id.bt_device_item_add_device)
                    setOnClickListener(mBtAddDevice) { _, dataWrapper ->
                        addDevice(dataWrapper)
                    }
                }
            }
        }

        private fun getPastTimeData(
            devid: String,
            type: PastTimeDeviceDataActivity.PastTimeDataType
        ) {
            (mContext as BaseActivity).startActivity(PastTimeDeviceDataActivity::class.java) {
                it.putExtra(PAST_TIME_TYPE, type.value)
                it.putExtra(PAST_TIME_DEVICE_ID, devid)
            }
        }

        private fun addDevice(device: DataWrapper<Any>) {
            val dev = device.data as DeviceInfo
            InputMsgDialog.Companion.Builder(mContext).setTitle("请输入添加原因")
                .setCommitButtonText("提交") { msg ->
                    RemoteService.addDevice(
                        UserManager.instance.userInfo.uuid,
                        UserManager.instance.userInfo.username,
                        dev.address, dev.devid, msg,
                        object : CallBack<String> {
                            override fun success(data: String?) {
                                log("$data")
                            }

                            override fun failed(res: String?) {
                            }

                        })
                }.create().show()
        }

        /**
         * 处理管理员申请
         */
        private fun handleAdminApply(adminApply: DataWrapper<Any>, agree: Boolean) {
            val data = adminApply.data as ApplyAdminBean
            RemoteService.handleAdminApply(UserManager.instance.userInfo.uuid,
                data.uuid, agree
                , object : CallBack<GeneralResponse> {
                    override fun success(data: GeneralResponse?) {
                        //---代办---删除列表item
                    }

                    override fun failed(res: String?) {
                    }
                })
        }

        /**
         * 处理添加设备申请
         */
        private fun handleAddDeviceApply(adminApply: DataWrapper<Any>, agree: Boolean) {
            val data = adminApply.data as AddDeviceApplyBean
            RemoteService.handleAddDevApply(
                UserManager.instance.userInfo.uuid,
                data.uuid, data.devid, agree
                , object : CallBack<String> {
                    override fun success(data: String?) {
                        //---代办---
                        log(data!!)
                    }

                    override fun failed(res: String?) {
                    }
                })
        }
    }

    override fun bindData(holder: SelectedDeviceViewHolder, dataWrapper: DataWrapper<Any>) {
        when (operateType) {
            DeviceListActivity.OperateType.SELECT_DEVICE_SHOW_DATA -> {
                val deviceInfo = dataWrapper.data as DeviceManagerBean
                holder.mTvName.text = deviceInfo.devid
                holder.data = dataWrapper
            }
            DeviceListActivity.OperateType.MANAGER_DEICE -> {
                val deviceInfo = dataWrapper.data as DeviceManagerBean
                holder.mTvName.text = deviceInfo.devid
                holder.data = dataWrapper
            }
            DeviceListActivity.OperateType.GET_ADMIN_APPLY_LIST -> {
                val info = dataWrapper.data as ApplyAdminBean
                holder.mTvName.text = info.uuid
                holder.mTvApplyMsg.text = info.adminMsg
                holder.data = dataWrapper
            }
            DeviceListActivity.OperateType.GET_ADD_DEVICE_APPLY_LIST -> {
                val info = dataWrapper.data as AddDeviceApplyBean
                holder.mTvAddDeviceDetail.text = "${info.username} 申请添加 ${info.address}"
                holder.mTvAddDeviceDescription.text = info.devmsg
                holder.data = dataWrapper
            }
            DeviceListActivity.OperateType.GET_DEVICE -> {
                val info = dataWrapper.data as DeviceInfo
                holder.mTvName.text = info.address
                holder.data = dataWrapper
            }
        }
    }
}

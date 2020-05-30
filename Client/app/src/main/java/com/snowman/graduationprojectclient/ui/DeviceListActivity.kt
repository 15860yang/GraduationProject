package com.snowman.graduationprojectclient.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snowman.graduationprojectclient.R
import com.snowman.graduationprojectclient.manager.UserManager
import com.snowman.graduationprojectclient.remote.remoteservice.CallBack
import com.snowman.graduationprojectclient.remote.remoteservice.RemoteService
import com.snowman.graduationprojectclient.ui.base.BaseActivity
import com.snowman.graduationprojectclient.adapter.SnowManRvAdapter
import com.snowman.graduationprojectclient.bean.*
import com.snowman.graduationprojectclient.utils.log

class DeviceListActivity : BaseActivity() {

    companion object {
        const val OPERATE_TYPE = "operate_type"
    }

    enum class OperateType(var value: Int) {
        SELECT_DEVICE_SHOW_DATA(1), MANAGER_DEICE(2),
        GET_ADMIN_APPLY_LIST(4), GET_ADD_DEVICE_APPLY_LIST(5), GET_DEVICE(6)
    }

    private lateinit var mToolbar: Toolbar
    private lateinit var mRvDeviceList: RecyclerView
    private var mDataList: ArrayList<DataWrapper<Any>> = ArrayList()
    private lateinit var mRvAdapter: SnowManRvAdapter

    private var operateType: OperateType = OperateType.SELECT_DEVICE_SHOW_DATA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
        initArgs()
        initView()
        initData()
        setUpView()
    }

    private fun initArgs() {
        operateType = when (intent.getIntExtra(OPERATE_TYPE, 1)) {
            OperateType.SELECT_DEVICE_SHOW_DATA.value -> OperateType.SELECT_DEVICE_SHOW_DATA
            OperateType.MANAGER_DEICE.value -> OperateType.MANAGER_DEICE
            OperateType.GET_ADMIN_APPLY_LIST.value -> OperateType.GET_ADMIN_APPLY_LIST
            OperateType.GET_ADD_DEVICE_APPLY_LIST.value -> OperateType.GET_ADD_DEVICE_APPLY_LIST
            OperateType.GET_DEVICE.value -> OperateType.GET_DEVICE
            else -> OperateType.SELECT_DEVICE_SHOW_DATA
        }
    }

    private fun initView() {
        mRvDeviceList = findViewById(R.id.rv_select_device_device_list)
        mToolbar = findViewById(R.id.tb_device_list_toolbar)
        mRvAdapter = SnowManRvAdapter(this, mDataList, operateType)
        mRvDeviceList.adapter = mRvAdapter
        mRvDeviceList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        mToolbar.setNavigationOnClickListener { finish() }

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.device_item_decoration)!!)
        mRvDeviceList.addItemDecoration(divider)
    }

    private fun initData() {
        when (operateType) {
            //获取所有的设备列表
            OperateType.GET_DEVICE -> {
                RemoteService.getDeviceList(
                    UserManager.instance.userInfo.uuid,
                    object : CallBack<List<DeviceInfo>> {
                        override fun success(data: List<DeviceInfo>?) {
                            fillData(data)
                        }

                        override fun failed(res: String?) {
                        }
                    })
            }
            //获取管理员申请列表
            OperateType.GET_ADMIN_APPLY_LIST -> {
                RemoteService.getAdminApplyList(UserManager.instance.userInfo.uuid,
                    object : CallBack<List<ApplyAdminBean>> {
                        override fun success(data: List<ApplyAdminBean>?) {
                            fillData(data)
                        }

                        override fun failed(res: String?) {
                        }

                    })
            }
            //获取添加设备申请列表
            OperateType.GET_ADD_DEVICE_APPLY_LIST -> {
                RemoteService.getDevApplyList(UserManager.instance.userInfo.uuid,
                    object : CallBack<List<AddDeviceApplyBean>> {
                        override fun success(data: List<AddDeviceApplyBean>?) {
                            fillData(data)
                        }

                        override fun failed(res: String?) {

                        }
                    })
            }
            //获取可管理的设备列表
            OperateType.MANAGER_DEICE -> {
                RemoteService.getManagerDeviceList(UserManager.instance.userInfo.uuid,
                    object : CallBack<List<DeviceManagerBean>> {
                        override fun success(data: List<DeviceManagerBean>?) {
                            fillData(data)
                        }

                        override fun failed(res: String?) {

                        }
                    })
            }
            //获取可查看数据的所有设备
            OperateType.SELECT_DEVICE_SHOW_DATA -> {
                RemoteService.getBindDeviceList(UserManager.instance.userInfo.uuid,
                    object : CallBack<List<DeviceManagerBean>> {
                        override fun success(data: List<DeviceManagerBean>?) {
                            fillData(data)
                        }

                        override fun failed(res: String?) {
                        }
                    })
            }
        }
    }

    private fun fillData(data: List<Any>?) {
        if (data == null) {
            return
        }
        data.all {
            mDataList.add(DataWrapper(it, false))
            true
        }
        runOnUiThread { mRvAdapter.notifyDataSetChanged() }
    }

    private fun setUpView() {
        mToolbar.title = when (operateType) {
            OperateType.SELECT_DEVICE_SHOW_DATA -> "选择查看的设备"
            OperateType.MANAGER_DEICE -> "管理设备"
            OperateType.GET_ADMIN_APPLY_LIST -> "查看管理员权限申请列表"
            OperateType.GET_ADD_DEVICE_APPLY_LIST -> "查看添加设备申请列表"
            OperateType.GET_DEVICE -> "查看设备"
        }
    }
}

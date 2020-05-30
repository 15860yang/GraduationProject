package com.snowman.graduationprojectclient.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.snowman.graduationprojectclient.R
import com.snowman.graduationprojectclient.bean.GeneralResponse
import com.snowman.graduationprojectclient.bean.ValidTimeResponse
import com.snowman.graduationprojectclient.manager.UserManager
import com.snowman.graduationprojectclient.remote.remoteservice.CallBack
import com.snowman.graduationprojectclient.remote.remoteservice.RemoteService
import com.snowman.graduationprojectclient.ui.DeviceListActivity

class AdminActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBtDeviceManager: Button
    private lateinit var mBtGetAdminApplyList: Button
    private lateinit var mBtGetApplyAddDeviceList: Button
    private lateinit var mBtSetDataValidTime: Button
    private lateinit var mLlDataValidTime: LinearLayout
    private lateinit var mTvDataValidTime: TextView

    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        initView()
        setUpView()
    }

    private fun initView() {
        mBtDeviceManager = findViewById(R.id.bt_admin_device_manager)
        mBtGetAdminApplyList = findViewById(R.id.bt_admin_get_admin_apply_list)
        mBtGetApplyAddDeviceList = findViewById(R.id.bt_damin_get_add_device_apply_list)
        mBtSetDataValidTime = findViewById(R.id.bt_admin_set_data_valid_time)
        mLlDataValidTime = findViewById(R.id.ll_set_data_valid_time_admin)
        mTvDataValidTime = findViewById(R.id.tv_data_valid_time)
        mToolbar = findViewById(R.id.tb_admin_toolbar)

        setSupportActionBar(mToolbar)
        mToolbar.setNavigationOnClickListener {
            finish()
        }

        mBtDeviceManager.setOnClickListener(this)
        mBtGetAdminApplyList.setOnClickListener(this)
        mBtGetApplyAddDeviceList.setOnClickListener(this)
        mBtSetDataValidTime.setOnClickListener(this)
    }

    private fun setUpView() {
        mBtDeviceManager.visibility = View.GONE
        mBtGetAdminApplyList.visibility = View.GONE
        mBtGetApplyAddDeviceList.visibility = View.GONE
        mLlDataValidTime.visibility = View.GONE

        when (UserManager.instance.identityLevel.value) {
            UserManager.IdentityLevelType.SUPER_ADMIN.value -> {
                mBtGetAdminApplyList.visibility = View.VISIBLE
                mBtGetApplyAddDeviceList.visibility = View.VISIBLE
                mLlDataValidTime.visibility = View.VISIBLE
            }
            UserManager.IdentityLevelType.ADMIN.value -> {
                mBtDeviceManager.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.bt_admin_device_manager -> adminOperate(DeviceListActivity.OperateType.MANAGER_DEICE)
            R.id.bt_admin_get_admin_apply_list -> adminOperate(DeviceListActivity.OperateType.GET_ADMIN_APPLY_LIST)
            R.id.bt_damin_get_add_device_apply_list -> adminOperate(DeviceListActivity.OperateType.GET_ADD_DEVICE_APPLY_LIST)
            R.id.bt_admin_set_data_valid_time -> setDataValidTime()
        }
    }

    private fun setDataValidTime() {
        RemoteService.updateValidTime(
            UserManager.instance.userInfo.uuid,
            "",
            "",
            object : CallBack<ValidTimeResponse> {
                override fun success(data: ValidTimeResponse?) {

                }

                override fun failed(res: String?) {
                }

            })
    }

    private fun adminOperate(
        operateType: DeviceListActivity.OperateType,
        invokeIntent: (Intent) -> Unit = {}
    ) {
        startActivity(DeviceListActivity::class.java) {
            it.putExtra(DeviceListActivity.OPERATE_TYPE, operateType.value)
            invokeIntent(it)
        }
    }
}

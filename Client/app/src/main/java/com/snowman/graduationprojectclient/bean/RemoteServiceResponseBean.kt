package com.snowman.graduationprojectclient.bean

import com.snowman.graduationprojectclient.adapter.base.BaseItemBean

/**
 * 设备数据类
 */
data class DeviceInfo(var address: String, var devid: String)

/**
 * RecyclerView 子项通用包装类
 */
data class DataWrapper<D>(var data: D, var selected: Boolean = false) : BaseItemBean()

/**
 * 更新设备数据有效期限Response类
 */
data class ValidTimeResponse(var time: String)

/**
 * 管理员申请列表子项类
 */
data class ApplyAdminBean(val uuid: String, val adminMsg: String)

/**
 * 添加设备申请列表子项bean
 */
data class AddDeviceApplyBean(
    var username: String,
    var uuid: String,
    val devid: String,
    val address: String,
    val devmsg: String
)

/**
 * 添加设备申请列表子项类
 */
data class AddDeviceUser(val uuid: String, val devid: String, val devmsg: String)

/**
 * 通用远程接口调用Response类
 */
data class GeneralResponse(var aBoolean: Boolean, val msg: String)

data class DeviceManagerBean(var uuid: String, var devid: String)

package com.snowman.wifi.bean

/**
 * 设备数据类
 */
data class DeviceInfo(var password: String, var address: String, var devid: String)

data class ResponseRemote<T>(var aBoolean: Boolean, var data: T)

/**
 * 通用远程接口调用Response类
 */
data class GeneralResponse(var boolean: Boolean, val msg: String)


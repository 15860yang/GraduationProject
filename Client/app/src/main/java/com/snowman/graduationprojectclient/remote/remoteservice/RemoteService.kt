package com.snowman.graduationprojectclient.remote.remoteservice

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.snowman.graduationprojectclient.Constant.Remote.SERVER_BASE_URL
import com.snowman.graduationprojectclient.bean.*
import com.snowman.graduationprojectclient.utils.log
import okhttp3.*
import java.io.IOException

class RemoteService {
    companion object {
        private val client = OkHttpClient()

        fun login(userNumber: String, password: String, callback: CallBack<UserInfo>) {
            val formBody = FormBody.Builder()
                .add("uuid", userNumber)
                .add("password", password)
                .build()
            remoteRequest(formBody, "/baseUser/login", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    val userInfo = gson.fromJson(response.data.toString(), UserInfo::class.java)
                    log("登录成功：call.body = $userInfo")
                    callback.success(userInfo)
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun register(userName: String, password: String, callback: CallBack<UserInfo>) {
            val formBody = FormBody.Builder()
                .add("username", userName)
                .add("password", password)
                .build()
            remoteRequest(formBody, "/baseUser/register", object : CallBack<String> {
                override fun success(data: String?) {
                    log("data = $data")
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val updateTime =
                            gson.fromJson(response.data.toString(), UserInfo::class.java)
                        log("注册成功：call.body = $updateTime")
                        callback.success(updateTime)
                    } else {
                        log("注册失败：msg = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("网络错误 $res")
                }
            })
        }

        fun getDevPastData(
            callback: CallBack<GeneralResponse>
        ) {
            val formBody = FormBody.Builder()
                .add("uuid", "123456")
                .add("devid", "123456")
                .add("datatype", "1")
                .build()
            remoteRequest(formBody, "/user/getDevPastData", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val updateTime = gson.fromJson(
                        data,
                        GeneralResponse::class.java
                    )
                    log("处理添加设备请求 请求成功：call.body = $updateTime")
                    callback.success(updateTime)
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        /**
         * datatype: 1 -> 温度，2->湿度
         */
        fun updateDeviceConfig(
            uuid: String,
            devId: String,
            dataType: String,
            maxValue: String,
            minValue: String,
            callback: CallBack<GeneralResponse>
        ) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .add("devid", devId)
                .add("datatype", dataType)
                .add("max", maxValue)
                .add("min", minValue)
                .build()
            remoteRequest(formBody, "/admin/updateDevData", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = GsonBuilder().create()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val res = gson.fromJson(
                            response.data.toString(),
                            GeneralResponse::class.java
                        )
                        log("设备管理更新数据成功：call.body = $res")
                        callback.success(res)
                    } else {
                        log("设备管理更新数据失败：call.body = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun updateValidTime(
            uuid: String,
            number: String,
            timeType: String,
            callback: CallBack<ValidTimeResponse>
        ) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .add("number", number)
                .add("timeType", timeType)
                .build()
            remoteRequest(formBody, "/superUser/updateValidTime", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val updateTime = gson.fromJson(
                        data,
                        ValidTimeResponse::class.java
                    )
                    log("更改数据存储期限成功：call.body = $updateTime")
                    callback.success(updateTime)
                }

                override fun failed(res: String?) {
                    log("更改数据存储期限失败 $res")
                }
            })
        }

        fun getAdminApplyList(uuid: String, callback: CallBack<List<ApplyAdminBean>>) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .build()
            remoteRequest(formBody, "/superUser/getAdminApplyList", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val adminApplyList = gson.fromJson<List<ApplyAdminBean>>(
                            response.data.toString(),
                            object : TypeToken<List<ApplyAdminBean>>() {}.type
                        )
                        log("获取列表请求成功：call.body = $adminApplyList")
                        callback.success(adminApplyList)
                    } else {
                        log("获取列表失败  = ${response.data.toString()}")
                    }

                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        /**
         * 处理管理员申请接口
         *
         * flag :1 -> 表示同意， 2 表示不同意
         */
        fun handleAdminApply(
            superUuid: String,
            uuid: String,
            flag: Boolean,
            callback: CallBack<GeneralResponse>
        ) {
            val formBody = FormBody.Builder()
                .add("spueruuid", superUuid)
                .add("uuid", uuid)
                .add("flag", if (flag) "1" else "2")
                .build()
            remoteRequest(formBody, "/superUser/handleAdminApply", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = GsonBuilder().create()
                    val response = gson.fromJson(data, RespondData::class.java)
                    log(response.data.toString())
                    val handleAdminApply =
                        gson.fromJson(response.data.toString(), GeneralResponse::class.java)
                    handleAdminApply.aBoolean = response.aBoolean
                    log("处理管理员申请请求成功：call.body = $handleAdminApply")
                    callback.success(handleAdminApply)
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun getDevApplyList(uuid: String, callback: CallBack<List<AddDeviceApplyBean>>) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .build()
            remoteRequest(formBody, "/superUser/getDevApplyList", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val deviceApplyList = gson.fromJson<List<AddDeviceApplyBean>>(
                            response.data.toString(),
                            object : TypeToken<List<AddDeviceApplyBean>>() {}.type
                        )
                        log("获取申请添加设备列表请求成功：call.body = $deviceApplyList")
                        callback.success(deviceApplyList)
                    } else {
                        log("获取申请添加设备列表请求失败：call.body = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun handleAddDevApply(
            superUuid: String,
            uuid: String,
            devId: String,
            agree: Boolean,
            callback: CallBack<String>
        ) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .add("spueruuid", superUuid)
                .add("flag", if (agree) "1" else "2")
                .add("devid", devId)
                .build()
            remoteRequest(formBody, "/superUser/handleDevApply", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val applyAdmin = gson.fromJson(
                            response.data.toString(),
                            GeneralResponse::class.java
                        )
                        log("处理添加设备请求成功：call.body = $applyAdmin")
                        callback.success(applyAdmin.msg)
                    } else {
                        log("处理添加设备请求成功：call.body = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun unbindDevice(callback: CallBack<GeneralResponse>) {
            val formBody = FormBody.Builder()
                .add("uuid", "123456")
                .add("boolean", "123456")
                .add("devid", "")
                .build()
            remoteRequest(formBody, "/superUser/unbind", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val applyAdmin = gson.fromJson(
                        data,
                        GeneralResponse::class.java
                    )
                    log("解绑设备请求成功：call.body = $applyAdmin")
                    callback.success(applyAdmin)
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun applyToAreaManager(
            uuid: String,
            adminMsg: String,
            callback: CallBack<String>
        ) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .add("adminMsg", adminMsg)
                .build()
            remoteRequest(formBody, "/user/applyAdmin", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val res =
                            gson.fromJson(response.data.toString(), GeneralResponse::class.java)
                        log("申请管理员成功：call.body = $res")
                        callback.success(res.msg)
                    } else {
                        log("申请管理员失败：call.body = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }


        fun getDeviceList(uuid: String, callBack: CallBack<List<DeviceInfo>>) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .build()
            remoteRequest(formBody, "/user/getDevId", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = GsonBuilder().create()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val deviceList = gson.fromJson<List<DeviceInfo>>(
                            response.data.toString(),
                            object : TypeToken<List<DeviceInfo>>() {}.type
                        )
                        log("获取设备列表请求成功：call.body = $deviceList")
                        callBack.success(deviceList)
                    } else {
                        callBack.success(null)
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun addDevice(
            uuid: String,
            userName: String,
            address: String,
            devId: String,
            devMsg: String,
            callBack: CallBack<String>
        ) {
            val formBody = FormBody.Builder()
                .add("devid", devId)
                .add("username", userName)
                .add("address", address)
                .add("uuid", uuid)
                .add("devmsg", devMsg)
                .build()
            remoteRequest(formBody, "/user/addDev", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val res =
                            gson.fromJson(response.data.toString(), GeneralResponse::class.java)
                        log("添加设备申请成功：call.body = $res")
                        callBack.success(res.msg)
                    } else {
                        log("添加设备申请失败：call.body = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        private fun remoteRequest(formBody: FormBody, path: String, callBack: CallBack<String>) {
            val request = Request.Builder()
                .url("${SERVER_BASE_URL}${path}")
                .post(formBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callBack.failed(e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = response.body?.string()
                    log("请求成功：call.body = ${responseJson.toString()}")
                    callBack.success(responseJson)
                }
            })
        }

        fun getManagerDeviceList(uuid: String, callBack: CallBack<List<DeviceManagerBean>>) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .build()
            remoteRequest(formBody, "/admin/selOperate", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val res =
                            gson.fromJson<List<DeviceManagerBean>>(
                                response.data.toString(),
                                object : TypeToken<List<DeviceManagerBean>>() {}.type
                            )
                        log("获取可管理设备列表成功：call.body = $res")
                        callBack.success(res)
                    } else {
                        log("获取可管理设备列表失败：call.body = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun getBindDeviceList(uuid: String, callBack: CallBack<List<DeviceManagerBean>>) {
            val formBody = FormBody.Builder()
                .add("uuid", uuid)
                .build()
            remoteRequest(formBody, "/user/selDevBind", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, RespondData::class.java)
                    if (response.aBoolean) {
                        val res =
                            gson.fromJson<List<DeviceManagerBean>>(
                                response.data.toString(),
                                object : TypeToken<List<DeviceManagerBean>>() {}.type
                            )
                        log("获取已绑定设备列表成功：call.body = $res")
                        callBack.success(res)
                    } else {
                        log("获取已绑定设备列表失败：call.body = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }
    }
}
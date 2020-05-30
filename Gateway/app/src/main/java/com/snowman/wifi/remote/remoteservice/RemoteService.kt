package com.snowman.wifi.remote.remoteservice

import com.google.gson.Gson
import com.snowman.wifi.Constant
import com.snowman.wifi.utils.log
import com.snowman.wifi.Constant.Remote.WEB_SOCKET_SERVER_URL
import com.snowman.wifi.bean.DeviceInfo
import com.snowman.wifi.bean.GeneralResponse
import com.snowman.wifi.bean.ResponseRemote
import okhttp3.*
import java.io.IOException

class RemoteService {
    companion object {
        private val client = OkHttpClient()

        fun login(userNumber: String, password: String, callback: CallBack<DeviceInfo>) {
            val formBody = FormBody.Builder()
                .add("devid", userNumber)
                .add("password", password)
                .build()
            remoteRequest(formBody, "/device/devicelogin", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, ResponseRemote::class.java)
                    if (response.aBoolean) {
                        val deviceInfo = gson.fromJson(response.data.toString(), DeviceInfo::class.java)
                        log("登录成功：call.body = $deviceInfo")
                        callback.success(deviceInfo)
                    } else {
                        log("登录失败：msg = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("请求失败 $res")
                }
            })
        }

        fun register(userName: String, password: String, callback: CallBack<DeviceInfo>) {
            val formBody = FormBody.Builder()
                .add("address", userName)
                .add("password", password)
                .build()
            remoteRequest(formBody, "/device/deviceregister", object : CallBack<String> {
                override fun success(data: String?) {
                    val gson = Gson()
                    val response = gson.fromJson(data, ResponseRemote::class.java)
                    if (response.aBoolean) {
                        val deviceInfo =
                            gson.fromJson(response.data.toString(), DeviceInfo::class.java)
                        log("注册成功：call.body = $deviceInfo")
                        callback.success(deviceInfo)
                    } else {
                        log("注册失败：msg = ${response.data.toString()}")
                    }
                }

                override fun failed(res: String?) {
                    log("网络错误 $res")
                    callback.failed("网络错误 $res")
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


        private fun remoteRequest(formBody: FormBody, path: String, callBack: CallBack<String>) {
            val request = Request.Builder()
                .url("${Constant.Remote.SERVER_BASE_URL}${path}")
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
    }
}
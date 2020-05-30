package com.snowman.wifi.manager

import com.snowman.wifi.bean.DeviceInfo

class UserManager {

    lateinit var deviceInfo: DeviceInfo

    companion object {
        val instance by lazy {
            UserManager()
        }
    }

    enum class IdentityLevelType(var value: Int) {
        GENERAL_USER(2)
    }
}

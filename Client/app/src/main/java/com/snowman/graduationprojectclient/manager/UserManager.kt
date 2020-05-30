package com.snowman.graduationprojectclient.manager

import androidx.lifecycle.MutableLiveData
import com.snowman.graduationprojectclient.bean.UserInfo

class UserManager {

    lateinit var userInfo: UserInfo

    var identityLevel: MutableLiveData<Int> = MutableLiveData()
    val nowDisplayDeviceId: MutableLiveData<String> = MutableLiveData()

    companion object {
        val instance by lazy {
            UserManager().apply {
                identityLevel.postValue(IdentityLevelType.GENERAL_USER.value)
            }
        }
    }

    enum class IdentityLevelType(var value: Int) {
        ADMIN(2), GENERAL_USER(3), SUPER_ADMIN(1)
    }
}
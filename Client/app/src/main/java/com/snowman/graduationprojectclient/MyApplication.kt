package com.snowman.graduationprojectclient

import android.app.Application
import com.snowman.graduationprojectclient.utils.SpUtil

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SpUtil.initData(this)
    }
}

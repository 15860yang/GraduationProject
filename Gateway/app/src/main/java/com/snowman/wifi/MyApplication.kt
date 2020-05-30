package com.snowman.wifi

import android.app.Application
import com.snowman.wifi.utils.SpUtil

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SpUtil.initData(this)
    }
}

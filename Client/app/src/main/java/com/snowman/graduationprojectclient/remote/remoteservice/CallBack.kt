package com.snowman.graduationprojectclient.remote.remoteservice

interface CallBack<T> {
    fun success(data: T?)
    fun failed(res: String?)
}

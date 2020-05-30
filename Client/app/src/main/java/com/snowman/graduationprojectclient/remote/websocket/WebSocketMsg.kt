package com.snowman.graduationprojectclient.remote.websocket

data class WebSocketMsgBean<D>(var value: Int, var data: D, var flag: Int = 0)
enum class WebSocketMsgType(var value: Int) {
    ORDINARY(1), PICTURE(3)
}

enum class WebSocketMsgFlagType(val value: Int) {
    START(1), END(2)
}

package com.snowman.wifi.remote.websocket

data class WebSocketMsgBean<D>(var value: Int, var data: D, var flag: Int = 0)
enum class WebSocketMsgType(var value: Int) {
    ORDINARY(1), PICTURE(3)
}

enum class WebSocketMsgFlagType(val value: Int) {
    START(1), END(2)
}

data class OrdinaryData(
    var humidity: String,
    var temperature: String,
    var lightIntensity: String,
    var rainfall: String,
    var flame: Boolean,
    var smoke: Boolean
)

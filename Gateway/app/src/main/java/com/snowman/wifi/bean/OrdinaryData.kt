package com.snowman.wifi.bean

data class OrdinaryData(
    var humidity1: Int,
    var humidity2: Int,
    var temperature1: Int,
    var temperature2: Int,
    var lightIntensity: Int,
    var rainfall: Int,
    var flame: Boolean,
    var smoke: Boolean,
    var electricity: Boolean
)

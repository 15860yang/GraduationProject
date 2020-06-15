package com.snowman.graduationprojectclient.bean

data class OrdinaryData(
    var humidity: Float = 1.0f,
    var temperature: Float = 1f,
    var lightIntensity: Int = 1,
    var rainfall: Int = 1,
    var flame: Boolean = false,
    var smoke: Boolean = false,
    var electricity: Boolean = false
)

package com.snowman.graduationprojectclient.bean

data class OrdinaryData(
    var humidity: Int,
    var temperature: Int,
    var lightIntensity: Int,
    var rainfall: Int,
    var flame: Boolean,
    var smoke: Boolean,
    var electricity: Boolean
)

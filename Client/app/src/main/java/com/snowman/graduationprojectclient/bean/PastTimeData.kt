package com.snowman.graduationprojectclient.bean

import java.sql.Timestamp

data class PastTimeData<D>(var value: Int, var flag: Int, var data: D)

data class PastTimeTemData(var temperature: String = "", var time: Timestamp? = null) :
    BasePastTimeData()

data class PastTimeHumData(var humidity: String = "", var time: Timestamp? = null) :
    BasePastTimeData()

data class PastTimeTPictureData(var type: Int) : BasePastTimeData()

open class BasePastTimeData

/**
 * flag:3->picture,1->温度，2->湿度
 */
data class PastTimeQueryData(var flag: Int, var timebegin: Timestamp, var timeend: Timestamp)

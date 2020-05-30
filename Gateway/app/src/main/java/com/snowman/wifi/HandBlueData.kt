package com.snowman.wifi

import android.graphics.Bitmap
import com.snowman.wifi.bean.OrdinaryData
import com.snowman.wifi.utils.log
import java.security.PrivateKey
import java.util.concurrent.LinkedBlockingQueue

class HandBlueData(
    private var blueData: LinkedBlockingQueue<Byte>,
    private val logMessage: (msg: String) -> Unit,
    private val pictureMessage: (picture: Bitmap) -> Unit,
    private val ordinaryMessage: (msg: OrdinaryData) -> Unit,
    private val byteArrayMsg: (byteArrayMsg1: ByteArray, byteArrayMsg2: ByteArray) -> Unit
) : Thread() {
    var pictureArray: IntArray? = null
    var arrIndex = 0
    var width = 0
    var height = 0

    var pictureByteArray1: ByteArray? = null
    var pictureByteArray2: ByteArray? = null
    override fun run() {
        super.run()
        var checkIndex = 0
        while (true) {
            val b1 = blueData.take()
            val b2 = blueData.take()
            if (pictureArray == null && b1.toInt() == '*'.toInt() && b2.toInt() == '#'.toInt()) {
                val b3 = blueData.take()
                val b4 = blueData.take()
                log("判断是否头信息 b3 = $b3 b4 = $b4")
                if (b3.toInt() == '*'.toInt() && b4.toInt() == '#'.toInt()) {
                    log("开始创建接收图像头数据")
                    var a: Byte = blueData.take()
                    log("接收到字符3 a = ${a.toChar()}")
                    while (a.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        width = width * 10 + a - '0'.toInt()
                        a = blueData.take()
                        log("接收到字符4 a = ${a.toChar()}")
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    a = blueData.take()
                    log("接收到字符5 a = ${a.toChar()}")
                    while (a.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        height = height * 10 + a - '0'.toInt()
                        a = blueData.take()
                        log("接收到字符6 a = ${a.toChar()}")
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    if (width > 500 || height > 500) {
                        log("图片规格解析出错")
                        continue
                    }
                    log("创建数组，准备填充数据 width = $width height = $height")

                    pictureByteArray1 = ByteArray(width * height)
                    pictureByteArray2 = ByteArray(width * height)
                    pictureArray = IntArray(width * height)
                } else if (b3.toInt() == '#'.toInt() && b4.toInt() == '*'.toInt()) {
                    //"*##*%d#%d#%d#%d#%d#%d#%d**"
                    var ti = 0 //温度的整数
                    var temp = blueData.take()
                    while (temp.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        ti = ti * 10 + temp - '0'.toInt()
                        temp = blueData.take()
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    var td = 0 //温度的小数
                    temp = blueData.take()
                    while (temp.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        td = td * 10 + temp - '0'.toInt()
                        temp = blueData.take()
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    var hi = 0 //湿度的整数
                    temp = blueData.take()
                    while (temp.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        hi = hi * 10 + temp - '0'.toInt()
                        temp = blueData.take()
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    var hd = 0 //温度的小数
                    temp = blueData.take()
                    while (temp.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        hd = hd * 10 + temp - '0'.toInt()
                        temp = blueData.take()
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    var lightIntensity = 0 //光照强度
                    temp = blueData.take()
                    while (temp.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        lightIntensity = lightIntensity * 10 + temp - '0'.toInt()
                        temp = blueData.take()
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    var rainfall = 0 //雨量
                    temp = blueData.take()
                    while (temp.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        rainfall = rainfall * 10 + temp - '0'.toInt()
                        temp = blueData.take()
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    var comprehensive = 0
                    temp = blueData.take()
                    while (temp.toInt() != '#'.toInt()) {
                        if (checkIndex++ > 5) {
                            log("数据出错")
                            break
                        }
                        comprehensive = comprehensive * 10 + temp - '0'.toInt()
                        temp = blueData.take()
                    }
                    if (checkIndex > 5) {
                        continue
                    } else checkIndex = 0
                    var a = OrdinaryData(
                        hi, hd, ti, td,
                        lightIntensity,
                        rainfall,
                        comprehensive.and(0x0001) == 1,
                        comprehensive.and(0x0002) == 2,
                        comprehensive.and(0x0004) == 4
                    )
                    log(a.toString())
                    ordinaryMessage(a)
                }
            } else if (pictureArray != null && (arrIndex == width * height || (b1.toInt() == '#'.toInt() && b2.toInt() == '#'.toInt()))) {
                log("接收到字符7 b1 = ${b1.toChar()}  b2 = ${b2.toChar()}")
                log("转换图片 arrIndex = $arrIndex")
                if (b1.toInt() == '#'.toInt() && b2.toInt() == '#'.toInt()) {
                    log("图片正常")
                } else {
                    log("图片不正常 arrIndex = $arrIndex")
                }
                createBitmap()
                byteArrayMsg(pictureByteArray1!!, pictureByteArray2!!)
                arrIndex = 0
                width = 0
                height = 0
                pictureArray = null
                pictureByteArray1 = null
                pictureByteArray2 = null
            } else if (pictureArray != null) {
                if (arrIndex * 2 < pictureByteArray1!!.size) {
                    pictureByteArray1!![2 * arrIndex] = b1
                    pictureByteArray1!![2 * arrIndex + 1] = b2
                } else {
                    pictureByteArray2!![2 * arrIndex - pictureByteArray1!!.size] = b1
                    pictureByteArray2!![2 * arrIndex - pictureByteArray1!!.size + 1] = b2
                }
                val a = HandData.mergeByteToInt(b1, b2)
                pictureArray!![arrIndex++] = a
            }
        }
    }

    private fun createBitmap() {
        val bitmap = Bitmap.createBitmap(pictureArray!!, width, height, Bitmap.Config.RGB_565)
        pictureMessage(bitmap)
    }
}

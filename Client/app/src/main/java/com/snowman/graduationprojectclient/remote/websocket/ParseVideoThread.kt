package com.snowman.graduationprojectclient.remote.websocket

import com.snowman.graduationprojectclient.Constant.Code.BUFFER_LENGTH
import com.snowman.graduationprojectclient.utils.log
import com.snowman.graduationprojectclient.utils.videoutil.h264.H264Decoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.BlockingQueue

class ParseVideoThread : Thread() {

    lateinit var dataSource: BlockingQueue<ByteArray>
    lateinit var mH264Decoder: H264Decoder

    private var bytes = LinkedList<ByteArray>()
    var length = 0

    override fun run() {
        super.run()
        while (true) {
            val data = dataSource.take()
            when (String(data, 0, data.size, StandardCharsets.ISO_8859_1)) {
                "start" -> {
                    bytes.clear()
                    length = 0
                }
                "end" -> {
                    val videoFrame = ByteArray(length)
                    for (i in bytes.indices) {
                        System.arraycopy(
                            bytes[i], 0,
                            videoFrame, i * BUFFER_LENGTH, bytes[i].size
                        )
                    }
                    log("发送到解码器 res.length = " + videoFrame.size)
                    mH264Decoder.put(videoFrame)
                }
                else -> {
                    log("网络来的数据，data.length = " + data.size)
                    length += data.size
                    bytes.add(data)
                }
            }
        }
    }
}

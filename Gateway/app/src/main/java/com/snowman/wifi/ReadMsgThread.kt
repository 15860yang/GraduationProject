package com.snowman.wifi

import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class ReadMsgThread(
    private val socket: Socket,
    private val blueData: LinkedBlockingQueue<Byte>
) : Thread() {
    override fun run() {
        super.run()
        val inputStream = socket.getInputStream()
        val buffer = ByteArray(1024 * 8)
        while (true) {
            val length = inputStream.read(buffer)
            if (length > 0) {
                for (i in 0 until length) {
                    blueData.add(buffer[i])
                }
            }
        }
    }
}

package com.snowman.wifi.remote.websocket

import android.os.Handler
import com.snowman.wifi.remote.websocket.MyWebSocket.WebSocketReCreate

class WebSocketTools {
    private var mMyWebSocket: MyWebSocket? = null
    private val mHandler = Handler()
    private var c: WebSocketReCreate? = null
    fun startHeatBeat(
        myWebSocket: MyWebSocket?,
        c: WebSocketReCreate?
    ) {
        check(!(myWebSocket == null || myWebSocket.isClosed)) { "you should input a connected webSocket" }
        mMyWebSocket = myWebSocket
        this.c = c
        mHandler.post(heartBeatRunnable)
    }

    private val heartBeatRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mMyWebSocket != null) {
                if (mMyWebSocket!!.isClosed) {
                    reconnectWs()
                }
            } else {
                //如果client已为空，重新初始化websocket
                mMyWebSocket = c!!.initWebSocket()
            }
            //定时对长连接进行心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE)
        }
    }

    private fun reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable)
        object : Thread() {
            override fun run() {
                try {
                    //重连
                    mMyWebSocket!!.reconnectBlocking()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    companion object {
        private const val HEART_BEAT_RATE = 10 * 1000L //每隔10秒进行一次对长连接的心跳检测
    }
}
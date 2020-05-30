package com.snowman.wifi.remote.websocket

import com.snowman.wifi.utils.log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.charset.StandardCharsets

class MyWebSocket(serverUri: String) : WebSocketClient(URI.create(serverUri), Draft_6455()) {

    private var mConnectionStatusListener: ConnectionStatusListener? = null
    private var msgListener: ((String) -> Unit)? = null

    override fun onOpen(handshakedata: ServerHandshake) {
        if (mConnectionStatusListener != null) {
            mConnectionStatusListener!!.connectionSuccess(handshakedata)
        }
    }

    override fun onMessage(message: String) {
        log("从网络收到消息 : message,length = " + message.toByteArray(StandardCharsets.ISO_8859_1).size)
        msgListener?.let { it(message) }
    }

    override fun onClose(
        code: Int,
        reason: String,
        remote: Boolean
    ) {
        if (mConnectionStatusListener != null) {
            mConnectionStatusListener!!.connectionClosed(code, reason, remote)
        }
    }

    override fun onError(ex: Exception) {
        if (mConnectionStatusListener != null) {
            mConnectionStatusListener!!.connectionError(ex)
        }
    }

    fun setMsgListener(dataStream: (String) -> Unit) {
        msgListener = dataStream
    }

    fun setConnectSuccessListener(connectSuccessListener: ConnectionStatusListener?) {
        mConnectionStatusListener = connectSuccessListener
    }

    interface ConnectionStatusListener {
        fun connectionSuccess(handshakedata: ServerHandshake?)
        fun connectionError(tr: Throwable?)
        fun connectionClosed(
            code: Int,
            reason: String?,
            remote: Boolean
        )
    }

    interface WebSocketReCreate {
        fun initWebSocket(): MyWebSocket?
    }
}
package com.snowman.wifi

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.snowman.wifi.Constant.Remote.WEB_SOCKET_SERVER_URL
import com.snowman.wifi.bean.OrdinaryData
import com.snowman.wifi.bean.ResponseRemote
import com.snowman.wifi.manager.UserManager
import com.snowman.wifi.remote.websocket.*
import com.snowman.wifi.utils.EnvironmentalDigitalConversionUtils
import com.snowman.wifi.utils.log
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.handshake.ServerHandshake
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : BaseActivity(), MyWebSocket.WebSocketReCreate {

    private lateinit var mTvHumidity: TextView
    private lateinit var mTvTemperature: TextView
    private lateinit var mTvLightIntensity: TextView
    private lateinit var mTvFlame: TextView
    private lateinit var mTvSmoke: TextView
    private lateinit var mTvRainfall: TextView
    private lateinit var mElectricity: TextView
    private lateinit var mIVPicture: ImageView
    private lateinit var mTvInfo: TextView
    private lateinit var mDeviceStatus: TextView
    private lateinit var mDeviceName: TextView
    private lateinit var mAviTransmissionAnimation: AVLoadingIndicatorView

    private var remoteDataStreamLongLink: MyWebSocket? = null
    private var serverSocket: ServerSocket? = null
    private var ipAddress: String? = null

    private lateinit var mToolbar: Toolbar

    val blueData: LinkedBlockingQueue<Byte> = LinkedBlockingQueue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initServer()
        initWebSocket()
    }

    private fun initView() {
        mTvHumidity = findViewById(R.id.tv_main_humidity)
        mTvTemperature = findViewById(R.id.tv_main_temperature)
        mTvLightIntensity = findViewById(R.id.tv_main_light_intensity)
        mTvFlame = findViewById(R.id.tv_main_flame)
        mTvSmoke = findViewById(R.id.tv_main_smoke)
        mTvRainfall = findViewById(R.id.tv_main_rainfall)
        mElectricity = findViewById(R.id.tv_main_electricity)
        mToolbar = findViewById(R.id.tb_main_toolbar)
        mDeviceStatus = findViewById(R.id.tv_main_device_status)
        mDeviceName = findViewById(R.id.tv_main_device_name)
        mIVPicture = findViewById(R.id.iv_main_image)
        mTvInfo = findViewById(R.id.tv_main_user_info)
        setSupportActionBar(mToolbar)
        mDeviceName.text = UserManager.instance.deviceInfo.address
        mTvInfo.text =
            "uuid ${UserManager.instance.deviceInfo.devid} add = ${UserManager.instance.deviceInfo.address}"
        mAviTransmissionAnimation = findViewById(R.id.avi_main_transmission)
        mAviTransmissionAnimation.smoothToHide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.login_out -> {
                startActivity(LoginActivity::class.java)
                remoteDataStreamLongLink?.close()
                remoteDataStreamLongLink = null
                serverSocket?.close()
                serverSocket = null
                wifiSocket?.close()
                wifiSocket = null
                finish()
            }
        }
        return true
    }

    private fun initServer() {
        ipAddress = getLocalIpAddress()
        ipAddress?.let { createServer() }
    }

    override fun initWebSocket(): MyWebSocket? {
        remoteDataStreamLongLink =
            MyWebSocket(WEB_SOCKET_SERVER_URL + UserManager.instance.deviceInfo.devid)
        remoteDataStreamLongLink!!.setConnectSuccessListener(object :
            MyWebSocket.ConnectionStatusListener {
            override fun connectionSuccess(handshakedata: ServerHandshake?) {
                Looper.prepare()
                WebSocketTools().startHeatBeat(remoteDataStreamLongLink, this@MainActivity)
                remoteDataStreamLongLink!!.setMsgListener {
                    handRemoteLongLinkMsg(it)
                }
                log("连接成功")
                runOnUiThread { mAviTransmissionAnimation.smoothToShow() }
            }

            override fun connectionError(tr: Throwable?) {
            }

            override fun connectionClosed(
                code: Int,
                reason: String?,
                remote: Boolean
            ) {
                log("关闭连接")
                runOnUiThread { mAviTransmissionAnimation.smoothToHide() }
            }
        })
        remoteDataStreamLongLink!!.connect()
        return remoteDataStreamLongLink
    }

    private fun createServer() {
        mHandler.sendLogMessage("$ipAddress 等待连接")
        serverSocket = ServerSocket(Constant.SERVER_PORT)
        serverSocket?.let { ServerThread(it).start() }
    }

    enum class MsgType(var value: Int) {
        PICTURE(1), ORDINARY(2), LOG(3)
    }

    private val mHandler = Handler {
        when (it.what) {
            MsgType.ORDINARY.value -> {
                fillingOrdinary(it.obj as com.snowman.wifi.remote.websocket.OrdinaryData)
                true
            }
            MsgType.PICTURE.value -> {
                mIVPicture.setImageBitmap(it.obj as Bitmap)
                true
            }
            MsgType.LOG.value -> {
                mDeviceStatus.text = it.obj as String
                true
            }
            else -> true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAviTransmissionAnimation.smoothToHide()
    }

    private fun fillingOrdinary(ordinaryData: com.snowman.wifi.remote.websocket.OrdinaryData) {
        mTvHumidity.text = "${ordinaryData.humidity}%"
        mTvTemperature.text = "${ordinaryData.temperature} 度"
        EnvironmentalDigitalConversionUtils.ConversionNumberToLightIntensity(
            mTvLightIntensity,
            ordinaryData.lightIntensity.toInt()
        )
        EnvironmentalDigitalConversionUtils.ConversionNumberToRainFall(
            mTvRainfall,
            ordinaryData.rainfall.toInt()
        )
        mTvFlame.text = if (ordinaryData.flame) "异常" else "正常"
        mTvSmoke.text = if (ordinaryData.smoke) "异常" else "正常"
        mElectricity.text = if (ordinaryData.electricity) "异常" else "正常"
    }

    private fun getLocalIpAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            if (networkInterfaces != null) {
                while (networkInterfaces.hasMoreElements()) {
                    val interfaceElement = networkInterfaces.nextElement()
                    if (interfaceElement != null) {
                        val interfaceAddresses = interfaceElement.inetAddresses
                        while (interfaceAddresses.hasMoreElements()) {
                            val addressElement = interfaceAddresses.nextElement()
                            if (!addressElement.isLoopbackAddress) {
                                val ipAddress = addressElement.hostAddress
                                val split = ipAddress.split(".")
                                if (split.size == 4) {
                                    log("mobile address = $ipAddress")
                                    return ipAddress
                                }
                            }
                        }
                    }
                }
            } else {
                log("null")
                mHandler.sendLogMessage("未获取到本机ip")
            }
        } catch (ex: SocketException) {
            log(ex.toString())
            mHandler.sendLogMessage("系统服务出现故障，请重启再试")
        }
        return null
    }

    private fun handRemoteLongLinkMsg(data: String) {
        log("接收到：$data")//湿度正常
        when (data) {
            "7" -> {
                //降温
                log("降温")
                wifiSocket!!.getOutputStream().write("**CD##".toByteArray())
                return
            }
            "8" -> {
                //喷水
                log("喷水")
                wifiSocket!!.getOutputStream().write("**WS##".toByteArray())
                return
            }
            else -> {
                val gson = GsonBuilder().create()
                val res = gson.fromJson(data, ResponseRemote::class.java)
                when (res.data.toString()) {
                    "1" -> {
                        wifiSocket!!.getOutputStream().write("**TT##".toByteArray())
                        //温度过高
                        log("温度过高")
                        mTvTemperature.setTextColor(getColor(R.color.OrangeRed))
                    }
                    "2" -> {
                        wifiSocket!!.getOutputStream().write("**TT##".toByteArray())
                        //温度过低
                        log("温度过低")
                        mTvTemperature.setTextColor(getColor(R.color.SpringGreen2))
                    }
                    "3" -> {
                        wifiSocket!!.getOutputStream().write("**HH##".toByteArray())
                        //湿度过高
                        log("湿度过高")
                        mTvHumidity.setTextColor(getColor(R.color.OrangeRed))
                    }
                    "4" -> {
                        wifiSocket!!.getOutputStream().write("**HH##".toByteArray())
                        //湿度过低
                        log("湿度过低")
                        mTvHumidity.setTextColor(getColor(R.color.SpringGreen2))
                    }
                    "5" -> {
                        wifiSocket!!.getOutputStream().write("**00##".toByteArray())
                        //温度正常
                        log("温度正常")
                        mTvTemperature.setTextColor(getColor(R.color.whiteSmoke))
                    }
                    "6" -> {
                        //湿度正常
                        wifiSocket!!.getOutputStream().write("**11##".toByteArray())
                        log("湿度正常")
                        mTvHumidity.setTextColor(getColor(R.color.whiteSmoke))
                    }
                }
            }
        }
    }

    var wifiSocket: Socket? = null

    inner class ServerThread(private val serverSocket: ServerSocket) : Thread() {
        override fun run() {
            super.run()
            val gson = GsonBuilder().create()
            while (true) {
                wifiSocket = serverSocket.accept()
                mHandler.sendLogMessage("${wifiSocket!!.inetAddress.hostAddress} ${wifiSocket!!.port} 连接")
                log("${wifiSocket!!.inetAddress.hostAddress} ${wifiSocket!!.port} 连接")
                HandBlueData(blueData, {
                    mHandler.sendLogMessage(it)
                }, {
                    mHandler.sendPictureMessage(it)
                    //remoteDataStreamLongLink.send("")
                }, {
                    val od = OrdinaryData(
                        "${it.humidity1}.${it.humidity2}",
                        "${it.temperature1}.${it.temperature2}",
                        "${it.lightIntensity}",
                        "${it.rainfall}",
                        it.flame, it.smoke, it.electricity
                    )
                    val msg = WebSocketMsgBean(WebSocketMsgType.ORDINARY.value, od)
                    if (remoteDataStreamLongLink != null && !remoteDataStreamLongLink!!.isClosed) {
                        log("发送数据msg = $msg")
                        remoteDataStreamLongLink!!.send(gson.toJson(msg))
                    } else {
                        log("发送消息时发现有点尴尬")
                    }
                    mHandler.sendOrdinaryMessage(od)
                }, { p1, p2 ->
                    handlePictureByteArray(p1, p2)
                }).start()
                ReadMsgThread(wifiSocket!!, blueData).start()
            }
        }
    }

    private fun handlePictureByteArray(pictureArray1: ByteArray, pictureArray2: ByteArray) {
        val gson = Gson()
        var count = 0
        if (remoteDataStreamLongLink == null || remoteDataStreamLongLink!!.isClosed) {
            log("webSocket长连接被关闭")
            return
        }
        var flag = 3
        log("准备发送图片数据")
        while (count <= pictureArray1.size) {
            val offset =
                if (count + Constant.SLICE_LENGTH > pictureArray1.size) (pictureArray1.size - count) else Constant.SLICE_LENGTH
            val str = String(
                pictureArray1, count, offset,
                Charset.forName("ISO-8859-1")
            )
            count += Constant.SLICE_LENGTH
            val msg = WebSocketMsgBean(WebSocketMsgType.PICTURE.value, str)
            if (count == Constant.SLICE_LENGTH) {
                msg.flag = WebSocketMsgFlagType.START.value
            } else {
                msg.flag = flag++
            }
            val sss = gson.toJson(msg)
            log(" msg.flag = ${msg.flag} sss1.length = ${sss.length}")
            remoteDataStreamLongLink!!.send(sss)
        }
        count = 0
        while (count < pictureArray2.size) {
            var offset =
                if (count + Constant.SLICE_LENGTH > pictureArray2.size) (pictureArray2.size - count) else Constant.SLICE_LENGTH
            val str = String(
                pictureArray2, count,
                offset,
                Charset.forName("ISO-8859-1")
            )
            count += Constant.SLICE_LENGTH
            val msg = WebSocketMsgBean(WebSocketMsgType.PICTURE.value, str)
            if (count >= pictureArray2.size) {
                msg.flag = WebSocketMsgFlagType.END.value
            } else {
                msg.flag = flag++
            }
            val sss = gson.toJson(msg)
            log(" msg.flag = ${msg.flag} sss2.length = ${sss.length}")
            remoteDataStreamLongLink!!.send(sss)
        }
    }

    private fun Handler.sendLogMessage(obj: Any) {
        sendMessage(MsgType.LOG, obj)
    }

    private fun Handler.sendPictureMessage(obj: Any) {
        sendMessage(MsgType.PICTURE, obj)
    }

    private fun Handler.sendOrdinaryMessage(obj: com.snowman.wifi.remote.websocket.OrdinaryData) {
        sendMessage(MsgType.ORDINARY, obj)
    }

    private fun Handler.sendMessage(type: MsgType, obj: Any) {
        val msg = Message.obtain()
        msg.what = type.value
        msg.obj = obj
        sendMessage(msg)
    }
}

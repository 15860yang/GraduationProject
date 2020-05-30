package com.snowman.graduationprojectclient.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.snowman.graduationprojectclient.Constant
import com.snowman.graduationprojectclient.R
import com.snowman.graduationprojectclient.adapter.PastTimeDataAdapter
import com.snowman.graduationprojectclient.bean.*
import com.snowman.graduationprojectclient.remote.websocket.MyWebSocket
import com.snowman.graduationprojectclient.remote.websocket.WebSocketMsgFlagType
import com.snowman.graduationprojectclient.ui.base.BaseActivity
import com.snowman.graduationprojectclient.utils.ByteDataUtil
import com.snowman.graduationprojectclient.utils.log
import kotlinx.android.synthetic.main.activity_past_time_device_data.*
import org.java_websocket.handshake.ServerHandshake
import java.nio.charset.Charset
import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PastTimeDeviceDataActivity : BaseActivity() {

    private lateinit var mRvDeviceList: RecyclerView
    private lateinit var mRvAdapter: PastTimeDataAdapter

    private lateinit var mToolbar: Toolbar

    private val mDataList = ArrayList<BasePastTimeData>()

    private var remoteDataStreamLongLink: MyWebSocket? = null

    private val gson = GsonBuilder().create()

    private lateinit var devid: String
    private lateinit var type: PastTimeDataType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_time_device_data)
        initArgs()
        initView()
        initData()
    }

    private fun initArgs() {
        type = when (intent.getIntExtra(PAST_TIME_TYPE, 1)) {
            PastTimeDataType.PICTURE.value -> PastTimeDataType.PICTURE
            PastTimeDataType.HUMIDITY.value -> PastTimeDataType.HUMIDITY
            PastTimeDataType.TEMPERATURE.value -> PastTimeDataType.TEMPERATURE
            else -> PastTimeDataType.HUMIDITY
        }
        log("type = ${type.value}")
        devid = intent.getStringExtra(PAST_TIME_DEVICE_ID)!!
    }

    var send: Boolean = false

    private fun initData() {
        val s = "${Constant.Remote.WEB_SOCKET_PAST_TIME_URL}$devid"
        log("将要去连接来传输往期数据 $s")
        remoteDataStreamLongLink = MyWebSocket(s)
        remoteDataStreamLongLink!!.setConnectSuccessListener(object :
            MyWebSocket.ConnectionStatusListener {
            override fun connectionSuccess(handshakedata: ServerHandshake?) {
                log("连接成功")
                if (send) {
                    return
                }
                val pastTimeQueryData =
                    PastTimeQueryData(
                        type.value,
                        createTimeStamp("2020-05-26 17:30:51"),
                        createTimeStamp("2020-05-26 17:31:03")
                    )
                val str = JSON.toJSONString(pastTimeQueryData)
                log("will send $str")
                remoteDataStreamLongLink!!.send(str)
                send = true
            }

            override fun connectionError(tr: Throwable?) {
//                log("-----连接失败 $stackTraceString")
            }

            override fun connectionClosed(code: Int, reason: String?, remote: Boolean) {
                log("关闭连接")
            }
        })
        remoteDataStreamLongLink!!.setMsgListener {
            handRemoteLongLinkMsg(it)
        }
        remoteDataStreamLongLink!!.connect()

    }

    private fun createTimeStamp(dataString: String): Timestamp {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
        dateFormat.isLenient = false
        val destTimeZone = TimeZone.getTimeZone("Asia/Shanghai")
        dateFormat.timeZone = destTimeZone
        var timeDate: Date? = null
        try {
            timeDate = dateFormat.parse(dataString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Timestamp(timeDate!!.time)
    }

    var pictureArray: IntArray? = null
    var pictureArrayIndex = 0

    /**
     * [{"devid":"485312","id":44,"temperature":"23.6","time":1590485451000},{"devid":"485312","id":45,"temperature":"23.6","time":1590485463000}]
     */
    private fun handRemoteLongLinkMsg(data: String) {
        val res = gson.fromJson(data, PastTimeData::class.java)
        log("res:res.value = ${res.value}，res.data = ${res.data.toString()}")

        when (res.value) {
            //温度
            PastTimeDataType.TEMPERATURE.value -> {
                val temperatureList =
                    JSON.parseArray(res.data.toString(), PastTimeTemData::class.java)
                temperatureList?.all {
                    mDataList.add(it)
                    true
                }
                log("change ${mDataList.size}")
                runOnUiThread { mRvAdapter.notifyDataSetChanged() }
            }
            //湿度
            PastTimeDataType.HUMIDITY.value -> {
                val humidityList = JSON.parseArray(res.data.toString(), PastTimeHumData::class.java)
                humidityList?.all {
                    mDataList.add(it)
                    true
                }
                log("change ${mDataList.size}")
                runOnUiThread { mRvAdapter.notifyDataSetChanged() }
            }
        }


//        when {
//            res.flag == WebSocketMsgFlagType.START.value -> {
//                pictureArray = IntArray(71680)
//                log("创建数组")
//                val byteArray = (res.data as String).toByteArray(Charset.forName("ISO-8859-1"))
//                var i = 0
//                while (i + 1 < byteArray.size) {
//                    pictureArray!![pictureArrayIndex++] =
//                        ByteDataUtil.mergeByteToInt(byteArray[i], byteArray[i + 1])
//                    i += 2
//                }
//                log("加入完毕")
//            }
//            res.flag == WebSocketMsgFlagType.END.value -> {
//                val byteArray = (res.data as String).toByteArray(Charset.forName("ISO-8859-1"))
//                var i = 0
//                log("创建图片 pictureIndex = $pictureArrayIndex  byte.size = ${byteArray.size}")
//                while (i + 1 < byteArray.size) {
//                    pictureArray!![pictureArrayIndex++] =
//                        ByteDataUtil.mergeByteToInt(byteArray[i], byteArray[i + 1])
//                    i += 2
//                }
//
//                val bitmap = Bitmap.createBitmap(pictureArray!!, 320, 224, Bitmap.Config.RGB_565)
//                val msg = Message.obtain()
//                msg.obj = bitmap
//                mHandler.sendMessage(msg)
//                pictureArray = null
//                pictureArrayIndex = 0
//            }
//            pictureArray != null -> {
//                val byteArray = (res.data as String).toByteArray(Charset.forName("ISO-8859-1"))
//                var i = 0
//                while (i + 1 < byteArray.size) {
//                    pictureArray!![pictureArrayIndex++] =
//                        ByteDataUtil.mergeByteToInt(byteArray[i], byteArray[i + 1])
//                    i += 2
//                }
//            }
//        }
    }

    private fun initView() {
        mToolbar = findViewById(R.id.tb_past_time_data_toolbar)
        setSupportActionBar(mToolbar)
        mToolbar.title = when (type) {
            PastTimeDataType.TEMPERATURE -> {
                "往期温度数据"
            }
            PastTimeDataType.HUMIDITY -> {
                "往期湿度数据"
            }
            PastTimeDataType.PICTURE -> {
                "往期图像数据"
            }
        }
        mToolbar.setNavigationOnClickListener {
            finish()
        }

        mRvDeviceList = findViewById(R.id.rv_past_time_data_list)
        mRvDeviceList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRvAdapter = PastTimeDataAdapter(this, mDataList, type)
        mRvDeviceList.adapter = mRvAdapter
    }

    companion object {
        const val PAST_TIME_TYPE = "past_time_type"
        const val PAST_TIME_DEVICE_ID = "past_time_type_device_id"
    }

    enum class PastTimeDataType(var value: Int) {
        HUMIDITY(2), TEMPERATURE(1), PICTURE(3)
    }
}

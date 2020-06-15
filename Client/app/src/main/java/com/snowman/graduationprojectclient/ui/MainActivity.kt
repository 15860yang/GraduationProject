package com.snowman.graduationprojectclient.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import com.snowman.graduationprojectclient.Constant.Remote.WEB_SOCKET_SERVER_URL
import com.snowman.graduationprojectclient.R
import com.snowman.graduationprojectclient.bean.OrdinaryData
import com.snowman.graduationprojectclient.manager.UserManager
import com.snowman.graduationprojectclient.remote.remoteservice.CallBack
import com.snowman.graduationprojectclient.remote.remoteservice.RemoteService
import com.snowman.graduationprojectclient.remote.websocket.*
import com.snowman.graduationprojectclient.ui.base.AdminActivity
import com.snowman.graduationprojectclient.ui.base.BaseActivity
import com.snowman.graduationprojectclient.ui.login.LoginActivity
import com.snowman.graduationprojectclient.utils.ByteDataUtil
import com.snowman.graduationprojectclient.utils.EnvironmentalDigitalConversionUtils
import com.snowman.graduationprojectclient.utils.log
import com.snowman.graduationprojectclient.view.InputMsgDialog
import com.wang.avi.AVLoadingIndicatorView
import org.java_websocket.handshake.ServerHandshake
import java.nio.charset.Charset

class MainActivity : BaseActivity(), View.OnClickListener, MyWebSocket.WebSocketReCreate {
    private lateinit var mBtApplyAdmin: Button
    private lateinit var mBtAdmin: Button
    private lateinit var mBtGetRealTimeData: Button
    private lateinit var mToolbar: Toolbar
    private lateinit var mNowShowDevice: TextView
    private lateinit var mBtAddDevice: Button
    private lateinit var mTvUserInfo: TextView
    private var remoteDataStreamLongLink: MyWebSocket? = null
    private lateinit var mIvPictureDisPlayer: ImageView
    private lateinit var mAviTransmissionAnimation: AVLoadingIndicatorView

    private lateinit var mTvHumidity: TextView
    private lateinit var mTvTemperature: TextView
    private lateinit var mTvLightIntensity: TextView
    private lateinit var mTvFlame: TextView
    private lateinit var mTvSmoke: TextView
    private lateinit var mTvRainfall: TextView
    private lateinit var mElectricity: TextView
    private lateinit var mBtCoolTem: Button
    private lateinit var mBtWaterSpray: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        startUpView()
    }

    private fun initView() {

        mTvHumidity = findViewById(R.id.tv_main_humidity)
        mTvTemperature = findViewById(R.id.tv_main_temperature)
        mTvLightIntensity = findViewById(R.id.tv_main_light_intensity)
        mTvFlame = findViewById(R.id.tv_main_flame)
        mTvSmoke = findViewById(R.id.tv_main_smoke)
        mTvRainfall = findViewById(R.id.tv_main_rainfall)
        mElectricity = findViewById(R.id.tv_main_electricity)

        mBtApplyAdmin = findViewById(R.id.bt_main_apply_admin)
        mBtAdmin = findViewById(R.id.bt_main_admin)
        mBtAddDevice = findViewById(R.id.bt_main_add_device)
        mBtGetRealTimeData = findViewById(R.id.bt_main_get_device_data)
        mNowShowDevice = findViewById(R.id.actv_main_now_show_device_id)
        mToolbar = findViewById(R.id.tb_main_toolbar)
        mTvUserInfo = findViewById(R.id.tv_main_user_info)
        mIvPictureDisPlayer = findViewById(R.id.iv_main_picture)
        mAviTransmissionAnimation = findViewById(R.id.avi_main_transmission)
        mAviTransmissionAnimation.smoothToHide()

        mBtCoolTem = findViewById(R.id.bt_main_cool_down)
        mBtWaterSpray = findViewById(R.id.bt_main_water_spray)

        setSupportActionBar(mToolbar)

        mBtWaterSpray.setOnClickListener(this)
        mBtCoolTem.setOnClickListener(this)
        mBtAddDevice.setOnClickListener(this)
        mBtAdmin.setOnClickListener(this)
        mBtApplyAdmin.setOnClickListener(this)
        mBtGetRealTimeData.setOnClickListener(this)
    }

    private fun startUpView() {
        mTvUserInfo.text =
            "name ${UserManager.instance.userInfo.username} uuid ${UserManager.instance.userInfo.uuid}"
        UserManager.instance.nowDisplayDeviceId.observe(this, Observer {
            mNowShowDevice.text = "${UserManager.instance.nowDisplayDeviceId.value}"
            startDisplay()
        })
        if (UserManager.instance.identityLevel.value == UserManager.IdentityLevelType.GENERAL_USER.value) {
            mBtApplyAdmin.visibility = View.VISIBLE
            mBtAdmin.visibility = View.GONE
            mBtCoolTem.visibility = View.GONE
            mBtWaterSpray.visibility = View.GONE
        } else {
            mBtAdmin.visibility = View.VISIBLE
            mBtApplyAdmin.visibility = View.GONE
            mBtCoolTem.visibility = View.VISIBLE
            mBtWaterSpray.visibility = View.VISIBLE
        }
        UserManager.instance.identityLevel.observe(this, Observer {
            when (it) {
                UserManager.IdentityLevelType.SUPER_ADMIN.value -> mBtAdmin.visibility =
                    View.VISIBLE
                UserManager.IdentityLevelType.ADMIN.value -> mBtAdmin.visibility =
                    View.VISIBLE
                UserManager.IdentityLevelType.GENERAL_USER.value -> mBtApplyAdmin.visibility =
                    View.VISIBLE
                else -> {
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_main_admin -> startActivity(AdminActivity::class.java)
            //申请管理员
            R.id.bt_main_apply_admin -> applyAdmin()
            //查看数据，先选择设备
            R.id.bt_main_get_device_data -> startActivity(DeviceListActivity::class.java) { intent ->
                intent.putExtra(
                    DeviceListActivity.OPERATE_TYPE,
                    DeviceListActivity.OperateType.SELECT_DEVICE_SHOW_DATA.value
                )
            }
            //添加设备，先查看所有设备列表，再添加
            R.id.bt_main_add_device -> startActivity(DeviceListActivity::class.java) { intent ->
                intent.putExtra(
                    DeviceListActivity.OPERATE_TYPE,
                    DeviceListActivity.OperateType.GET_DEVICE.value
                )
            }
            R.id.bt_main_cool_down -> {
                log("降温 发送7")
                remoteDataStreamLongLink!!.send("7")
            }
            R.id.bt_main_water_spray -> {
                log("喷水 发送8")
                remoteDataStreamLongLink!!.send("8")
            }
        }
    }

    private fun applyAdmin() {
        InputMsgDialog.Companion.Builder(this).setTitle("请输入申请原因")
            .setCommitButtonText("提交") { msg ->
                RemoteService.applyToAreaManager(
                    UserManager.instance.userInfo.uuid,
                    msg, object : CallBack<String> {
                        override fun success(res: String?) {
                            log("$res")
                        }

                        override fun failed(res: String?) {
                            log("网络错误")
                        }
                    })
            }.create().show()
    }

    //开始从服务器拉取数据流
    private fun startDisplay() {
        log("------this------${UserManager.instance.nowDisplayDeviceId.value}")
        if (UserManager.instance.nowDisplayDeviceId.value != null
            && !UserManager.instance.nowDisplayDeviceId.value.isNullOrEmpty()
        ) {
            log("start get device date,device id = ${UserManager.instance.nowDisplayDeviceId.value}")
            initWebSocket(UserManager.instance.nowDisplayDeviceId.value!!)
        }
    }

    override fun initWebSocket(devid: String): MyWebSocket? {
        val s = "$WEB_SOCKET_SERVER_URL/${UserManager.instance.userInfo.uuid}/$devid"
        log("将要去连接 $s")
        remoteDataStreamLongLink = MyWebSocket(s)
        remoteDataStreamLongLink!!.setConnectSuccessListener(object :
            MyWebSocket.ConnectionStatusListener {
            override fun connectionSuccess(handshakedata: ServerHandshake?) {
                log("连接成功")
                runOnUiThread { mAviTransmissionAnimation.smoothToShow() }
                Looper.prepare()
                WebSocketTools().startHeatBeat(remoteDataStreamLongLink, this@MainActivity)
            }

            override fun connectionError(tr: Throwable?) {
//                val stackTraceString: String = Log.getStackTraceString(Throwable())
//                log("-----连接失败 $stackTraceString")
//                tr?.printStackTrace()
//                log("---连接失败的异常内容 --- ${tr?.message}")
            }

            override fun connectionClosed(
                code: Int,
                reason: String?,
                remote: Boolean
            ) {
                runOnUiThread { mAviTransmissionAnimation.smoothToHide() }
                log("关闭连接")
            }
        })
        remoteDataStreamLongLink!!.connect()
        remoteDataStreamLongLink!!.setMsgListener {
            handRemoteLongLinkMsg(it)
        }
        return remoteDataStreamLongLink
    }

    private var pictureIntArray: IntArray? = null
    private var pictureArrayIndex = 0
    private fun handRemoteLongLinkMsg(data: String) {
        val remoteMsg = GsonBuilder().create().fromJson(data, WebSocketMsgBean::class.java)
        if (remoteMsg.value == WebSocketMsgType.ORDINARY.value) {
            log("从网络接收到的普通数据 ${remoteMsg.data}")
            val ordinary = GsonBuilder().create().fromJson(remoteMsg.data.toString(), OrdinaryData::class.java)
            log("解析之后  $ordinary")
            val msg = Message.obtain()
            msg.what = DataType.ORDINARY.value
            msg.obj = ordinary
            mHandler.sendMessage(msg)

        } else if (remoteMsg.value == WebSocketMsgType.PICTURE.value) {
            if (remoteMsg.flag == WebSocketMsgFlagType.START.value) {
                pictureIntArray = IntArray(71680)
                log("开始解析图片  remote.flag = ${remoteMsg.flag}")
            }
            if (pictureIntArray != null) {
                val buffer =
                    remoteMsg.data.toString().toByteArray(Charset.forName("ISO-8859-1"))
                var i = 0
                while (2 * i + 1 < buffer.size) {
                    pictureIntArray!![pictureArrayIndex++] =
                        ByteDataUtil.mergeByteToInt(buffer[2 * i], buffer[2 * i + 1])
                    i += 1
                }
            }
            if (pictureArrayIndex == 71680) {
                log("解析图片没问题")
            }
            if (remoteMsg.flag == WebSocketMsgFlagType.END.value) {
                log("now index = $pictureArrayIndex")
                log("解析图片完成  remote.flag = ${remoteMsg.flag}")
                val bitmap =
                    Bitmap.createBitmap(pictureIntArray!!, 320, 224, Bitmap.Config.RGB_565)
                pictureIntArray = null
                pictureArrayIndex = 0
                val msg = Message.obtain()
                msg.what = DataType.PICTURE.value
                msg.obj = bitmap
                mHandler.sendMessage(msg)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runOnUiThread { mAviTransmissionAnimation.smoothToHide() }
    }

    enum class DataType(var value: Int) {
        PICTURE(1), ORDINARY(2)
    }

    private val mHandler = Handler {
        when (it.what) {
            DataType.PICTURE.value -> {
                mIvPictureDisPlayer.setImageBitmap(it.obj as Bitmap)
                true
            }
            DataType.ORDINARY.value -> {
                fillingOrdinary(it.obj as OrdinaryData)
                true
            }
            else -> false
        }
    }

    private fun fillingOrdinary(ordinaryData: OrdinaryData) {
        mTvHumidity.text = "${ordinaryData.humidity}%"
        mTvTemperature.text = "${ordinaryData.temperature}度"
        EnvironmentalDigitalConversionUtils.ConversionNumberToLightIntensity(
            mTvLightIntensity,
            ordinaryData.lightIntensity
        )
        EnvironmentalDigitalConversionUtils.ConversionNumberToRainFall(
            mTvRainfall,
            ordinaryData.rainfall
        )
        mTvFlame.text = if (ordinaryData.flame) "异常" else "正常"
        mTvSmoke.text = if (ordinaryData.smoke) "异常" else "正常"
        mElectricity.text = if (ordinaryData.electricity) "异常" else "正常"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.login_out -> {
                if (remoteDataStreamLongLink != null && !remoteDataStreamLongLink!!.isClosed && !remoteDataStreamLongLink!!.isClosing) {
                    remoteDataStreamLongLink!!.close()
                    remoteDataStreamLongLink = null
                }
                startActivity(LoginActivity::class.java)
                finish()
            }
        }
        return true
    }
}

package com.snowman.graduationprojectclient.ui.login

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.snowman.graduationprojectclient.R
import com.snowman.graduationprojectclient.bean.UserInfo
import com.snowman.graduationprojectclient.manager.UserManager
import com.snowman.graduationprojectclient.remote.remoteservice.CallBack
import com.snowman.graduationprojectclient.remote.remoteservice.RemoteService
import com.snowman.graduationprojectclient.ui.MainActivity
import com.snowman.graduationprojectclient.ui.base.BaseActivity
import com.snowman.graduationprojectclient.utils.SoftWareKeyBoardUtil
import com.snowman.graduationprojectclient.utils.SpUtil
import com.snowman.graduationprojectclient.utils.ToastUtil
import com.snowman.graduationprojectclient.utils.log

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mAcEtLoUserNumber: AppCompatEditText
    private lateinit var mAcEtLoPassword: AppCompatEditText
    private lateinit var mAcBtLoLogin: AppCompatButton
    private lateinit var mToolbar: Toolbar
    private lateinit var mRlBlankArea: RelativeLayout
    private lateinit var mBtLoginMode: Button
    private lateinit var mBtRegisterMode: Button
    private lateinit var mIvPasswordIsVisible: ImageView
    private lateinit var mLlRememberPassword: LinearLayout
    private lateinit var mCbRememberPassword: CheckBox

    private var isLoginPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        startUpView()
    }

    private fun initView() {
        mAcEtLoUserNumber = findViewById(R.id.actv_login_user_number)
        mAcEtLoPassword = findViewById(R.id.actv_login_password)
        mAcBtLoLogin = findViewById(R.id.acbt_login_login_or_register)
        mRlBlankArea = findViewById(R.id.rl_login_blank_area)
        mBtLoginMode = findViewById(R.id.bt_login_login_mode)
        mBtRegisterMode = findViewById(R.id.bt_login_register_mode)
        mIvPasswordIsVisible = findViewById(R.id.password_is_visible)
        mLlRememberPassword = findViewById(R.id.ll_login_remember_password)
        mCbRememberPassword = findViewById(R.id.cb_login_remember_password)
        mToolbar = findViewById(R.id.tb_login_toolbar)

        mAcBtLoLogin.setOnClickListener(this)
        mRlBlankArea.setOnClickListener(this)
        mBtRegisterMode.setOnClickListener(this)
        mBtLoginMode.setOnClickListener(this)
        mIvPasswordIsVisible.setOnClickListener(this)
        mLlRememberPassword.setOnClickListener(this)
    }

    private fun startUpView() {
        SpUtil.getString(R.string.userNumber)?.let {
            if (it.isNotEmpty()) {
                mAcEtLoUserNumber.setText(it)
            }
        }
        mCbRememberPassword.isChecked = SpUtil.getBoolean(R.string.remember_password_eg)
        if (mCbRememberPassword.isChecked) mAcEtLoPassword.setText(SpUtil.getString(R.string.password))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.acbt_login_login_or_register -> if (isLoginPage) login() else register()
            R.id.rl_login_blank_area -> SoftWareKeyBoardUtil.hideKeyBoard(window.decorView)
            R.id.bt_login_login_mode -> switchModeToLogin(true)
            R.id.bt_login_register_mode -> switchModeToLogin(false)
            R.id.password_is_visible -> switchPassWordVisible()
            R.id.ll_login_remember_password -> rememberPassword()
        }
    }

    private fun login() {
        if (checkFormat(
                mAcEtLoUserNumber.text.toString().trim(),
                mAcEtLoPassword.text.toString().trim()
            )
        ) {
            RemoteService.login(
                mAcEtLoUserNumber.text.toString(),
                mAcEtLoPassword.text.toString(),
                object : CallBack<UserInfo> {
                    override fun success(data: UserInfo?) {
                        log(data.toString())
                        saveData()
                        UserManager.instance.userInfo = data!!
                        UserManager.instance.identityLevel.postValue(UserManager.instance.userInfo.status)
                        startActivity(MainActivity::class.java)
                        finish()
                    }

                    override fun failed(res: String?) {
                    }
                }
            )
        } else {
            ToastUtil.showShortToast("账号或密码不可以为空哦", this)
        }
    }

    private fun register() {
        if (checkFormat(
                mAcEtLoUserNumber.text.toString().trim(),
                mAcEtLoPassword.text.toString().trim()
            )
        ) {
            RemoteService.register(
                mAcEtLoUserNumber.text.toString(),
                mAcEtLoPassword.text.toString(),
                object : CallBack<UserInfo> {
                    override fun success(data: UserInfo?) {
                        log(data.toString())
                        saveData()
                        if (data != null) {
                            UserManager.instance.userInfo = data
                            startActivity(MainActivity::class.java)
                            finish()
                        } else {
                            log("登录失败")
                        }
                    }

                    override fun failed(res: String?) {
                        log("登录失败")
                    }
                }
            )
        } else {
            ToastUtil.showShortToast("账号或密码不可以为空哦", this)
        }
    }

    private fun switchModeToLogin(loginMode: Boolean) {
        isLoginPage = loginMode
        if (loginMode) {
            mBtLoginMode.setBackgroundColor(getColor(R.color.colorAccent))
            mBtRegisterMode.setBackgroundColor(getColor(R.color.gainsboro))
            mAcBtLoLogin.text = getString(R.string.login)
        } else {
            mBtLoginMode.setBackgroundColor(getColor(R.color.gainsboro))
            mBtRegisterMode.setBackgroundColor(getColor(R.color.colorAccent))
            mAcBtLoLogin.text = getString(R.string.register)
        }
    }

    private fun switchPassWordVisible() {
        if (mAcEtLoPassword.inputType == TEXTPASSWORD) {
            mAcEtLoPassword.inputType = TEXTVISIBLEPASSWORD
            mIvPasswordIsVisible.setBackgroundResource(R.drawable.password_visible)
        } else if (mAcEtLoPassword.inputType == TEXTVISIBLEPASSWORD) {
            mAcEtLoPassword.inputType = TEXTPASSWORD
            mIvPasswordIsVisible.setBackgroundResource(R.drawable.password_invisible)
        }
    }

    private fun rememberPassword() {
        mCbRememberPassword.isChecked = !mCbRememberPassword.isChecked
    }

    private fun saveData() {
        if (mCbRememberPassword.isChecked) {
            SpUtil.putString(R.string.userNumber, mAcEtLoUserNumber.text.toString())
            SpUtil.putString(R.string.password, mAcEtLoPassword.text.toString())
            SpUtil.putBoolean(R.string.userNumber, mCbRememberPassword.isChecked)
        }
    }

    private fun checkFormat(userName: String, password: String): Boolean {
        return (userName.isNotEmpty() && password.isNotEmpty())
    }
}

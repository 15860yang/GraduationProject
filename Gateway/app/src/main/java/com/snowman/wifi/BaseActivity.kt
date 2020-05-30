package com.snowman.wifi

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var mDialog: AlertDialog? = null
    private var mDialogTag: Int = -1

    fun showDialog(
        msg: String,
        dialogTag: Int = -1,
        negativeText: String = getString(R.string.confirm),
        positiveText: String = getString(R.string.cancel),
        dialogCancel: (Int) -> Unit = { dialogCanceled(it) },
        dialogConfirm: (Int) -> Unit = { dialogConfirmed(it) }
    ) {
        mDialogTag = dialogTag
        if (mDialog == null) {
            mDialog = AlertDialog.Builder(this)
                .setMessage(msg)
                .setNegativeButton(negativeText) { _, _ ->
                    dialogCancel(dialogTag)
                }
                .setPositiveButton(positiveText) { _, _ ->
                    dialogConfirm(dialogTag)
                }
                .create()
        }
        mDialog?.show()
    }

    open fun dialogConfirmed(dialogTag: Int) {

    }

    open fun dialogCanceled(dialogTag: Int) {

    }

    fun <T : BaseActivity> startActivity(
        activityClazz: Class<T>,
        injectionParams: ((Intent) -> Unit)? = null
    ) {
        val intent = Intent(this, activityClazz)
        injectionParams?.let { it(intent) }
        startActivity(intent)
    }

    private lateinit var mActivityResultMethod: (Int, Int, Intent?) -> Unit

    fun <T : BaseActivity> startActivityForResult(
        activityClazz: Class<T>,
        requestCode: Int,
        activityResultMethod: (Int, Int, Intent?) -> Unit
    ) {
        val intent = Intent(this, activityClazz)
        startActivityForResult(intent, 1)
        mActivityResultMethod = activityResultMethod
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mActivityResultMethod(requestCode, resultCode, data)
    }

    fun dismissDialog() {
        if (mDialog != null) {
            mDialog?.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        dismissDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog = null
    }

    companion object {
        const val TEXTPASSWORD = 0x81
        const val TEXTVISIBLEPASSWORD = 0x91
    }

}
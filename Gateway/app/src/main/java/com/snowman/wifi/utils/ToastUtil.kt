package com.snowman.wifi.utils

import android.content.Context
import android.widget.Toast

class ToastUtil {
    companion object {
        fun showShortToast(msg: String, context: Context) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun showLongToast(msg: String, context: Context) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
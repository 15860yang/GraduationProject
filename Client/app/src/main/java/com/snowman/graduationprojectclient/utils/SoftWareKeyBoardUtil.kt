package com.snowman.graduationprojectclient.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class SoftWareKeyBoardUtil {
    companion object {
        fun showKeyBoard(view: View) {
            val input =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            view.requestFocus()
            input?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }

        fun hideKeyBoard(view: View) {
            val input =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            input?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}

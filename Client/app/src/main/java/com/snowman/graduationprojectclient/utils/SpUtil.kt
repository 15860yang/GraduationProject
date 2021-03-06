package com.snowman.graduationprojectclient.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SpUtil {
    companion object {
        private var sp: SharedPreferences? = null
        private var context: Context? = null
        fun initData(context: Context) {
            this.context = context
            sp = context.getSharedPreferences("snowman.GraduationProjectClient", MODE_PRIVATE)
        }

        fun putString(stringId: Int, value: String) {
            sp?.edit()?.putString(context?.getString(stringId), value)?.apply()
        }

        fun putString(key: String, value: String) {
            sp?.edit()?.putString(key, value)?.apply()
        }

        fun getString(stringId: Int): String? {
            return sp?.getString(context?.getString(stringId), "")
        }

        fun getString(key: String): String? {
            return sp?.getString(key, "")
        }

        fun getBoolean(stringId: Int): Boolean {
            return sp?.getBoolean(context?.getString(stringId), false) ?: false
        }

        fun getBoolean(key: String): Boolean {
            return sp?.getBoolean(key, false) ?: false
        }

        fun putBoolean(stringId: Int, value: Boolean) {
            sp?.edit()?.putBoolean(context?.getString(stringId), value)?.apply()
        }

        fun putBoolean(key: String, value: Boolean) {
            sp?.edit()?.putBoolean(key, value)?.apply()
        }
    }
}
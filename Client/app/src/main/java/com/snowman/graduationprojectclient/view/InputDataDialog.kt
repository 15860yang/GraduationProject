package com.snowman.graduationprojectclient.view

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.snowman.graduationprojectclient.R

class InputDataDialog private constructor(private val mContext: Context, theme: Int) :
    Dialog(mContext, theme) {

    companion object {
        class Builder(val context: Context) {
            private var title: String = ""
            private var commitButtonText: String = "提交"
            private var positiveButtonClickListener: ((sT: String, eT: String) -> Unit)? = null
            private lateinit var btCommit: Button
            private lateinit var tvTitle: TextView
            private lateinit var etStartTime: EditText
            private lateinit var etEndTime: EditText

            fun setTitle(title: String): Builder {
                this.title = title
                return this
            }

            fun setCommitButtonText(
                commitButtonText: String,
                listener: ((sT: String, eT: String) -> Unit)? = null
            ): Builder {
                this.positiveButtonClickListener = listener
                this.commitButtonText = commitButtonText
                return this
            }

            fun create(): Dialog {
                val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val dialog = InputDataDialog(context, R.style.Dialog)
                val rootView = inflater.inflate(
                    R.layout.input_date_dialog,
                    dialog.window!!.decorView as ViewGroup,
                    false
                )
                val params = dialog.window!!.attributes
                params.width = WindowManager.LayoutParams.MATCH_PARENT
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
                params.gravity = Gravity.CENTER
                dialog.window!!.attributes = params
                dialog.addContentView(
                    rootView,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                rootView.apply {
                    btCommit = findViewById(R.id.bt_dialog_commit)
                    tvTitle = findViewById(R.id.tv_dialog_title)
                    etStartTime = findViewById(R.id.tv_start_time)
                    etEndTime = findViewById(R.id.tv_end_time)
                }
                tvTitle.text = title

                btCommit.setOnClickListener {
                    positiveButtonClickListener?.let {
                        dialog.dismiss()
                        it(etStartTime.text.toString(), etEndTime.text.toString())
                    }
                }
                return dialog
            }
        }
    }
}

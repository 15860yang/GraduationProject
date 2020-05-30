package com.snowman.graduationprojectclient.view

import android.app.Dialog
import android.content.Context
import android.view.*
import android.widget.*
import com.snowman.graduationprojectclient.R

class InputMsgDialog private constructor(private val mContext: Context, theme: Int) :
    Dialog(mContext, theme) {

    companion object {
        class Builder(val context: Context) {
            private var title: String = ""
            private var commitButtonText: String = "提交"
            private var positiveButtonClickListener: ((msg: String) -> Unit)? = null
            private lateinit var btCommit: Button
            private lateinit var tvTitle: TextView
            private lateinit var etInputMsg: EditText

            fun setTitle(title: String): Builder {
                this.title = title
                return this
            }

            fun setCommitButtonText(
                commitButtonText: String,
                listener: ((msg: String) -> Unit)? = null
            ): Builder {
                this.positiveButtonClickListener = listener
                this.commitButtonText = commitButtonText
                return this
            }

            fun create(): Dialog {
                val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val dialog = InputMsgDialog(context, R.style.Dialog)
                val rootView = inflater.inflate(R.layout.dialog_msg_input, dialog.window!!.decorView as ViewGroup,false)
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
                    etInputMsg = findViewById(R.id.et_dialog_input_msg)
                }
                tvTitle.text = title

                btCommit.setOnClickListener {
                    positiveButtonClickListener?.let {
                        dialog.dismiss()
                        it(etInputMsg.text.toString())
                    }
                }
                return dialog
            }
        }
    }
}

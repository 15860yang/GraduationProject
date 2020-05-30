package com.snowman.graduationprojectclient.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.snowman.graduationprojectclient.R

class DeviceManagerDialog private constructor(private val mContext: Context, theme: Int) :
    Dialog(mContext, theme) {

    companion object {
        class Builder(val context: Context) {
            private val list: ArrayList<String> = ArrayList()
            private var title: String = ""
            private var commitButtonText: String = "提交"
            private var positiveButtonClickListener: ((item: String, max: String, min: String) -> Unit)? =
                null
            private var selectItem: String = ""
            private var selectPosition: Int = 1

            private lateinit var etMaxValue: EditText
            private lateinit var eTMinValue: EditText
            private lateinit var btCommit: Button
            private lateinit var spItems: Spinner
            private lateinit var tvTitle: TextView

            fun setTitle(title: String): Builder {
                this.title = title
                return this
            }

            fun setList(vararg item: String): Builder {
                list.clear()
                item.all {
                    list.add(it)
                    true
                }
                return this
            }

            fun setCommitButtonText(
                commitButtonText: String,
                listener: ((item: String, max: String, min: String) -> Unit)? = null
            ): Builder {
                this.positiveButtonClickListener = listener
                this.commitButtonText = commitButtonText
                return this
            }

            fun create(): Dialog {
                val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val dialog = DeviceManagerDialog(context, R.style.Dialog)
                val rootView = inflater.inflate(R.layout.dialog_updata_device_config, null)
                dialog.addContentView(
                    rootView,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                rootView.apply {
                    etMaxValue = findViewById(R.id.et_max_value)
                    eTMinValue = findViewById(R.id.et_min_value)
                    btCommit = findViewById(R.id.bt_dialog_commit)
                    spItems = findViewById(R.id.sp_dialog_items)
                    tvTitle = findViewById(R.id.tv_dialog_title)
                }
                tvTitle.text = title
                val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
                spItems.adapter = adapter
                spItems.onItemSelectedListener = object : AdapterView.OnItemClickListener,
                    AdapterView.OnItemSelectedListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        selectItem = list[position]
                        selectPosition = position + 1
                    }
                }
                btCommit.setOnClickListener {
                    positiveButtonClickListener?.let {
                        dialog.dismiss()
                        it(
                            selectPosition.toString(),
                            etMaxValue.text.toString(),
                            eTMinValue.text.toString()
                        )
                    }
                }
                return dialog
            }
        }
    }
}
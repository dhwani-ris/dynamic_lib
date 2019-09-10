package com.dhwaniris.dynamicForm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig
import com.dhwaniris.dynamicForm.R
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean
import com.dhwaniris.dynamicForm.interfaces.SelectListener
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class MultiSelectorAdapter(private val formListener: SelectListener,
                           private val list: ArrayList<AnswerOptionsBean>,
                           checkLimit: Int,
                           private val checkedList: MutableList<String>,
                           questionBean: QuestionBean) : RecyclerView.Adapter<MultiSelectorAdapter.MyViewHolder>(), Filterable {
    private var checkLimit = 0
    private val arrayList: ArrayList<AnswerOptionsBean>
    private val itemsCheckAlone = ArrayList<String>()
    private var isCheckAllCase = false
    private var checkAllPattern = ".*"


    init {
        this.checkLimit = checkLimit
        arrayList = ArrayList()
        this.arrayList.addAll(list)

        if (!questionBean.validation.isEmpty()) {
            for (validationBean in questionBean.validation) {
                if (validationBean != null && validationBean._id == LibDynamicAppConfig.VAL_DESELECT_ALL) {
                    itemsCheckAlone.add(validationBean.error_msg)
                } else if (validationBean != null && validationBean._id == LibDynamicAppConfig.VAL_COMBIND_CHECK) {
                    isCheckAllCase = true
                    if (validationBean.error_msg != null && validationBean.error_msg != "") {
                        checkAllPattern = validationBean.error_msg
                    }
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_multi_selector_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        holder.checkBox.text = list[pos]!!.name
        holder.checkBox.isChecked = checkedList.contains(list[pos]!!._id)
        holder.checkBox.isEnabled = !(checkedList.size >= checkLimit && !checkedList.contains(list[pos]!!._id))


        holder.checkBox.setOnClickListener {
            if (holder.checkBox.isChecked) {
                if (!checkedList.contains(list[pos]!!._id)) {

                    ///if check alone item available
                    if (itemsCheckAlone.size > 0) {
                        if (itemsCheckAlone.contains(list[pos]!!._id) || itemsCheckAlone.containsAll(checkedList)) {
                            checkedList.clear()
                            formListener.SingleSelector(null, "ClearAll", null, false)
                            checkedList.add(list[pos]!!._id)
                            formListener.SingleSelector(null, null, list[pos]!!._id, false)
                        } else {
                            checkedList.add(list[pos]!!._id)
                            formListener.SingleSelector(null, null, list[pos]!!._id, false)
                        }

                    } else {
                        checkedList.add(list[pos]!!._id)
                        formListener.SingleSelector(null, null, list[pos]!!._id, false)

                    }

                    if (isCheckAllCase) {
                        if (Pattern.matches(checkAllPattern, list[pos]!!._id)) {
                            for (answerOptions in list) {
                                if (Pattern.matches(checkAllPattern, answerOptions._id)) {
                                    checkedList.add(answerOptions._id)
                                    formListener.SingleSelector(null, null, answerOptions._id, false)
                                }
                            }

                        }
                    }

                }

            } else {
                checkedList.remove(list[pos]!!._id)
                formListener.SingleSelector(null, list[pos]!!._id, null, false)
                if (isCheckAllCase) {
                    if (Pattern.matches(checkAllPattern, list[pos]!!._id)) {
                        for (answerOptions in list) {
                            if (Pattern.matches(checkAllPattern, answerOptions._id)) {
                                checkedList.remove(answerOptions._id)
                                formListener.SingleSelector(null, answerOptions._id, null, false)
                            }
                        }

                    }
                }
            }
            notifyDataSetChanged()
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter? {
        return null
    }

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var checkBox: CheckBox = v.findViewById(R.id.checkbox_item)
        var mainView: LinearLayout = v.findViewById(R.id.mainView)

    }

    fun search(s: String) {
        val searchTxt = s.trim { it <= ' ' }.toLowerCase(Locale.getDefault())
        list.clear()
        if (searchTxt.isEmpty()) {
            list.addAll(arrayList)
        } else {
            for (answerOptionsBean in arrayList) {
                if (answerOptionsBean.name.toLowerCase(Locale.getDefault()).contains(searchTxt)) {
                    list.add(answerOptionsBean)
                }
            }
        }
        notifyDataSetChanged()
    }


}
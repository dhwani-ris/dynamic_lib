package com.dhwaniris.dynamicForm.adapters

import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig
import com.dhwaniris.dynamicForm.R
import com.dhwaniris.dynamicForm.db.FilledForms
import com.dhwaniris.dynamicForm.interfaces.FormAdapterClickListener
import com.dhwaniris.dynamicForm.pojo.FilledFormCard
import com.dhwaniris.dynamicForm.utils.QuestionsUtils
import java.text.SimpleDateFormat
import java.util.*


class FilledFormAdapter(diffCallback: DiffUtil.ItemCallback<FilledFormCard>, val listener: FormAdapterClickListener, val viewSequenceOrders: List<String>) : ListAdapter<FilledFormCard, FilledFormsViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): FilledFormsViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.dy_item_filled_forms, parent, false)
        return FilledFormsViewHolder(v, viewSequenceOrders)
    }

    override fun onBindViewHolder(viewHolder: FilledFormsViewHolder, position: Int) {
        val singleForm = getItem(position)
        viewHolder.bindView(singleForm, listener)
    }
}

class FilledFormsViewHolder(v: View, private val viewSequenceOrders: List<String>) : RecyclerView.ViewHolder(v) {
    var date: TextView = v.findViewById(R.id.date)
    private var del: TextView = v.findViewById(R.id.del)
    private var retry: TextView = v.findViewById(R.id.retry)
    var tvFormStatus: TextView = v.findViewById(R.id.form_status)
    var mainView: LinearLayout = v.findViewById(R.id.mainView)
    private var linear: LinearLayout = v.findViewById(R.id.linear)
    private var btnDraft: LinearLayout = v.findViewById(R.id.btnsDraft)

    fun bindView(filledFormCard: FilledFormCard, listener: FormAdapterClickListener) {
        val stringForMakeSearchable = StringBuilder()
        linear.removeAllViews()
        linear.orientation = LinearLayout.VERTICAL
        val tid = filledFormCard.transactionId
        date.text = getDateTime(filledFormCard.mobileCreatedAt)
        stringForMakeSearchable.append(getDateTime(filledFormCard.mobileCreatedAt))
        setFormStatusVisibility(tvFormStatus, filledFormCard.upload_status)
        for (text in filledFormCard.viewSecquenceList) {
            val view = LayoutInflater.from(linear.context).inflate(R.layout.dy_row_dynam, null)
            if (view is TextView) {
                view.text = text
                stringForMakeSearchable.append(text)
            }
            linear.addView(view)

        }
        when (filledFormCard.upload_status) {
            LibDynamicAppConfig.DRAFT,
            LibDynamicAppConfig.REJECTED_DUPLICATE
            -> {
                btnDraft.visibility = View.VISIBLE
                retry.visibility = View.GONE
            }

            LibDynamicAppConfig.REJECTED_DY_NOT_FOUND -> {
                btnDraft.visibility = View.VISIBLE
                retry.visibility = View.VISIBLE
            }

            else -> {
                btnDraft.visibility = View.GONE
                retry.visibility = View.GONE
            }
        }

        del.setOnClickListener {
            AlertDialog.Builder(del.context)
                    .setTitle(R.string.are_you_sure)
                    .setMessage(R.string.data_will_be_removed)
                    .setPositiveButton(R.string.delete) { _, _ ->
                     // todo delete here

                        Toast.makeText(del.context, R.string.data_deleted, Toast.LENGTH_LONG).show()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .setCancelable(false)
                    .show()

        }
        mainView.setOnClickListener { listener.onFormClick(tid) }

        retry.setOnClickListener {
            AlertDialog.Builder(del.context)
                    .setTitle(R.string.are_you_sure)
                    .setMessage(R.string.re_submmit_response)
                    .setPositiveButton(R.string.submit) { _, _ ->
                   //todo retry here

                        Toast.makeText(del.context, R.string.form_submitted_successfully, Toast.LENGTH_LONG).show()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .setCancelable(false)
                    .show()
        }

    }

    private fun setFormStatusVisibility(tv1: TextView, formStatus: Int) {
        tv1.visibility = View.VISIBLE
        when (formStatus) {
            LibDynamicAppConfig.DRAFT -> {
                setTextAnsColor(tv1, R.string.draft, R.color.draft)
            }
            LibDynamicAppConfig.SUBMITTED -> {
                setTextAnsColor(tv1, R.string.saved, R.color.saved)
            }
            LibDynamicAppConfig.SYNCED -> {
                setTextAnsColor(tv1, R.string.synced, R.color.synced)
            }
            LibDynamicAppConfig.SYNCED_BUT_EDITABLE -> {
                setTextAnsColor(tv1, R.string.flagged, R.color.flagged)
            }
            LibDynamicAppConfig.EDITABLE_DARFT -> {
                setTextAnsColor(tv1, R.string.flagged_draft, R.color.flagged_draft)
            }
            LibDynamicAppConfig.EDITABLE_SUBMITTED -> {
                setTextAnsColor(tv1, R.string.flagged_saved, R.color.flagged_draft)

            }
            LibDynamicAppConfig.REJECTED_DUPLICATE -> {
                setTextAnsColor(tv1, R.string.rejected_duplicate, R.color.red)
            }
            LibDynamicAppConfig.REJECTED_DY_NOT_FOUND -> {
                setTextAnsColor(tv1, R.string.rejected_dy_not_found, R.color.red)
            }
            else -> {
                tv1.visibility = View.GONE
            }
        }
    }


    private fun setTextAnsColor(tv: TextView, stringResId: Int, colorResId: Int) {
        tv.setText(stringResId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setBackgroundColor(tv.context.resources.getColor(colorResId, null))
        } else {
            tv.setBackgroundColor(tv.context.resources.getColor(colorResId))
        }
    }

    private fun getDateTime(value: String): String {
        val timestamp = java.lang.Long.parseLong(value)
        val date = Date(timestamp)
        val df = SimpleDateFormat("MMM dd, yyyy hh:mm a",
                Locale.ENGLISH)
        return df.format(date)
    }

    private fun viewSequenceTextFormFilledForm(filledForms: FilledForms, igorQueers: List<String>): List<String> {
        val strings = ArrayList<String>()
        for (questionSequence in igorQueers) {
            val questionBeanFilled = filledForms.question.find { it.order == questionSequence }
            if (questionBeanFilled != null) {
                val text = String.format(Locale.getDefault(),
                        "%s : %s", questionBeanFilled.title,
                        QuestionsUtils.getViewableStringFormAns(questionBeanFilled))
                strings.add(text)
            }
        }

        return strings
    }

}

package com.dhwaniris.dynamicForm.db.dbhelper.form

import com.google.gson.annotations.SerializedName

/**
 * Created by ${Sahjad} on 6/12/2019.
 */

open class CreateDynamicOption(
        @SerializedName("formId")var formId: String = "",
        @SerializedName("order")var order: String = "",
        @SerializedName("parentOrder")var parentOrder: String = "",
        @SerializedName("optionIdentifier")var optionIdentifier: List<String> = ArrayList()

) {

    companion object {
        const val FORM_ID = "formId"
        const val ORDER = "order"
        const val PARENT_ORDER = "parentOrder"
        const val OPTIONS_IDENTIFIER = "optionIdentifier"
    }

}
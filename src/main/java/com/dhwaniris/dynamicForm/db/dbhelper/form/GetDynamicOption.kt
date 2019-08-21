package com.dhwaniris.dynamicForm.db.dbhelper.form

import com.google.gson.annotations.SerializedName

/**
 * Created by ${Sahjad} on 6/12/2019.
 */
open class GetDynamicOption(
        @SerializedName("formId")var formId: String = "",
        @SerializedName("filterBy")var filterBy: List<String> = ArrayList(),
        @SerializedName("orderToDisplayIn")var orderToDisplayIn: String = "",
        @SerializedName("dataOrdersMapping")var dataOrdersMapping: List<DataOrderMapping> = ArrayList(),
        @SerializedName("isReusable")var isReusable: Boolean = false
) {

    companion object {
        const val FORMI_D = "formId"
        const val FILTER_BY = "filterBy"
        const val ORDER_TO_DISPLAY_IN = "orderToDisplayIn"
        const val DATA_ORDER_MAPPING = "dataOrdersMapping"
        const val IS_REUSEABE = "isReusable"
    }

}

open class DataOrderMapping(
        var fromOrder: String = "",
        var toOrder: String = ""
) {

    companion object {
        const val FROM_ORDER = "fromOrder"
        const val TO_ORDER = "toOrder"
    }

}
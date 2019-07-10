package com.dhwaniris.dynamicForm.db.dbhelper.form

/**
 * Created by ${Sahjad} on 6/12/2019.
 */
open class GetDynamicOption(
        var formId: String = "",
        var filterBy: List<String> = ArrayList(),
        var orderToDisplayIn: String = "",
        var dataOrdersMapping: List<DataOrderMapping> = ArrayList(),
        var isReusable: Boolean = false
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
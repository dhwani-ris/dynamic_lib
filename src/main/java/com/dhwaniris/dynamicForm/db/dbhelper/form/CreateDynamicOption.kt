package com.dhwaniris.dynamicForm.db.dbhelper.form

/**
 * Created by ${Sahjad} on 6/12/2019.
 */

open class CreateDynamicOption(
        var formId: String = "",
        var order: String = "",
        var parentOrder: String = "",
        var optionIdentifier: List<String> = ArrayList()

) {

    companion object {
        const val FORM_ID = "formId"
        const val ORDER = "order"
        const val PARENT_ORDER = "parentOrder"
        const val OPTIONS_IDENTIFIER = "optionIdentifier"
    }

}
package com.dhwaniris.dynamicForm.db.dbhelper

/**
 * Created by ${Sahjad} on 6/12/2019.
 */

open class DynamicAnswerData(

        var uniqueId: String = "",
        var formId: String = "",
        var transactionId: String = "",
        var data: List<QuestionBeanFilled> = ArrayList()

) {

    fun makeUnique() {
        uniqueId = formId + "_" + transactionId
    }

    companion object {
        const val FORM_ID = "formId"
        const val TRANSACTION_ID = "transactionId"
        const val DATA = "data"
        const val UNIQUE_ID = "uniqueId"
    }

}
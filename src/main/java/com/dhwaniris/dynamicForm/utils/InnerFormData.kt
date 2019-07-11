package com.dhwaniris.dynamicForm.utils

import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.dynamicForm.db.dbhelper.form.Nested
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean

/**
 * Created by ${Sahjad} on 7/11/2019.
 */
class InnerFormData private constructor() {

    var questionBeanRealmList: List<QuestionBean>? = null

    var questionBeanFilled: QuestionBeanFilled? = null

    companion object {

        private var instance: InnerFormData? = null

        fun getInstance(): InnerFormData {
            if (instance == null) {
                instance = InnerFormData()
            }
            return instance as InnerFormData
        }

        fun createNew(questionBeanRealmList: List<QuestionBean>, questionBeanFilled: QuestionBeanFilled) {
            instance = InnerFormData()
            instance!!.questionBeanRealmList = questionBeanRealmList
            instance!!.questionBeanFilled = questionBeanFilled

        }

        fun releaseData() {
            instance = null
        }

        fun saveAns(questionBeanFilled: QuestionBeanFilled) {
            instance!!.questionBeanFilled = questionBeanFilled
        }

        fun saveNestedAns(nestedList: List<Nested>) {
            instance!!.questionBeanFilled!!.nestedAnswer = nestedList
        }
    }
}

package com.dhwaniris.tata_trust_delta.persistance

import com.dhwaniris.tata_trust_delta.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.Nested
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.QuestionBean

import io.realm.RealmList

/**
 * Created by ${Sahjad} on 7/11/2019.
 */
class InnerFormData private constructor() {

    var questionBeanRealmList: RealmList<QuestionBean>? = null

    var questionBeanFilled: QuestionBeanFilled? = null

    companion object {

        private var instance: InnerFormData? = null

        fun getInstance(): InnerFormData {
            if (instance == null) {
                instance = InnerFormData()
            }
            return instance as InnerFormData
        }

        fun createNew(questionBeanRealmList: RealmList<QuestionBean>, questionBeanFilled: QuestionBeanFilled) {
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

        fun saveNestedAns(nestedList: RealmList<Nested>) {
            instance!!.questionBeanFilled!!.nestedAnswer = nestedList
        }
    }
}

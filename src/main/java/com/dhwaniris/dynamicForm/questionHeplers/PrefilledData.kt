package com.dhwaniris.dynamicForm.questionHeplers

import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.dynamicForm.utils.QuestionsUtils

/**
 * Created by  on 1/7/2020.
 */
class PrefilledData private constructor() {

    var questionedFilledList: List<QuestionBeanFilled> = ArrayList()

    companion object {

        private var instance: PrefilledData? = null

        fun getInstance(): PrefilledData {
            if (instance == null) {
                instance = PrefilledData()
            }
            return instance as PrefilledData
        }

        fun createNew(questionFilledList: List<QuestionBeanFilled>) {
            instance = PrefilledData()
            instance!!.questionedFilledList = questionFilledList

        }

        fun createNewDefaultData(questionFilledList: List<QuestionBeanFilled>) {
            instance = PrefilledData()
            instance!!.questionedFilledList = questionFilledList

        }

        fun releaseData() {
            instance = null
        }

    }
}

class PrefilledDefaultData private constructor() {

    var questionedFilledList: List<QuestionBeanFilled> = ArrayList()

    fun getSingleQuestionFilled(order: String): QuestionBeanFilled? =
            questionedFilledList.find { it.order == order }


    companion object {

        private var instance: PrefilledDefaultData? = null

        fun getInstance(): PrefilledDefaultData {
            if (instance == null) {
                instance = PrefilledDefaultData()
            }
            return instance as PrefilledDefaultData
        }

        fun createNew(questionFilledList: List<QuestionBeanFilled>) {
            instance = PrefilledDefaultData()
            instance!!.questionedFilledList = questionFilledList

        }

        fun createNewByPair(vararg defaultValues: Pair<String, String>) {
            val tempQuestionBeanFilledList = ArrayList<QuestionBeanFilled>()
            defaultValues.forEach {
                tempQuestionBeanFilledList.add(getDymmyQuestionFilled(it.first, it.second))
            }
            instance = PrefilledDefaultData()
            instance!!.questionedFilledList = tempQuestionBeanFilledList

        }

        private fun getDymmyQuestionFilled(order: String, value: String): QuestionBeanFilled {
            val questionBeanFilled = QuestionBeanFilled()
            questionBeanFilled.order = order
            val newAnswerList = QuestionsUtils.getNewAnswerList()
            newAnswerList.first().value = value
            newAnswerList.first().textValue = value
            questionBeanFilled.answer = newAnswerList
            return questionBeanFilled
        }

        fun releaseData() {
            instance = null
        }

    }
}
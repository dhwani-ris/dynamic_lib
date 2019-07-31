package com.dhwaniris.dynamicForm.utils

import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.dynamicForm.db.dbhelper.form.Form
import org.json.JSONObject

/**
 * Created by ${Sahjad} on 7/31/2019.
 */
class FormUtils {
    companion object {
        fun getAnswerLabel(jsonObject: JSONObject, form: Form, userLanguage: String = "en"): JSONObject {
            form.language.find { it.lng == userLanguage }?.let { languageBean ->
                for (question in languageBean.question) {
                    val columnName = question.columnName
                    if (jsonObject.has(columnName)) {
                        val answer = jsonObject.getString(columnName)
                        val answerFormText = DynamicLibUtils.getAnswerFormText(answer, question)
                        val questionBeanFilled = QuestionBeanFilled()
                        questionBeanFilled.input_type = question.input_type
                        questionBeanFilled.answer = answerFormText
                        val viewableStringFormAns = QuestionsUtils.getViewableStringFormAns(questionBeanFilled, question)
                        jsonObject.put(columnName, viewableStringFormAns)
                    }
                }
            }

            return jsonObject

        }

    }
}
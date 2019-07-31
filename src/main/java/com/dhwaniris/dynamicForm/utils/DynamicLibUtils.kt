package com.dhwaniris.dynamicForm.utils

import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean
import java.util.ArrayList

/**
 * Created by ${Sahjad} on 7/31/2019.
 */
class DynamicLibUtils {
    companion object {

        fun getAnswerFormText(originalAns: String?, questionBean: QuestionBean): List<Answers> {
            val answersList = ArrayList<Answers>()
            if (originalAns != null && originalAns.isNotEmpty()) {
                val split = originalAns.split(",")
                for (ans in split) {
                    val answers = Answers()
                    if (QuestionsUtils.isEditTextType(questionBean.input_type)) {
                        answers.textValue = ans
                    } else {
                        answers.value = ans
                    }
                    answersList.add(answers)
                }
            }
            return answersList

        }
    }
}

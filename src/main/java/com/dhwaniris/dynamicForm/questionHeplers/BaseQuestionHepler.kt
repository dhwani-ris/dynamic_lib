package com.dhwaniris.dynamicForm.questionHeplers

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled

/**
 * Created by ${Sahjad} on 5/14/2019.
 */
abstract class BaseQuestionHepler(var questionMap: LinkedHashMap<String, QuestionBean>, val answerMap: LinkedHashMap<String, QuestionBeanFilled>) {

}
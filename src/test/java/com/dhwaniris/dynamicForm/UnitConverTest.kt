package com.dhwaniris.dynamicForm

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean

/**
 * Created by ${Sahjad} on 5/27/2019.
 */
class UnitConverTest {
    /*@Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        val unitTest: UnitConversionHelper = UnitConversionHelper()
        val parseDouble = java.lang.Double.parseDouble("50.0")
        val saved = java.lang.Double.parseDouble("15.24")

      //  assertEquals(15.24, unitTest.convertLengthIntoMetre(parseDouble, unitTest.FOOT))

        val questionBean = QuestionBean()
        questionBean.max = "3"
        questionBean.min = "2.9"


        assertEquals(true, checkMinMaxRange("2.91324224343", questionBean))


    }*/

    internal fun checkMinMaxRange(standardValue: String, questionBean: QuestionBean): Boolean {
        if (!questionBean.max.isEmpty() || !questionBean.min.isEmpty()) {
            val value = java.lang.Double.parseDouble(standardValue)
            if (!questionBean.min.isEmpty() && questionBean.max.isEmpty()) {
                val min = java.lang.Double.parseDouble(questionBean.min)
                if (value < min) {
                    return false
                }
            } else if (questionBean.min.isEmpty() && !questionBean.max.isEmpty()) {
                val max = java.lang.Double.parseDouble(questionBean.max)
                if (value > max) {
                    return false
                }
            } else if (!questionBean.min.isEmpty() && !questionBean.max.isEmpty()) {
                val min = java.lang.Double.parseDouble(questionBean.min)
                val max = java.lang.Double.parseDouble(questionBean.max)
                if (value < min || value > max) {
                    return false
                }
            }

        }
        return true
    }


}
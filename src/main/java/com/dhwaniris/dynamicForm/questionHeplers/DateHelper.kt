package com.dhwaniris.dynamicForm.questionHeplers

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by  on 11/13/2019.
 */
class DateHelper {

    private fun isContainsValidValidation(validationBeanList: List<ValidationBean>): Boolean {
        val dateValidation = arrayOf(
                LibDynamicAppConfig.VAL_FUTURE,
                LibDynamicAppConfig.VAL_FUTURE_AND_PRESENT,
                LibDynamicAppConfig.VAL__PRESENT,
                LibDynamicAppConfig.VAL_PAST,
                LibDynamicAppConfig.VAL_PAST_AND_PRESENT

        )
        return validationBeanList.any {
            dateValidation.contains(it._id)
        }

    }

    /**
     * this method validate the selected date with provided validation
     *
     * @param validationBeanList
     * @param selectedDateValue
     * @return it return true if any validation is verified
     */
    fun checkValidationsOnDate(validationBeanList: List<ValidationBean>, selectedDateValue: String): Boolean {

        if (validationBeanList.isEmpty() || !isContainsValidValidation(validationBeanList)) {
            return true
        }
        val formatter: DateFormat
        val date: Date
        val validList = ArrayList<Boolean>()
        formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        try {
            date = formatter.parse(selectedDateValue)
            val selectedDate = Calendar.getInstance()
            selectedDate.time = date

            val temptoday = Calendar.getInstance()
            var today = temptoday.time
            val sToday = formatter.format(today)
            today = formatter.parse(sToday)
            val currentDate = Calendar.getInstance()
            currentDate.time = today

            for (validationBean1 in validationBeanList) {
                when (validationBean1._id) {
                    LibDynamicAppConfig.VAL_FUTURE ->
                        validList.add(validateFuture(selectedDate, currentDate, validationBean1))
                    LibDynamicAppConfig.VAL_FUTURE_AND_PRESENT ->
                        validList.add(validateFuture(selectedDate, currentDate, validationBean1) || isSameDate(currentDate, selectedDate))
                    LibDynamicAppConfig.VAL_PAST ->
                        validList.add(validatePast(selectedDate, currentDate, validationBean1))
                    LibDynamicAppConfig.VAL_PAST_AND_PRESENT ->
                        validList.add(validatePast(selectedDate, currentDate, validationBean1) || isSameDate(currentDate, selectedDate))
                    LibDynamicAppConfig.VAL__PRESENT ->
                        validList.add(isSameDate(currentDate, selectedDate))


                }
            }


        } catch (e: ParseException) {
        }


        return validList.any { it }

    }

    /**
     * this method validate is the selected date is a future date as well validate the future with
     * a provided range which will found "value" by the validation
     *
     * @param selectedDate
     * @param currentDate
     * @param validationBean
     * @return
     */
    private fun validateFuture(selectedDate: Calendar, currentDate: Calendar, validationBean: ValidationBean): Boolean {
        if (validationBean.value.isNullOrEmpty()) {
            return selectedDate.after(currentDate)
        } else {
            validationBean.value.toLongOrNull()?.let { value ->
                val distance = value * 86400000
                val calculatedTime = currentDate.timeInMillis + distance
                val instance = Calendar.getInstance()
                instance.timeInMillis = calculatedTime
                return (selectedDate.before(instance) || isSameDate(selectedDate, instance)) && selectedDate.after(currentDate)

            }
        }

        return true
    }

    /**
     * this method is  validate is the selected date is past date as well validate the past with
     * a provided range which will found "value" by the validation
     *
     * @param selectedDate
     * @param currentDate
     * @param validationBean
     * @return
     */
    private fun validatePast(selectedDate: Calendar, currentDate: Calendar, validationBean: ValidationBean): Boolean {
        if (validationBean.value.isNullOrEmpty()) {
            return selectedDate.before(currentDate)
        } else {
            validationBean.value.toLongOrNull()?.let { value ->
                val distance = value * 86400000
                val calculatedTime = currentDate.timeInMillis - distance
                val instance = Calendar.getInstance()
                instance.timeInMillis = calculatedTime
                return (selectedDate.after(instance) || isSameDate(selectedDate, instance)) && selectedDate.before(currentDate)

            }
        }

        return true
    }


    private fun isSameDate(currentDate: Calendar, selectedDate: Calendar): Boolean {
        return currentDate == selectedDate

    }


}
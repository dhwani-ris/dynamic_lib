package com.dhwaniris.dynamicForm.utils

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.dynamicForm.db.dbhelper.form.*
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.regex.Pattern.matches
import kotlin.Comparator


class QuestionsUtils {


    companion object {
        fun isOneOf(inputType: String, vararg types: String): Boolean {
            return (types.contains(inputType))
        }

        fun getNewAnswerList(): ArrayList<Answers> {
            val answerBeanHelperList = ArrayList<Answers>()
            val answers = Answers()
            answers.label = ""
            answers.reference = ""
            answers.value = ""
            answers.textValue = ""
            answerBeanHelperList.add(answers)
            return answerBeanHelperList

        }

        fun isQuestionHasValidation(questionBean: QuestionBean, validationId: String): Boolean {
            return (questionBean.validation.any { it._id == validationId })
        }

        fun isQuestionRequired(questionBean: QuestionBean): Boolean {
            if (questionBean.validation.find { it._id == LibDynamicAppConfig.VAL_REQUIRED } != null) {
                return true
            }
            return false
        }

        fun isAnswerIsExpected(uniqueKey: String, value: String, questionBeanFilledList: LinkedHashMap<String, QuestionBeanFilled>): Boolean {

            val questionBeanFilled = questionBeanFilledList[uniqueKey]
            if (questionBeanFilled != null) {
                val answers = questionBeanFilled.answer
                for (answers1 in answers) {
                    val answers1Value = answers1.value
                    if (answers1Value != "") {
                        val isMatchPatten = matches(value, answers1Value)
                        val isMatchValue = value == answers1Value
                        if (isMatchPatten || isMatchValue) {
                            return true
                        }
                    }
                }

            } else {
                return true
            }
            return false
        }


        fun checkValueForVisibility(questionBean: QuestionBean?, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>): Boolean {
            var isMatch: Boolean
            if (questionBean != null && questionBean.parent.isNotEmpty()) {
                val parentBean1 = questionBean.parent[0]
                isMatch = isAnswerIsExpected(getParentUniqueId(parentBean1!!), parentBean1.value, answerBeanHelperList)

                if (questionBean.parent.size > 1) {
                    isMatch = validateVisibilityWithMultiParent(questionBean, isMatch, answerBeanHelperList)
                }
            } else {
                return true
            }
            return isMatch
        }

        fun validateVisibilityWithMultiParent(questionBean: QuestionBean, isMatchOld: Boolean, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>): Boolean {
            var isMatch = isMatchOld
            var isOrCase = false
            var isVisibleOnParentsDiffrentValue = false
            for (validationBean in questionBean.validation) {
                if (validationBean._id == LibDynamicAppConfig.VAL_OR_CASE_WITH_MULTIPLE_PARENT) {
                    isOrCase = true
                    break
                } else if (validationBean._id == LibDynamicAppConfig.VAL_VISIBLE_ON_PARENTS_HAS_DIFFERENT_VALUES) {
                    isVisibleOnParentsDiffrentValue = true
                    break
                }
            }
            var valueInParent: String? = null
            for (parentBean in questionBean.parent) {
                if (!isVisibleOnParentsDiffrentValue) {
                    if (!isOrCase) {
                        isMatch = isMatch && isAnswerIsExpected(getParentUniqueId(parentBean), parentBean.value, answerBeanHelperList)
                    } else {
                        isMatch = isMatch || isAnswerIsExpected(getParentUniqueId(parentBean), parentBean.value, answerBeanHelperList)
                    }
                } else {
                    if (valueInParent == null) {
                        valueInParent = getSingleAnswerValueFormOder(getParentUniqueId(parentBean), answerBeanHelperList)
                    } else {
                        isMatch = valueInParent != getSingleAnswerValueFormOder(getParentUniqueId(parentBean), answerBeanHelperList)
                        valueInParent = getSingleAnswerValueFormOder(getParentUniqueId(parentBean), answerBeanHelperList)
                    }

                }

            }
            return isMatch
        }

        private fun getSingleAnswerValueFormOder(uniqueKey: String, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>): String? {

            val questionBeanFilled = answerBeanHelperList[uniqueKey]

            if (questionBeanFilled != null) {
                val answers = questionBeanFilled.answer
                if (answers.isNotEmpty() && answers[0] != null) {
                    return answers[0]!!.value
                }
            }
            return null
        }

        fun getAnswerListFormRestriction(allOptions: List<AnswerOptionsBean>, restrictionsBean: RestrictionsBean?, questionBean: QuestionBean, questionBeanFilledList: LinkedHashMap<String, QuestionBeanFilled>): List<AnswerOptionsBean> {

            var avlList = ArrayList<AnswerOptionsBean>()
            if (restrictionsBean == null) {
                return allOptions
            }
            when (restrictionsBean.type) {
                LibDynamicAppConfig.REST_GET_ANS_OPTION, LibDynamicAppConfig.REST_GET_ANS_OPTION_LOOPING -> {
                    //     avlList.addAll(allOptions);
                    //requiredValue null if not want to match with any value
                    if (restrictionsBean.type == LibDynamicAppConfig.REST_GET_ANS_OPTION) {
                        val answers = ArrayList<Answers>()
                        for (ordersBean in restrictionsBean.orders) {
                            answers.addAll(getAnswerListbyOder(getRestrictionOrderUniqueId(ordersBean), ordersBean.value, questionBeanFilledList))
                        }
                        var tempID = 1
                        for (answers1 in answers) {
                            if (getValueFormTextInputType(answers1).isNotBlank()) {
                                val answerOptionsBean = AnswerOptionsBean()
                                answerOptionsBean.name = appendAnswer(answers1)
                                answerOptionsBean._id = tempID.toString()
                                answerOptionsBean.did = ArrayList()
                                avlList.add(answerOptionsBean)
                            }
                            tempID++
                        }
                    } else {
                        //// get Answer_option_ data from looping (Nested Answer)
                        if (questionBean.parent.isNotEmpty()) {
                            val loopingParent = questionBean.parent.find { arrayOf(LibDynamicAppConfig.QUS_LOOPING, LibDynamicAppConfig.QUS_LOOPING_MILTISELECT).contains(it.type) }
                            if (loopingParent != null) {
                                for (ordersBean in restrictionsBean.orders) {
                                    val nestedAns = getNestedAnswerListFromNestedTypeNew(getParentUniqueId(loopingParent),
                                            getRestrictionOrderUniqueId(ordersBean), ordersBean.value, questionBeanFilledList)
                                    avlList.addAll(getRequiredAnsFromNestedData(nestedAns, ordersBean))
                                }

                            }
                        }
                    }


                    //handling or case for filter (default AND case)
                    var isOrCase = false
                    for (validationBean in questionBean.validation) {
                        if (validationBean._id == LibDynamicAppConfig.VAL_OR_CASE_WITH_GET_FILTER_OPTION) {
                            isOrCase = true
                            break
                        }
                    }
                    var tempList = ArrayList<AnswerOptionsBean>()
                    ///apply filters on collected option
                    var isfilter = false
                    for (filterRestriction in questionBean.restrictions) {
                        if (filterRestriction.type == LibDynamicAppConfig.REST_GET_ANS_OPTION_FILTER ||
                                filterRestriction.type == LibDynamicAppConfig.REST_GET_ANS_OPTION_LOOPING_FILTER) {
                            isfilter = true
                            if (tempList.isEmpty()) {
                                tempList = filterOnAnsOptFormRestriction(avlList, filterRestriction, questionBean, questionBeanFilledList)
                                if (tempList.isEmpty() && !isOrCase) {
                                    break
                                }
                            } else {
                                tempList = if (!isOrCase) {
                                    filterOnAnsOptFormRestriction(tempList, filterRestriction, questionBean, questionBeanFilledList)
                                } else {
                                    getOrCaseListFormAnswerBean(tempList, filterOnAnsOptFormRestriction(avlList, filterRestriction, questionBean, questionBeanFilledList))
                                }
                            }


                        }
                    }

                    if (isfilter) {
                        avlList.clear()
                        avlList.addAll(tempList)
                    }


                    for (validationBean in questionBean.validation) {
                        if (validationBean._id == LibDynamicAppConfig.VAL_ADD_DEFAULT_OPTION_DY_AO) {
                            avlList.addAll(questionBean.answer_options)
                            break
                        } else if (avlList.size == 0 && validationBean._id == LibDynamicAppConfig.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0) {
                            avlList.addAll(questionBean.answer_options)
                            break
                        }
                    }

                }


                LibDynamicAppConfig.REST_DID_RELATION -> {
                    avlList.clear()
                    avlList.addAll(allOptions)
                    for (orderBean in restrictionsBean.orders) {
                        val didAnswerFormRestriction = getDIDAnswerFormRestriction(avlList, orderBean, questionBeanFilledList, restrictionsBean.orders.size > 1)
                        avlList.clear()
                        avlList.addAll(didAnswerFormRestriction)
                    }

                }
            }

            return avlList
        }

        private fun getRequiredAnsFromNestedData(nestedAns: ArrayList<Nested>, ordersBean: OrdersBean): ArrayList<AnswerOptionsBean> {
            val optionsBean: ArrayList<AnswerOptionsBean> = ArrayList()
            for (nested in nestedAns) {
                for (questionBeanFilled in nested.answerNestedData) {
                    if (getAnswerUniqueId(questionBeanFilled) == getRestrictionOrderUniqueId(ordersBean)) {
                        if (!questionBeanFilled.answer.isEmpty()) {
                            val answerObject = questionBeanFilled.answer[0]
                            if (getValueFormTextInputType(answerObject!!).isNotBlank()) {
                                val answerOptionsBean = AnswerOptionsBean()
                                answerOptionsBean.name = appendAnswer(answerObject)
                                answerOptionsBean._id = nested.forParentValue
                                answerOptionsBean.did = ArrayList()
                                optionsBean.add(answerOptionsBean)
                            }
                        }
                        break
                    }
                }
            }
            return optionsBean
        }

        private fun appendAnswer(answers1: Answers): String {
            return answers1.value + "" + answers1.textValue + " " + answers1.label
        }

        private fun filterOnAnsOptFormRestriction(allOptions: ArrayList<AnswerOptionsBean>, restrictionsBean: RestrictionsBean?, questionBean: QuestionBean, questionBeanFilledList: LinkedHashMap<String, QuestionBeanFilled>): ArrayList<AnswerOptionsBean> {

            val avlList = ArrayList<AnswerOptionsBean>()
            val tempList = ArrayList<AnswerOptionsBean>()

            if (restrictionsBean == null) {
                return allOptions
            }

            when (restrictionsBean.type) {
                LibDynamicAppConfig.REST_GET_ANS_OPTION_FILTER -> {
                    val answers = ArrayList<Answers>()
                    if (restrictionsBean.orders.size == 0) {
                        return allOptions
                    }
                    val resOrder = restrictionsBean.orders
                    for (ordersBean in resOrder)
                        answers.addAll(getAnswerListbyOder(getRestrictionOrderUniqueId(ordersBean), null, questionBeanFilledList))
                    //       int tempID = 1;

                    if (answers.size == restrictionsBean.orders.size) {
                        for (i in answers.indices) {
                            val isMatchPatten = matches(resOrder[i]!!.value, answers[i]!!.value)
                            val isMatchValue = answers[i]!!.value == resOrder[i]!!.value
                            if (isMatchPatten || isMatchValue) {
                                val answerOptionsBean = AnswerOptionsBean()
                                answerOptionsBean.name = appendAnswer(answers[i]!!)
                                answerOptionsBean._id = (i + 1).toString()
                                answerOptionsBean.did = ArrayList()
                                tempList.add(answerOptionsBean)
                            }

                        }
                    }


                }

                LibDynamicAppConfig.REST_GET_ANS_OPTION_LOOPING_FILTER -> {
                    ArrayList<Answers>()
                    if ((restrictionsBean.orders.size == 0) or (questionBean.parent.size == 0)) {
                        return allOptions
                    }
                    val resOrder = restrictionsBean.orders

                    for (ordersBean in resOrder) {
                        if (questionBean.parent.isNotEmpty()) {
                            val loopingParent = questionBean.parent.find { arrayOf(LibDynamicAppConfig.QUS_LOOPING, LibDynamicAppConfig.QUS_LOOPING_MILTISELECT).contains(it.type) }
                            if (loopingParent != null) {
                                val nestedAns = getNestedAnswerListFromNestedTypeNew(getParentUniqueId(loopingParent),
                                        getRestrictionOrderUniqueId(ordersBean), ordersBean.value, questionBeanFilledList)
                                val allAvailableOption = getRequiredAnsFromNestedData(nestedAns, ordersBean)
                                tempList.addAll(allAvailableOption)
                            }
                        }
                    }

                }
            }


            for (answerOptionsBean in allOptions) {
                for (answerOptionsBeanTemp in tempList) {
                    if (answerOptionsBean._id == answerOptionsBeanTemp._id) {
                        avlList.add(answerOptionsBean)
                        break
                    }

                }
            }

            return avlList
        }


        fun getNestedAnswerListFromNestedType(parentQuestionUniqueKey: String?, nestedUniqueKey: String?, requiredValueOrPattern: String?, answerList: LinkedHashMap<String, QuestionBeanFilled>): ArrayList<Answers> {
            //
            val finalAns = ArrayList<Answers>()
            if (parentQuestionUniqueKey != null && parentQuestionUniqueKey != "" && nestedUniqueKey != null && nestedUniqueKey != "") {
                val questionBeanFilled = answerList[parentQuestionUniqueKey]
                if (questionBeanFilled != null) {
                    val nestedArrayList = questionBeanFilled.nestedAnswer
                    val tempAns = ArrayList<Answers>()
                    if (nestedArrayList != null && nestedArrayList.isNotEmpty()) {
                        for (nested in nestedArrayList) {
                            val answerNestedData = nested.answerNestedData
                            for (questionBeanFilled1 in answerNestedData) {
                                if (questionBeanFilled1.answer.isNotEmpty()) {
                                    if (getAnswerUniqueId(questionBeanFilled1) == nestedUniqueKey) {
                                        tempAns.add(questionBeanFilled1.answer[0])
                                    }
                                }
                            }

                        }
                        if (requiredValueOrPattern == null) {
                            finalAns.addAll(tempAns)
                        } else if (requiredValueOrPattern == "") {
                            finalAns.addAll(tempAns)
                        } else {
                            for (answers in tempAns) {
                                val isMatchPatten = matches(requiredValueOrPattern, answers.value)
                                val isMatchValue = answers.value == requiredValueOrPattern
                                if (isMatchPatten || isMatchValue) {
                                    finalAns.addAll(tempAns)
                                    break
                                }
                            }
                        }


                    } else {
                        return finalAns
                    }

                }

            }

            return finalAns
        }

        fun getNestedAnswerListFromNestedTypeNew(parentQuestionUniqueKey: String?, nestedUniqueKey: String?, requiredValueOrPattern: String?, answerList: LinkedHashMap<String, QuestionBeanFilled>): ArrayList<Nested> {
            //
            val finalAns = ArrayList<Nested>()
            if (parentQuestionUniqueKey != null && parentQuestionUniqueKey != "" && nestedUniqueKey != null && nestedUniqueKey != "") {
                val questionBeanFilled = answerList[parentQuestionUniqueKey]
                if (questionBeanFilled != null) {
                    val nestedArrayList = questionBeanFilled.nestedAnswer
                    if (nestedArrayList != null && nestedArrayList.isNotEmpty()) {
                        for (nested in nestedArrayList) {
                            val answerNestedData = nested.answerNestedData
                            for (questionBeanFilled1 in answerNestedData) {
                                if (getAnswerUniqueId(questionBeanFilled1) == nestedUniqueKey) {
                                    if (questionBeanFilled1.answer.isNotEmpty()) {
                                        if (requiredValueOrPattern == null) {
                                            finalAns.add(nested)
                                        } else if (requiredValueOrPattern == "") {
                                            finalAns.add(nested)
                                        } else {
                                            for (answers in questionBeanFilled1.answer) {
                                                val isMatchPatten = matches(requiredValueOrPattern, answers.value)
                                                val isMatchValue = answers.value == requiredValueOrPattern
                                                if (isMatchPatten || isMatchValue) {
                                                    finalAns.add(nested)
                                                    break
                                                }
                                            }
                                        }

                                        break
                                    }
                                }
                            }

                        }


                    } else {
                        return finalAns
                    }

                }

            }

            return finalAns
        }


        private fun getOrCaseListFormAnswerBean(answerOptionsBeans1: ArrayList<AnswerOptionsBean>,
                                                answerOptionsBeans2: ArrayList<AnswerOptionsBean>): ArrayList<AnswerOptionsBean> {
            val oRCaseList = ArrayList<AnswerOptionsBean>()
            val oRCaseListMap = HashMap<String, AnswerOptionsBean>()
            for (answerOptionsBean in answerOptionsBeans1) {
                oRCaseListMap[answerOptionsBean._id] = answerOptionsBean
            }
            for (answerOptionsBean in answerOptionsBeans2) {
                oRCaseListMap[answerOptionsBean._id] = answerOptionsBean
            }
            oRCaseList.addAll(oRCaseListMap.values)
            return oRCaseList
        }

        private fun getDIDAnswerFormRestriction(allOptions: List<AnswerOptionsBean>, ordersBean: OrdersBean?, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>, isMultiParent: Boolean): List<AnswerOptionsBean> {

            var avlList = ArrayList<AnswerOptionsBean>()

            if (ordersBean == null) {
                return allOptions
            }
            val didParentsAnswer: List<Answers>
            val restrictionOrderUniqueId = getRestrictionOrderUniqueId(ordersBean)
            val questionBeanFilled = answerBeanHelperList[restrictionOrderUniqueId]
            if (questionBeanFilled != null) {
                didParentsAnswer = questionBeanFilled.answer
                avlList = getDidAnswerFormParentAns(allOptions, didParentsAnswer, restrictionOrderUniqueId, isMultiParent)
            } else {
                avlList.addAll(allOptions)
            }
            return avlList
        }

        fun isValidFloat(value: String?): Boolean {
            if (value == null)
                return false
            return if (value == "") false else value[0] != '.'
        }

        private fun getDidAnswerFormParentAns(allOptions: List<AnswerOptionsBean>, parentsAnswer: List<Answers>, restrictionOrderUniqueId: String, multiParent: Boolean): ArrayList<AnswerOptionsBean> {

            val avlList = ArrayList<AnswerOptionsBean>()

            /* if (parentsAnswer.isEmpty()) {
                 avlList.addAll(allOptions)
                 return avlList
             }*/
            for (answerOptionsBean in allOptions) {
                if (answerOptionsBean.did.isNotEmpty()) {
                    var isInList = false
                    for (parentAns in parentsAnswer) {
                        val did = if (!multiParent) {
                            answerOptionsBean.did[0]
                        } else {
                            answerOptionsBean.did.find { it.parentOrder == restrictionOrderUniqueId }
                        }
                        did?.let {
                            val didValueOrPattern = it.parent_option
                            if (parentAns.value != "" && didValueOrPattern != null) {
                                val isMatchPatten = matches(didValueOrPattern, parentAns.value)
                                val isMatchValue = parentAns.value == didValueOrPattern
                                isInList = isMatchPatten || isMatchValue
                            }
                        }

                        if (isInList) {
                            avlList.add(answerOptionsBean)
                            break
                        }
                    }

                } else
                    avlList.add(answerOptionsBean)
            }
            return avlList
        }

        fun getAnswerListbyOder(uniqueKey: String?, requiredValueOrPattern: String?, answerBeanList: LinkedHashMap<String, QuestionBeanFilled>): ArrayList<Answers> {

            //

            val finalAns = ArrayList<Answers>()
            if (uniqueKey != null && uniqueKey != "") {
                val questionBeanFilled = answerBeanList[uniqueKey]
                if (questionBeanFilled != null) {
                    val p_ans = questionBeanFilled.answer

                    if (requiredValueOrPattern == null) {
                        finalAns.addAll(p_ans)
                    } else if (requiredValueOrPattern == "") {
                        finalAns.addAll(p_ans)
                    } else {
                        for (answers in p_ans) {
                            val isMatchPatten = matches(requiredValueOrPattern, answers.value)
                            val isMatchValue = answers.value == requiredValueOrPattern
                            if (isMatchPatten || isMatchValue) {
                                finalAns.addAll(p_ans)
                                break
                            }
                        }


                    }
                }


            }

            return finalAns
        }

        fun validateAnswerListWithAnswerOptions(answersList: List<Answers>, availableOpiton: List<AnswerOptionsBean>): Boolean {
            for (answers in answersList) {
                var found = false
                if (answers.value != "") {
                    for (answerOptionsBean in availableOpiton) {
                        if (answers.value.trim { it <= ' ' } == answerOptionsBean._id.trim { it <= ' ' }) {
                            found = true
                            break
                        }
                    }
                }
                if (!found) {
                    return false
                }
            }
            return true
        }

        fun isSelectType(inputType: String): Boolean {
            return (inputType == LibDynamicAppConfig.QUS_DROPDOWN
                    || inputType == LibDynamicAppConfig.QUS_DROPDOWN_HIDE
                    || inputType == LibDynamicAppConfig.QUS_MULTI_SELECT
                    || inputType == LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE
                    || inputType == LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED
                    || inputType == LibDynamicAppConfig.QUS_LOOPING
                    || inputType == LibDynamicAppConfig.QUS_LOOPING_MILTISELECT)
        }

        fun isEditTextType(inputType: String): Boolean {
            return (inputType == LibDynamicAppConfig.QUS_TEXT
                    || inputType == LibDynamicAppConfig.QUS_ADDRESS)
        }


        fun validateMultiAnsRestriction(restrictionsBean: RestrictionsBean, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>, questionBeenList: LinkedHashMap<String, QuestionBean>): Boolean {

            if (restrictionsBean.orders.isNotEmpty()) {
                val ordersBean = restrictionsBean.orders[0]
                if (ordersBean != null) {
                    val uniqueKey = getRestrictionOrderUniqueId(ordersBean)
                    val questionBeanFilled = answerBeanHelperList[uniqueKey]
                    val parentQuestion = questionBeenList[uniqueKey]
                    if (questionBeanFilled != null && parentQuestion != null) {
                        var allOptions = parentQuestion.answer_options
                        for (parentRestrictionBean in parentQuestion.restrictions) {
                            val restrictionType = parentRestrictionBean.type
                            if (restrictionType == LibDynamicAppConfig.REST_DID_RELATION ||
                                    restrictionType == LibDynamicAppConfig.REST_GET_ANS_OPTION ||
                                    restrictionType == LibDynamicAppConfig.REST_GET_ANS_OPTION_LOOPING ||
                                    restrictionType == LibDynamicAppConfig.REST_GET_ANS_SUM_LOOPING) {

                                allOptions = getAnswerListFormRestriction(allOptions, parentRestrictionBean, parentQuestion, answerBeanHelperList)
                                break
                            }

                        }
                        val allAvailableOptionsForParent = ArrayList<String>()
                        for (answerOptionsBean in allOptions) {
                            allAvailableOptionsForParent.add(answerOptionsBean._id)
                        }

                        val answersList = questionBeanFilled.answer
                        val answerStringList = ArrayList<String>()
                        for (answers in answersList) {
                            answerStringList.add(answers.value)
                        }

                        for (ordersBean1 in restrictionsBean.orders) {
                            if (!answerStringList.contains(ordersBean1.value) && allAvailableOptionsForParent.contains(ordersBean1.value)) {
                                return true
                            }
                        }

                    }
                }
            }

            return false
        }

        private fun getParentAnswer(questionBean: QuestionBean, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>,
                                    questionBeenList: LinkedHashMap<String, QuestionBean>): ArrayList<Answers> {
            var parentQuestion: QuestionBean?
            val parentAnswer = ArrayList<Answers>()
            for (parentBean in questionBean.parent) {
                parentQuestion = questionBeenList[getParentUniqueId(parentBean)]
                val parentAnswerBean = answerBeanHelperList[getParentUniqueId(parentBean)]
                if (parentQuestion != null && parentAnswerBean != null) {
                    parentAnswer.addAll(parentAnswerBean.answer)
                    break
                }
            }
            return parentAnswer
        }


        fun getAnsOptionFromQuestionAfterFilter(questionBean: QuestionBean,
                                                questionBeenList: LinkedHashMap<String, QuestionBean>,
                                                answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>,
                                                userLanguage: String,
                                                formId: Int): ArrayList<AnswerOptionsBean> {

            var filteredList = ArrayList<AnswerOptionsBean>()
            var allOptions = questionBean.answer_options


            var masterVillage = false
            var masterDistrict = false
            var masterBlock = false
            var isDynamicOption = false
            var isDefaultOptionWhen0 = false
            var isDefaultOptionWhen = false


            for (validationBean in questionBean.validation) {
                when (validationBean._id) {
                    LibDynamicAppConfig.VAL_MASTER_DISTRICT -> masterDistrict = true
                    LibDynamicAppConfig.VAL_MASTER_BLOCK -> masterBlock = true
                    LibDynamicAppConfig.VAL_MASTER_VILLAGE -> masterVillage = true
                    LibDynamicAppConfig.VAL_DYNAMIC_ANSWER_OPTION -> isDynamicOption = true
                    LibDynamicAppConfig.VAL_ADD_DEFAULT_OPTION_DY_AO -> isDefaultOptionWhen = true
                    LibDynamicAppConfig.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0 -> isDefaultOptionWhen0 = true
                }

            }
            val isDynamic = masterDistrict || masterBlock || masterVillage || isDynamicOption
            /*    if (isDynamic) {

                    val realm = Realm.getDefaultInstance()
                    val singleform = realm.where(Form::class.java).equalTo("formId", formId).findFirst()
                    val projectId = singleform!!.project._id
                    if (masterDistrict) {
                        val districtBeanRealmResults = realm.where(MasterDistrictBean::class.java).equalTo("projectId", projectId).findAll().sort("name")
                        filteredList.clear()
                        filteredList = prepareDistrictList(districtBeanRealmResults, userLanguage)


                    } else if (masterBlock) {
                        val parentAnswerDistrict = getParentAnswer(questionBean, answerBeanHelperList, questionBeenList)
                        val districtId = parentAnswerDistrict[0]!!.value
                        val blockListByDistrictId = realm.where(MasterBlockBean::class.java).equalTo("district", districtId).equalTo("projectId", projectId).findAll().sort("name")
                        filteredList.clear()
                        filteredList = prepareBlockList(blockListByDistrictId, userLanguage)

                    } else if (masterVillage) {
                        val parentAnswerBlock = getParentAnswer(questionBean, answerBeanHelperList, questionBeenList)
                        val blockId = parentAnswerBlock[0]!!.value
                        val villageListByBlockId = realm.where(MasterVillageBean::class.java).equalTo("block", blockId).equalTo("projectId", projectId).findAll().sort("name")
                        filteredList.clear()
                        filteredList = prepareVillageList(villageListByBlockId, userLanguage)
                    } else if (isDynamicOption) {
                        val uniqueKey = formId.toString() + "_" + questionBean.order
                        val dyamicAnswerOption = realm.where(DynamicAnswerOption::class.java)
                                .equalTo("uniqueId", uniqueKey).findFirst()
                        if (dyamicAnswerOption != null) {
                            allOptions.clear()
                            allOptions.addAll(realm.copyFromRealm(dyamicAnswerOption.answer_option))

                        }

                    }

                    realm.close()

                }*/

            ///restriction with did logic with any question;
            if (isContainsDynamicRestriction(questionBean.restrictions)) {
                val restriction = questionBean.restrictions.find { arrayOf(LibDynamicAppConfig.REST_GET_ANS_OPTION, LibDynamicAppConfig.REST_GET_ANS_OPTION_LOOPING, LibDynamicAppConfig.REST_DID_RELATION).contains(it.type) }
                allOptions = getAnswerListFormRestriction(allOptions, restriction, questionBean, answerBeanHelperList)
            }
            /////////////////////////////////to show option which are  in parent

            if (arrayOf(LibDynamicAppConfig.QUS_DROPDOWN,
                            LibDynamicAppConfig.QUS_MULTI_SELECT,
                            LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED,
                            LibDynamicAppConfig.QUS_LOOPING,
                            LibDynamicAppConfig.QUS_LOOPING_MILTISELECT).contains(questionBean.input_type)) {
                filteredList.addAll(allOptions)

            } else
                if (questionBean.input_type == LibDynamicAppConfig.QUS_DROPDOWN_HIDE
                        || questionBean.input_type == LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE) {
                    ///////////////////////////////////to hide option in parent
                    ////nested parent
                    val grandParentAns = ArrayList<Answers>()
                    var parentQuestion: QuestionBean?
                    for (parentBean in questionBean.parent) {
                        parentQuestion = questionBeenList[getParentUniqueId(parentBean)]
                        if (parentQuestion != null) {
                            if (arrayOf(LibDynamicAppConfig.QUS_DROPDOWN,
                                            LibDynamicAppConfig.QUS_MULTI_SELECT,
                                            LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED,
                                            LibDynamicAppConfig.QUS_DROPDOWN_HIDE,
                                            LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE).contains(parentQuestion.input_type)) {
                                grandParentAns.addAll(getGrandParentAns(parentQuestion, questionBeenList, answerBeanHelperList))
                            }
                        }
                    }

                    filteredList = getSkipAnswerFormParentAns(allOptions, grandParentAns)

                }


            /* if (isDefaultOptionWhen) {
                 filteredList.addAll(questionBean.answer_options)
             } else if (isDefaultOptionWhen0 && filteredList.isEmpty()) {
                 filteredList.addAll(questionBean.answer_options)
             }*/
            return filteredList
        }

        private fun isContainsDynamicRestriction(restrictions: List<RestrictionsBean>): Boolean {
            for (restrictionsBean in restrictions) {
                val restrictionType = restrictionsBean.type
                if (restrictionType == LibDynamicAppConfig.REST_DID_RELATION ||
                        restrictionType == LibDynamicAppConfig.REST_GET_ANS_OPTION ||
                        restrictionType == LibDynamicAppConfig.REST_GET_ANS_OPTION_LOOPING ||
                        restrictionType == LibDynamicAppConfig.REST_GET_ANS_SUM_LOOPING) {
                    return true
                }

            }

            return false
        }


        private fun getGrandParentAns(questionBeanOld: QuestionBean?, questionBeenList: LinkedHashMap<String, QuestionBean>, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>): ArrayList<Answers> {
            var questionBean = questionBeanOld
            val finalAns = ArrayList<Answers>()
            val questionBeanFilled = answerBeanHelperList[getQuestionUniqueId(questionBean!!)]

            while (questionBean != null && questionBeanFilled != null) {
                val parentAns = questionBeanFilled.answer
                finalAns.addAll(parentAns)
                var nextQuestion: QuestionBean? = null
                if (questionBean.input_type == LibDynamicAppConfig.QUS_DROPDOWN_HIDE || questionBean.input_type == LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE) {

                    for (parentBean in questionBean.parent) {
                        val tempQuestionBean = questionBeenList[getParentUniqueId(parentBean)]
                        if (tempQuestionBean != null) {
                            val questionType = tempQuestionBean.input_type
                            if (questionType == LibDynamicAppConfig.QUS_DROPDOWN ||
                                    questionType == LibDynamicAppConfig.QUS_MULTI_SELECT ||
                                    questionType == LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED ||
                                    questionType == LibDynamicAppConfig.QUS_DROPDOWN_HIDE ||
                                    questionType == LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE) {
                                nextQuestion = tempQuestionBean
                                break
                            }

                        }
                    }
                }
                questionBean = nextQuestion
            }

            return finalAns
        }

        private fun getSkipAnswerFormParentAns(allOptions: List<AnswerOptionsBean>, parentsAnswer: ArrayList<Answers>): ArrayList<AnswerOptionsBean> {

            val avlList = ArrayList<AnswerOptionsBean>()

            if (parentsAnswer.size == 0) {
                avlList.addAll(allOptions)
                return avlList
            }

            for (aob in allOptions) {
                if (parentsAnswer.isNotEmpty()) {
                    var isInList = false
                    for (parentAns in parentsAnswer) {
                        if (parentAns.value == aob._id) {
                            isInList = true
                            break
                        }
                    }
                    if (!isInList) {
                        avlList.add(aob)
                    }

                } else
                    avlList.add(aob)
            }
            return avlList
        }

        fun getAnswerUniqueId(questionBeanFilled: QuestionBeanFilled): String {
            return questionBeanFilled.order
        }

        fun getQuestionUniqueId(questionBean: QuestionBean): String {
            return questionBean.order
        }

        fun getParentUniqueId(parentBean: ParentBean): String {
            return parentBean.order
        }

        fun getRestrictionOrderUniqueId(ordersBean: OrdersBean): String {
            return ordersBean.order
        }

        fun getChildUniqueId(childBean: ChildBean): String {
            return childBean.order
        }


        fun sortAnsList(questionBeanFilledArrayList: List<QuestionBeanFilled>) {

            Collections.sort(questionBeanFilledArrayList, Comparator { o1, o2 ->
                try {
                    val currentOrder = Integer.parseInt(o1.viewSequence)
                    val secondOrder = Integer.parseInt(o2.viewSequence)
                    return@Comparator currentOrder - secondOrder

                } catch (e: Exception) {
                    val currentOrder = Integer.parseInt(o1.order)
                    val secondOrder = Integer.parseInt(o2.order)
                    return@Comparator currentOrder - secondOrder
                }
            })
        }

        fun sortQusList(questionBeanFilledArrayList: List<QuestionBean>) {
            Collections.sort(questionBeanFilledArrayList, Comparator { o1, o2 ->
                try {
                    val currentOrder = Integer.parseInt(o1.viewSequence)
                    val secondOrder = Integer.parseInt(o2.viewSequence)
                    return@Comparator currentOrder - secondOrder

                } catch (e: Exception) {
                    val currentOrder = Integer.parseInt(o1.order)
                    val secondOrder = Integer.parseInt(o2.order)
                    return@Comparator currentOrder - secondOrder
                }


            })
        }

        fun isLoopingType(questionBeanFilled: QuestionBeanFilled): Boolean {
            return questionBeanFilled.input_type == LibDynamicAppConfig.QUS_LOOPING_MILTISELECT || questionBeanFilled.input_type == LibDynamicAppConfig.QUS_LOOPING
        }

        fun isLoopingType(questionBean: QuestionBean): Boolean {
            return questionBean.input_type == LibDynamicAppConfig.QUS_LOOPING_MILTISELECT || questionBean.input_type == LibDynamicAppConfig.QUS_LOOPING
        }

        fun isMediaUploadTypeQuestion(type: String): Boolean {
            return type == LibDynamicAppConfig.QUS_IMAGE || type == LibDynamicAppConfig.QUS_RECORD_AUDIO
        }


        fun getUpdatedChildOrder(forParentValue: String, questionQrder: String): String {
            return forParentValue + "_" + questionQrder
        }

        fun getAnswer(answers: Answers, inputType: String?): String {
            return if (inputType != null && inputType != "") {
                if (inputType == LibDynamicAppConfig.QUS_TEXT || inputType == LibDynamicAppConfig.QUS_ADDRESS) {
                    getValueFormTextInputType(answers)
                } else {
                    answers.value
                }
            } else {
                answers.value
            }

        }

        fun getViewableStringFormAns(questionBeanFilled: QuestionBeanFilled?): String {
            var viewText = StringBuilder()
            if (questionBeanFilled != null && questionBeanFilled.answer.isNotEmpty()) {
                when (questionBeanFilled.input_type) {
                    LibDynamicAppConfig.QUS_MULTI_SELECT, LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE, LibDynamicAppConfig.QUS_LOOPING_MILTISELECT -> {
                        var tempFix = ""
                        for (answers in questionBeanFilled.answer) {
                            viewText.append(tempFix).append(answers.label)
                            tempFix = ",\n"
                        }
                    }

                    LibDynamicAppConfig.QUS_DROPDOWN, LibDynamicAppConfig.QUS_DROPDOWN_HIDE, LibDynamicAppConfig.QUS_LOOPING, LibDynamicAppConfig.QUS_RADIO_BUTTONS -> if (questionBeanFilled.answer[0] != null) {
                        viewText = StringBuilder(questionBeanFilled.answer[0]!!.label)
                    }

                    LibDynamicAppConfig.QUS_TEXT, LibDynamicAppConfig.QUS_ADDRESS ->

                        if (questionBeanFilled.answer[0] != null) {
                            viewText = StringBuilder(getValueFormTextInputType(questionBeanFilled.answer[0]!!))
                        }

                    else -> if (questionBeanFilled.answer[0] != null) {
                        viewText = StringBuilder(questionBeanFilled.answer[0]!!.value)
                    }
                }
            }
            return viewText.toString()
        }

        fun getViewableStringFormAns(questionBeanFilled: QuestionBeanFilled?, questionBean: QuestionBean): String {
            var viewText = StringBuilder()
            if (questionBeanFilled != null && questionBeanFilled.answer.isNotEmpty()) {
                when (questionBeanFilled.input_type) {
                    LibDynamicAppConfig.QUS_MULTI_SELECT,
                    LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE,
                    LibDynamicAppConfig.QUS_LOOPING_MILTISELECT -> {
                        var tempFix = ""
                        for (answers in questionBeanFilled.answer) {
                            viewText.append(tempFix).append(getAnsLabel(answers, questionBean.answer_option))
                            tempFix = ",\n"
                        }
                    }

                    LibDynamicAppConfig.QUS_DROPDOWN,
                    LibDynamicAppConfig.QUS_DROPDOWN_HIDE,
                    LibDynamicAppConfig.QUS_LOOPING,
                    LibDynamicAppConfig.QUS_RADIO_BUTTONS -> if (questionBeanFilled.answer[0] != null) {
                        viewText = StringBuilder(getAnsLabel(questionBeanFilled.answer[0]!!, questionBean.answer_option))
                    }

                    LibDynamicAppConfig.QUS_TEXT,
                    LibDynamicAppConfig.QUS_ADDRESS ->

                        if (questionBeanFilled.answer[0] != null) {
                            viewText = StringBuilder(getValueFormTextInputType(questionBeanFilled.answer[0]!!))
                        }

                    else -> if (questionBeanFilled.answer[0] != null) {
                        viewText = StringBuilder(questionBeanFilled.answer[0]!!.value)
                    }
                }
            }
            return viewText.toString()
        }

        fun getAnsLabel(answers: Answers, answerOption: List<AnswerOptionsBean>): String {
            answerOption.find { it._id == answers.value }?.let {
                return it.name
            }
            return if (answers.label.isNotEmpty())
                answers.label
            else
                answers.value
        }

        fun getAnsLabelOnly(answers: Answers, answerOption: List<AnswerOptionsBean>): String? {
            answerOption.find { it._id == answers.value }?.let {
                return it.name
            }
            return answers.label
        }

        fun getValueFormTextInputType(answers: Answers): String {
            val textValue = answers.textValue
            val value = answers.value
            return if (textValue.trim { it <= ' ' }.isNotEmpty()) textValue else value
        }

        fun isItHasAns(answersList: List<Answers>): Boolean {
            if (answersList.isNotEmpty()) return (answersList.first().value.isNotEmpty() || answersList.first().textValue.isNotEmpty())
            return false


        }

        fun isNotifyRestriction(restrictionsBean: RestrictionsBean): Boolean {
            return arrayOf(LibDynamicAppConfig.REST_DID_RELATION,
                    LibDynamicAppConfig.REST_SHOULD_BE_LESS_THAN,
                    LibDynamicAppConfig.REST_SHOULD_BE_LESS_THAN_EQUAL,
                    LibDynamicAppConfig.REST_SHOULD_BE_GRATER_THAN,
                    LibDynamicAppConfig.REST_SHOULD_BE_GRATER_THAN_EQUAL,
                    LibDynamicAppConfig.REST_CALCULATE_AGE,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_SPLIT_MONTH,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_SPLIT_DAYS,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_IN_DAYS,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_IN_MONTHS

            ).contains(restrictionsBean.type)
        }

        fun isQuestionHasAutoFillRestriction(questionBean: QuestionBean): Boolean {
            return questionBean.restrictions.any { isAutoFillRestriction(it) }

        }

        fun isQuestionHasNotifyRestriction(questionBean: QuestionBean): Boolean {
            return questionBean.restrictions.any { isNotifyRestriction(it) }

        }

        fun isAutoFillRestriction(restrictionsBean: RestrictionsBean): Boolean {
            return arrayOf(LibDynamicAppConfig.REST_CALCULATE_AGE,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_SPLIT_MONTH,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_SPLIT_DAYS,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_IN_DAYS,
                    LibDynamicAppConfig.REST_CALCULATE_AGE_IN_MONTHS

            ).contains(restrictionsBean.type)
        }
    }


}


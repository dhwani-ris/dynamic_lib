package com.dhwaniris.dynamicForm.utils

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing
import com.dhwaniris.dynamicForm.db.dbhelper.MasterBlockBean
import com.dhwaniris.dynamicForm.db.dbhelper.MasterDistrictBean
import com.dhwaniris.dynamicForm.db.dbhelper.MasterVillageBean
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers
import com.dhwaniris.dynamicForm.db.dbhelper.form.ChildBean
import com.dhwaniris.dynamicForm.db.dbhelper.form.Did
import com.dhwaniris.dynamicForm.db.dbhelper.form.Nested
import com.dhwaniris.dynamicForm.db.dbhelper.form.OrdersBean
import com.dhwaniris.dynamicForm.db.dbhelper.form.ParentBean
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.regex.Pattern

import java.util.regex.Pattern.matches


class QuestionsUtils {

    fun getDecimalOrder(parentOrder: String, childOrder: String, decimalCount: Int): String {
        val childOrderInt = Integer.parseInt(childOrder)
        var newOrder = "$parentOrder.$childOrder"
        val decimalPlaces = String.format("%0" + decimalCount + "d", childOrderInt)
        if (childOrderInt % 10 == 0) {
            newOrder = parentOrder + "." + decimalPlaces.substring(0, decimalPlaces.length - 1)
        } else {
            newOrder = "$parentOrder.$decimalPlaces"
        }
        return newOrder
    }

    companion object {
        fun isAnswerIsExpected(uniqueKey: String, value: String, questionBeanFilledList: LinkedHashMap<String, QuestionBeanFilled>): Boolean {

            val questionBeanFilled = questionBeanFilledList[uniqueKey]
            if (questionBeanFilled != null) {
                val answers = questionBeanFilled.answer
                for (answers1 in answers) {
                    val answers1Value = answers1.value
                    if (answers1Value != "") {
                        val isMatchPatten = Pattern.matches(value, answers1Value)
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
            if (questionBean != null && questionBean.parent.size > 0) {
                val parentBean1 = questionBean.parent[0]
                isMatch = isAnswerIsExpected(getParentUniqueId(parentBean1), parentBean1.value, answerBeanHelperList)

                if (questionBean.parent.size > 1) {
                    isMatch = validateVisibilityWithMultiParent(questionBean, isMatch, answerBeanHelperList)
                }
            } else {
                return true
            }
            return isMatch
        }

        fun validateVisibilityWithMultiParent(questionBean: QuestionBean, isMatch: Boolean, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>): Boolean {
            var isMatch = isMatch
            var isOrCase = false
            var isVisibleOnParentsDiffrentValue = false
            for (validationBean in questionBean.validation) {
                if (validationBean._id == AppConfing.VAL_OR_CASE_WITH_MULTIPLE_PARENT) {
                    isOrCase = true
                    break
                } else if (validationBean._id == AppConfing.VAL_VISIBLE_ON_PARENTS_HAS_DIFFERENT_VALUES) {
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
                if (!answers.isEmpty() && answers[0] != null) {
                    return answers[0].value
                }
            }
            return null
        }

        fun getAnswerListFormRestriction(allOptions: List<AnswerOptionsBean>, restrictionsBean: RestrictionsBean?, questionBean: QuestionBean, questionBeanFilledList: LinkedHashMap<String, QuestionBeanFilled>): List<AnswerOptionsBean> {

            var avlList: MutableList<AnswerOptionsBean> = ArrayList()
            if (restrictionsBean == null) {
                return allOptions
            }
            when (restrictionsBean.type) {
                AppConfing.REST_GET_ANS_OPTION, AppConfing.REST_GET_ANS_OPTION_LOOPING -> {
                    val answers = ArrayList<Answers>()
                    //     avlList.addAll(allOptions);
                    for (ordersBean in restrictionsBean.orders) {
                        //requiredValue null if not want to match with any value
                        if (restrictionsBean.type == AppConfing.REST_GET_ANS_OPTION) {
                            answers.addAll(getAnswerListbyOder(getRestrictionOrderUniqueId(ordersBean), ordersBean.value, questionBeanFilledList))
                        } else {
                            //// get Answer_option_ data from looping (Nested Answer)
                            if (questionBean.parent.size > 0)
                                answers.addAll(getNestedAnswerListFromNestedType(getParentUniqueId(questionBean.parent[0]),
                                        getRestrictionOrderUniqueId(ordersBean), ordersBean.value, questionBeanFilledList))
                        }
                    }
                    var tempID = 1
                    for (answers1 in answers) {
                        if (answers1.value != "" && answers1.textValue == "") {
                            val answerOptionsBean = AnswerOptionsBean()
                            answerOptionsBean.name = answers1.textValue + "" + answers1.value + " " + answers1.label
                            answerOptionsBean._id = tempID.toString()
                            answerOptionsBean.did = ArrayList()
                            avlList.add(answerOptionsBean)
                        }
                        tempID++
                    }

                    //handling or case for filter (default AND case)
                    var isOrCase = false
                    for (validationBean in questionBean.validation) {
                        if (validationBean._id == AppConfing.VAL_OR_CASE_WITH_GET_FILTER_OPTION) {
                            isOrCase = true
                            break
                        }
                    }
                    var tempList: List<AnswerOptionsBean> = ArrayList()
                    ///apply filters on collected option
                    var isfilter = false
                    for (filterRestriction in questionBean.restrictions) {
                        if (filterRestriction.type == AppConfing.REST_GET_ANS_OPTION_FILTER || filterRestriction.type == AppConfing.REST_GET_ANS_OPTION_LOOPING_FILTER) {
                            isfilter = true
                            if (tempList.size == 0) {
                                tempList = filterOnAnsOptFormRestriction(avlList, filterRestriction, questionBean, questionBeanFilledList)
                            } else {
                                if (!isOrCase) {
                                    tempList = filterOnAnsOptFormRestriction(tempList, filterRestriction, questionBean, questionBeanFilledList)
                                } else {
                                    tempList = getOrCaseListFormAnswerBean(tempList, filterOnAnsOptFormRestriction(avlList, filterRestriction, questionBean, questionBeanFilledList))
                                }

                            }

                        }
                    }

                    if (isfilter) {
                        avlList.clear()
                        avlList.addAll(tempList)
                    }


                    for (validationBean in questionBean.validation) {
                        if (validationBean._id == AppConfing.VAL_ADD_DEFAULT_OPTION_DY_AO) {
                            avlList.addAll(questionBean.answer_options)
                            break
                        } else if (avlList.size == 0 && validationBean._id == AppConfing.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0) {
                            avlList.addAll(questionBean.answer_options)
                            break
                        }
                    }

                }


                AppConfing.REST_DID_RELATION -> {
                    val ordersBean = restrictionsBean.orders[0]
                    avlList = getDIDAnswerFormRestriction(allOptions, ordersBean, questionBeanFilledList)
                }
            }

            return avlList
        }

        fun filterOnAnsOptFormRestriction(allOptions: List<AnswerOptionsBean>, restrictionsBean: RestrictionsBean?, questionBean: QuestionBean, questionBeanFilledList: LinkedHashMap<String, QuestionBeanFilled>): List<AnswerOptionsBean> {

            val avlList = ArrayList<AnswerOptionsBean>()
            val tempList = ArrayList<AnswerOptionsBean>()

            if (restrictionsBean == null) {
                return allOptions
            }

            when (restrictionsBean.type) {
                AppConfing.REST_GET_ANS_OPTION_FILTER -> {
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
                            val isMatchPatten = Pattern.matches(resOrder[i].value, answers[i].value)
                            val isMatchValue = answers[i].value == resOrder[i].value
                            if (isMatchPatten || isMatchValue) {
                                val answerOptionsBean = AnswerOptionsBean()
                                answerOptionsBean.name = answers[i].textValue + "" + answers[i].value + " " + answers[i].label
                                answerOptionsBean._id = (i + 1).toString()
                                answerOptionsBean.did = ArrayList()
                                tempList.add(answerOptionsBean)
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
                }

                AppConfing.REST_GET_ANS_OPTION_LOOPING_FILTER -> {
                    val answers = ArrayList<Answers>()
                    if ((restrictionsBean.orders.size == 0) or (questionBean.parent.size == 0)) {
                        return allOptions
                    }
                    val resOrder = restrictionsBean.orders

                    for (ordersBean in resOrder) {
                        answers.addAll(getNestedAnswerListFromNestedType(getParentUniqueId(questionBean.parent[0]), getRestrictionOrderUniqueId(ordersBean), null, questionBeanFilledList))
                    }
                    //       int tempID = 1;

                    if (answers.size > 0 && restrictionsBean.orders.size > 0) {
                        for (i in answers.indices) {
                            val isMatchPatten = Pattern.matches(resOrder[0].value, answers[i].value)
                            val isMatchValue = answers[i].value == resOrder[0].value
                            if (isMatchPatten || isMatchValue) {
                                val answerOptionsBean = AnswerOptionsBean()
                                answerOptionsBean.name = answers[i].textValue + answers[i].value + " " + answers[i].label
                                answerOptionsBean._id = (i + 1).toString()
                                answerOptionsBean.did = ArrayList()
                                tempList.add(answerOptionsBean)
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
                }
            }

            return avlList
        }

        fun getNestedAnswerListFromNestedType(parentQuestionUniqueKey: String?, nestedUniqueKey: String?, requiredValueOrPattern: String?, answerList: LinkedHashMap<String, QuestionBeanFilled>): List<Answers> {
            //
            val finalAns = ArrayList<Answers>()
            if (parentQuestionUniqueKey != null && parentQuestionUniqueKey != "" && nestedUniqueKey != null && nestedUniqueKey != "") {
                val questionBeanFilled = answerList[parentQuestionUniqueKey]
                if (questionBeanFilled != null) {
                    val nestedList = questionBeanFilled.nestedAnswer
                    val tempAns = ArrayList<Answers>()
                    if (nestedList != null && nestedList.size > 0) {
                        for (nested in nestedList) {
                            val answerNestedData = nested.answerNestedData
                            for (questionBeanFilled1 in answerNestedData) {
                                if (!questionBeanFilled1.answer.isEmpty()) {
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
                                val isMatchPatten = Pattern.matches(requiredValueOrPattern, answers.value)
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


        fun getOrCaseListFormAnswerBean(answerOptionsBeans1: List<AnswerOptionsBean>,
                                        answerOptionsBeans2: List<AnswerOptionsBean>): List<AnswerOptionsBean> {
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

        fun getDIDAnswerFormRestriction(allOptions: List<AnswerOptionsBean>, ordersBean: OrdersBean?, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>): MutableList<AnswerOptionsBean> {

            var avlList: MutableList<AnswerOptionsBean> = ArrayList()

            if (ordersBean == null) {
                return allOptions
            }
            val didParentsAnswer: List<Answers>
            val questionBeanFilled = answerBeanHelperList[getRestrictionOrderUniqueId(ordersBean)]
            if (questionBeanFilled != null) {
                didParentsAnswer = questionBeanFilled.answer
                avlList = getDidAnswerFormParentAns(allOptions, didParentsAnswer)
            } else {
                avlList.addAll(allOptions)
            }
            return avlList
        }

        fun isValidFloat(value: String?): Boolean {
            if (value == null)
                return false
            if (value == "")
                return false
            return if (value[0] == '.') false else true
        }

        fun getDidAnswerFormParentAns(allOptions: List<AnswerOptionsBean>, parentsAnswer: List<Answers>): MutableList<AnswerOptionsBean> {

            val avlList = ArrayList<AnswerOptionsBean>()

            if (parentsAnswer.size == 0) {
                return allOptions
            }
            for (aob in allOptions) {
                if (aob.did.size > 0 && parentsAnswer.size > 0) {
                    var isInList = false
                    for (`as` in parentsAnswer) {
                        for (did in aob.did) {
                            val didValueOrPattern = did.parent_option
                            if (`as`.value != "") {
                                val isMatchPatten = matches(didValueOrPattern, `as`.value)
                                val isMatchValue = `as`.value == didValueOrPattern
                                isInList = isMatchPatten || isMatchValue
                                break
                            }
                        }
                        if (isInList) {
                            avlList.add(aob)
                            break
                        }
                    }


                } else
                    avlList.add(aob)
            }
            return avlList
        }

        fun getAnswerListbyOder(uniqueKey: String?, requiredValueOrPattern: String?, answerBeanList: LinkedHashMap<String, QuestionBeanFilled>): List<Answers> {

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
                            val isMatchPatten = Pattern.matches(requiredValueOrPattern, answers.value)
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
            return (inputType == AppConfing.QUS_DROPDOWN
                    || inputType == AppConfing.QUS_DROPDOWN_HIDE
                    || inputType == AppConfing.QUS_MULTI_SELECT
                    || inputType == AppConfing.QUS_MULTI_SELECT_HIDE
                    || inputType == AppConfing.QUS_MULTI_SELECT_LIMITED
                    || inputType == AppConfing.QUS_LOOPING
                    || inputType == AppConfing.QUS_LOOPING_MILTISELECT)
        }

        fun isEditTextType(inputType: String): Boolean {
            return (inputType == AppConfing.QUS_TEXT
                    || inputType == AppConfing.QUS_NUMBER
                    || inputType == AppConfing.QUS_ADDRESS
                    || inputType == AppConfing.QUS_AADHAAR)
        }


        fun validateMultiAnsRestriction(restrictionsBean: RestrictionsBean, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>, questionBeenList: LinkedHashMap<String, QuestionBean>): Boolean {

            if (!restrictionsBean.orders.isEmpty()) {
                val ordersBean = restrictionsBean.orders[0]
                if (ordersBean != null) {
                    val uniqueKey = getRestrictionOrderUniqueId(ordersBean)
                    val questionBeanFilled = answerBeanHelperList[uniqueKey]
                    val parentQuestion = questionBeenList[uniqueKey]
                    if (questionBeanFilled != null && parentQuestion != null) {
                        var allOptions = parentQuestion.answer_options
                        for (parentRestrictionBean in parentQuestion.restrictions) {
                            val restrictionType = parentRestrictionBean.type
                            if (restrictionType == AppConfing.REST_DID_RELATION ||
                                    restrictionType == AppConfing.REST_GET_ANS_OPTION ||
                                    restrictionType == AppConfing.REST_GET_ANS_OPTION_LOOPING ||
                                    restrictionType == AppConfing.REST_GET_ANS_SUM_LOOPING) {

                                allOptions = QuestionsUtils.getAnswerListFormRestriction(allOptions, parentRestrictionBean, parentQuestion, answerBeanHelperList)
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
                                    questionBeenList: LinkedHashMap<String, QuestionBean>): List<Answers> {
            var parentQuestion: QuestionBean?
            var parentAnswer: List<Answers> = ArrayList()
            for (parentBean in questionBean.parent) {
                parentQuestion = questionBeenList[getParentUniqueId(parentBean)]
                val parentAnswerBean = answerBeanHelperList[getParentUniqueId(parentBean)]
                if (parentQuestion != null && parentAnswerBean != null) {
                    parentAnswer = parentAnswerBean.answer
                    break
                }
            }
            return parentAnswer
        }


        fun getAnsOptionFromQuestionAfterFilter(questionBean: QuestionBean,
                                                questionBeenList: LinkedHashMap<String, QuestionBean>,
                                                answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>,
                                                userLanguage: String,
                                                formId: Int): List<AnswerOptionsBean> {

            var filteredList: MutableList<AnswerOptionsBean> = ArrayList()
            var allOptions = questionBean.answer_options


            var masterVillage = false
            var masterDistrict = false
            var masterBlocl = false
            var isDynamicOption = false
            var isDefaultOptionWhen0 = false
            var isDefaultOptionWhen = false


            for (validationBean in questionBean.validation) {
                when (validationBean._id) {
                    AppConfing.VAL_MASTER_DISTRICT -> masterDistrict = true
                    AppConfing.VAL_MASTER_BLOCK -> masterBlocl = true

                    AppConfing.VAL_MASTER_VILLAGE -> masterVillage = true
                    AppConfing.VAL_DYNAMIC_ANSWER_OPTION -> isDynamicOption = true
                    AppConfing.VAL_ADD_DEFAULT_OPTION_DY_AO -> isDefaultOptionWhen = true
                    AppConfing.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0 -> isDefaultOptionWhen0 = true
                }

            }
            val isDynamic = masterDistrict || masterBlocl || masterVillage || isDynamicOption
            if (isDynamic) {
                /*

            Realm realm = Realm.getDefaultInstance();
            Form singleform = realm.where(Form.class).equalTo("formId", formId).findFirst();
            String projectId = singleform.getProject().get_id();
            if (masterDistrict) {
                RealmResults<MasterDistrictBean> districtBeanRealmResults = realm.where(MasterDistrictBean.class).equalTo("projectId", projectId).findAll().sort("name");
                filteredList.clear();
                filteredList = prepareDistrictList(districtBeanRealmResults, userLanguage);


            } else if (masterBlocl) {
                List<Answers> parentAnswerDistrict = getParentAnswer(questionBean, answerBeanHelperList, questionBeenList);
                String districtId = parentAnswerDistrict.get(0).getValue();
                RealmResults<MasterBlockBean> blockListByDistrictId = realm.where(MasterBlockBean.class).equalTo("district", districtId).equalTo("projectId", projectId).findAll().sort("name");
                filteredList.clear();
                filteredList = prepareBlockList(blockListByDistrictId, userLanguage);

            } else if (masterVillage) {
                List<Answers> parentAnswerBlock = getParentAnswer(questionBean, answerBeanHelperList, questionBeenList);
                String blockId = parentAnswerBlock.get(0).getValue();
                RealmResults<MasterVillageBean> villageListByBlockId = realm.where(MasterVillageBean.class).equalTo("block", blockId).equalTo("projectId", projectId).findAll().sort("name");
                filteredList.clear();
                filteredList = prepareVillageList(villageListByBlockId, userLanguage);
            } else if (isDynamicOption) {
                String uniqueKey = formId + "_" + questionBean.getOrder();
                DynamicAnswerOption dyamicAnswerOption = realm.where(DynamicAnswerOption.class)
                        .equalTo("uniqueId", uniqueKey).findFirst();
                if (dyamicAnswerOption != null) {
                    allOptions.clear();
                    allOptions.addAll(realm.copyFromRealm(dyamicAnswerOption.getAnswer_option()));

                }

            }


            realm.close();
*/

            }

            ///restriction with did logic with any question;

            if (questionBean.restrictions.size > 0) {
                for (restrictionsBean in questionBean.restrictions) {
                    val restrictionType = restrictionsBean.type
                    if (restrictionType == AppConfing.REST_DID_RELATION ||
                            restrictionType == AppConfing.REST_GET_ANS_OPTION ||
                            restrictionType == AppConfing.REST_GET_ANS_OPTION_LOOPING ||
                            restrictionType == AppConfing.REST_GET_ANS_SUM_LOOPING) {
                        allOptions = QuestionsUtils.getAnswerListFormRestriction(allOptions, restrictionsBean, questionBean, answerBeanHelperList)
                        break
                    }

                }
            }

            /////////////////////////////////to show option which are  in parent
            if (questionBean.input_type == AppConfing.QUS_MULTI_SELECT
                    || questionBean.input_type == AppConfing.QUS_DROPDOWN
                    || questionBean.input_type == AppConfing.QUS_LOOPING
                    || questionBean.input_type == AppConfing.QUS_LOOPING_MILTISELECT) {
                filteredList.addAll(allOptions)

            } else
            /*if (questionBean.getInput_type().equals(AppConfing.QUS_DROPDOWN_HIDE)
                || questionBean.getInput_type().equals(AppConfing.QUS_MULTI_SELECT_HIDE))*/ {

                ///////////////////////////////////to hide option in parent
                ////nested parent
                val grandParentAns = ArrayList<Answers>()
                var parentQuestion: QuestionBean?
                for (parentBean in questionBean.parent) {
                    parentQuestion = questionBeenList[getParentUniqueId(parentBean)]
                    if (parentQuestion != null) {
                        val parentQuestionType = parentQuestion.input_type
                        if (parentQuestionType == AppConfing.QUS_DROPDOWN ||
                                parentQuestionType == AppConfing.QUS_MULTI_SELECT ||
                                parentQuestionType == AppConfing.QUS_MULTI_SELECT_LIMITED ||
                                parentQuestionType == AppConfing.QUS_DROPDOWN_HIDE ||
                                parentQuestionType == AppConfing.QUS_MULTI_SELECT_HIDE) {
                            grandParentAns.addAll(getGrandParentAns(parentQuestion, questionBeenList, answerBeanHelperList))
                            break
                        }
                    }
                }

                filteredList = getSkipAnswerFormParentAns(allOptions, grandParentAns)

            }


            if (isDefaultOptionWhen) {
                filteredList.addAll(questionBean.answer_options)
            } else if (isDefaultOptionWhen0 && filteredList.isEmpty()) {
                filteredList.addAll(questionBean.answer_options)
            }
            return filteredList
        }

        private fun prepareDistrictList(districtBeanRealmResults: List<MasterDistrictBean>, userLanguage: String): List<AnswerOptionsBean> {
            val districtBeanList = ArrayList<AnswerOptionsBean>()

            for (districtBean in districtBeanRealmResults) {
                val districtOption = AnswerOptionsBean()
                if (districtBean != null) {
                    districtOption._id = districtBean.id
                    districtBeanList.add(districtOption)
                    districtOption.name = if (userLanguage == "en") districtBean.name else districtBean.regionalName
                }
            }

            return districtBeanList

        }

        private fun prepareVillageList(villageListByBlockId: List<MasterVillageBean>, userLanguage: String): List<AnswerOptionsBean> {
            val villageBeanList = ArrayList<AnswerOptionsBean>()

            for (villageBean in villageListByBlockId) {
                val villageOption = AnswerOptionsBean()
                if (villageBean != null) {
                    villageOption.name = if (userLanguage == "en") villageBean.name else villageBean.regionalName
                    villageOption._id = villageBean.id
                    villageBeanList.add(villageOption)
                }
            }


            return villageBeanList
        }

        private fun prepareBlockList(blockListByDistrictId: List<MasterBlockBean>, userLanguage: String): List<AnswerOptionsBean> {
            val blockBeanList = ArrayList<AnswerOptionsBean>()

            for (blockBean in blockListByDistrictId) {
                val blockOption = AnswerOptionsBean()
                if (blockBean != null) {
                    blockOption._id = blockBean.id
                    blockOption.name = if (userLanguage == "en") blockBean.name else blockBean.regionalName
                    blockBeanList.add(blockOption)
                }
            }
            return blockBeanList
        }


        private fun getGrandParentAns(questionBean: QuestionBean?, questionBeenList: LinkedHashMap<String, QuestionBean>, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>): List<Answers> {
            var questionBean = questionBean
            val finalAns = ArrayList<Answers>()
            val questionBeanFilled = answerBeanHelperList[getQuestionUniqueId(questionBean!!)]

            while (questionBean != null && questionBeanFilled != null) {
                val p_ans = questionBeanFilled.answer
                finalAns.addAll(p_ans)
                var nextQuestion: QuestionBean? = null
                if (questionBean.input_type == AppConfing.QUS_DROPDOWN_HIDE || questionBean.input_type == AppConfing.QUS_MULTI_SELECT_HIDE) {

                    for (parentBean in questionBean.parent) {
                        val tempQuestionBean = questionBeenList[getParentUniqueId(parentBean)]
                        if (tempQuestionBean != null) {
                            val questionType = tempQuestionBean.input_type
                            if (questionType == AppConfing.QUS_DROPDOWN ||
                                    questionType == AppConfing.QUS_MULTI_SELECT ||
                                    questionType == AppConfing.QUS_MULTI_SELECT_LIMITED ||
                                    questionType == AppConfing.QUS_DROPDOWN_HIDE ||
                                    questionType == AppConfing.QUS_MULTI_SELECT_HIDE) {
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

        private fun getSkipAnswerFormParentAns(allOptions: List<AnswerOptionsBean>, parentsAnswer: List<Answers>): MutableList<AnswerOptionsBean> {

            val avlList = ArrayList<AnswerOptionsBean>()

            if (parentsAnswer.size == 0) {
                return allOptions
            }

            for (aob in allOptions) {
                if (parentsAnswer.size > 0) {
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


        val newAnswerList: List<Answers>
            get() {
                val answerBeanHelperList = ArrayList<Answers>()
                val answers = Answers()
                answers.label = ""
                answers.reference = ""
                answers.value = ""
                answers.textValue = ""
                answerBeanHelperList.add(answers)
                return answerBeanHelperList
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


        fun sortAnsList(questionBeanFilledList: List<QuestionBeanFilled>) {
            Collections.sort(questionBeanFilledList) { o1, o2 ->
                try {
                    val currentOrder = Integer.parseInt(o1.viewSequence)
                    val secondOrder = Integer.parseInt(o2.viewSequence)
                    return@Collections.sort currentOrder -secondOrder

                } catch (e: Exception) {
                    val currentOrder = Integer.parseInt(o1.order)
                    val secondOrder = Integer.parseInt(o2.order)
                    return@Collections.sort currentOrder -secondOrder
                }


            }
        }

        fun sortQusList(questionBeanFilledList: List<QuestionBean>) {
            Collections.sort(questionBeanFilledList) { o1, o2 ->
                try {
                    val currentOrder = Integer.parseInt(o1.viewSequence)
                    val secondOrder = Integer.parseInt(o2.viewSequence)
                    return@Collections.sort currentOrder -secondOrder

                } catch (e: Exception) {
                    val currentOrder = Integer.parseInt(o1.order)
                    val secondOrder = Integer.parseInt(o2.order)
                    return@Collections.sort currentOrder -secondOrder
                }


            }
        }

        fun sortQusChildList(questionBeanFilledList: List<QuestionBean>) {
            Collections.sort(questionBeanFilledList) { o1, o2 ->
                try {
                    val currentOrder = Integer.parseInt(o1.viewSequence)
                    val secondOrder = Integer.parseInt(o2.viewSequence)
                    return@Collections.sort currentOrder -secondOrder

                } catch (e: Exception) {
                    val currentOrder = Integer.parseInt(o1.order)
                    val secondOrder = Integer.parseInt(o2.order)
                    return@Collections.sort currentOrder -secondOrder
                }


            }
        }

        fun isLoopingType(questionBeanFilled: QuestionBeanFilled): Boolean {
            return if (questionBeanFilled.input_type == AppConfing.QUS_LOOPING_MILTISELECT || questionBeanFilled.input_type == AppConfing.QUS_LOOPING) {
                true
            } else false
        }

        fun isMediaUploadTypeQuestion(type: String): Boolean {
            return type == AppConfing.QUS_IMAGE || type == AppConfing.QUS_RECORD_AUDIO
        }


        fun getUpdatedChildOrder(forParentValue: String, questionQrder: String): String {
            return forParentValue + "_" + questionQrder
        }

        fun getAnswer(answers: Answers, inputType: String?): String {
            return if (inputType != null && inputType != "") {
                if (inputType == AppConfing.QUS_TEXT || inputType == AppConfing.QUS_ADDRESS) {
                    answers.textValue
                } else {
                    answers.value
                }
            } else {
                answers.value
            }

        }

        fun getViewableStringFormAns(questionBeanFilled: QuestionBeanFilled?): String {
            var viewText = StringBuilder()
            if (questionBeanFilled != null && !questionBeanFilled.answer.isEmpty()) {
                when (questionBeanFilled.input_type) {
                    AppConfing.QUS_MULTI_SELECT, AppConfing.QUS_MULTI_SELECT_HIDE, AppConfing.QUS_LOOPING_MILTISELECT -> {
                        var tempFix = ""
                        for (answers in questionBeanFilled.answer) {
                            viewText.append(tempFix).append(answers.label)
                            tempFix = ","
                        }
                    }

                    AppConfing.QUS_DROPDOWN, AppConfing.QUS_DROPDOWN_HIDE, AppConfing.QUS_LOOPING, AppConfing.QUS_RADIO_BUTTONS -> if (questionBeanFilled.answer[0] != null) {
                        viewText = StringBuilder(questionBeanFilled.answer[0].label)
                    }

                    AppConfing.QUS_TEXT, AppConfing.QUS_ADDRESS ->

                        if (questionBeanFilled.answer[0] != null) {
                            viewText = StringBuilder(questionBeanFilled.answer[0].textValue)
                        }

                    else -> if (questionBeanFilled.answer[0] != null) {
                        viewText = StringBuilder(questionBeanFilled.answer[0].value)
                    }
                }
            }
            return viewText.toString()
        }
    }
}

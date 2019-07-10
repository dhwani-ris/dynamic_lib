package com.dhwaniris.dynamicForm.utils;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.db.dbhelper.MasterBlockBean;
import com.dhwaniris.dynamicForm.db.dbhelper.MasterDistrictBean;
import com.dhwaniris.dynamicForm.db.dbhelper.MasterVillageBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ChildBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Did;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Nested;
import com.dhwaniris.dynamicForm.db.dbhelper.form.OrdersBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ParentBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.matches;


public class QuestionsUtils {
    public static boolean isAnswerIsExpected(String uniqueKey, String value, LinkedHashMap<String, QuestionBeanFilled> questionBeanFilledList) {

        QuestionBeanFilled questionBeanFilled = questionBeanFilledList.get(uniqueKey);
        if (questionBeanFilled != null) {
            List<Answers> answers = questionBeanFilled.getAnswer();
            for (Answers answers1 : answers) {
                String answers1Value = answers1.getValue();
                if (!answers1Value.equals("")) {
                    boolean isMatchPatten = Pattern.matches(value, answers1Value);
                    boolean isMatchValue = value.equals(answers1Value);
                    if (isMatchPatten || isMatchValue) {
                        return true;
                    }
                }
            }

        } else {
            return true;
        }
        return false;
    }


    public static boolean checkValueForVisibility(QuestionBean questionBean, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        boolean isMatch;
        if (questionBean != null && questionBean.getParent().size() > 0) {
            ParentBean parentBean1 = questionBean.getParent().get(0);
            isMatch = isAnswerIsExpected(getParentUniqueId(parentBean1), parentBean1.getValue(), answerBeanHelperList);

            if (questionBean.getParent().size() > 1) {
                isMatch = validateVisibilityWithMultiParent(questionBean, isMatch, answerBeanHelperList);
            }
        } else {
            return true;
        }
        return isMatch;
    }

    public static boolean validateVisibilityWithMultiParent(QuestionBean questionBean, boolean isMatch, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        boolean isOrCase = false;
        boolean isVisibleOnParentsDiffrentValue = false;
        for (ValidationBean validationBean : questionBean.getValidation()) {
            if (validationBean.get_id().equals(AppConfing.VAL_OR_CASE_WITH_MULTIPLE_PARENT)) {
                isOrCase = true;
                break;
            } else if (validationBean.get_id().equals(AppConfing.VAL_VISIBLE_ON_PARENTS_HAS_DIFFERENT_VALUES)) {
                isVisibleOnParentsDiffrentValue = true;
                break;
            }
        }
        String valueInParent = null;
        for (ParentBean parentBean : questionBean.getParent()) {
            if (!isVisibleOnParentsDiffrentValue) {
                if (!isOrCase) {
                    isMatch = isMatch && isAnswerIsExpected(getParentUniqueId(parentBean), parentBean.getValue(), answerBeanHelperList);
                } else {
                    isMatch = isMatch || isAnswerIsExpected(getParentUniqueId(parentBean), parentBean.getValue(), answerBeanHelperList);
                }
            } else {
                if (valueInParent == null) {
                    valueInParent = getSingleAnswerValueFormOder(getParentUniqueId(parentBean), answerBeanHelperList);
                } else {
                    isMatch = !valueInParent.equals(getSingleAnswerValueFormOder(getParentUniqueId(parentBean), answerBeanHelperList));
                    valueInParent = getSingleAnswerValueFormOder(getParentUniqueId(parentBean), answerBeanHelperList);
                }

            }

        }
        return isMatch;
    }

    private static String getSingleAnswerValueFormOder(String uniqueKey, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {

        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(uniqueKey);

        if (questionBeanFilled != null) {
            List<Answers> answers = questionBeanFilled.getAnswer();
            if (!answers.isEmpty() && answers.get(0) != null) {
                return answers.get(0).getValue();
            }
        }
        return null;
    }

    public static List<AnswerOptionsBean> getAnswerListFormRestriction
            (List<AnswerOptionsBean> allOptions, RestrictionsBean restrictionsBean, QuestionBean
                    questionBean, LinkedHashMap<String, QuestionBeanFilled> questionBeanFilledList) {

        List<AnswerOptionsBean> avlList = new ArrayList<>();
        if (restrictionsBean == null) {
            return allOptions;
        }
        switch (restrictionsBean.getType()) {
            case AppConfing.REST_GET_ANS_OPTION:
            case AppConfing.REST_GET_ANS_OPTION_LOOPING: {
                List<Answers> answers = new ArrayList<>();
                //     avlList.addAll(allOptions);
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    //requiredValue null if not want to match with any value
                    if (restrictionsBean.getType().equals(AppConfing.REST_GET_ANS_OPTION)) {
                        answers.addAll(getAnswerListbyOder(getRestrictionOrderUniqueId(ordersBean), ordersBean.getValue(), questionBeanFilledList));
                    } else {
                        //// get Answer_option_ data from looping (Nested Answer)
                        if (questionBean.getParent().size() > 0)
                            answers.addAll(getNestedAnswerListFromNestedType(getParentUniqueId(questionBean.getParent().get(0)),
                                    getRestrictionOrderUniqueId(ordersBean), ordersBean.getValue(), questionBeanFilledList));
                    }
                }
                int tempID = 1;
                for (Answers answers1 : answers) {
                    if (!answers1.getValue().equals("") && answers1.getTextValue().equals("")) {
                        AnswerOptionsBean answerOptionsBean = new AnswerOptionsBean();
                        answerOptionsBean.setName(answers1.getTextValue() + "" + answers1.getValue() + " " + answers1.getLabel());
                        answerOptionsBean.set_id(String.valueOf(tempID));
                        answerOptionsBean.setDid(new ArrayList<>());
                        avlList.add(answerOptionsBean);
                    }
                    tempID++;
                }

                //handling or case for filter (default AND case)
                boolean isOrCase = false;
                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(AppConfing.VAL_OR_CASE_WITH_GET_FILTER_OPTION)) {
                        isOrCase = true;
                        break;
                    }
                }
                List<AnswerOptionsBean> tempList = new ArrayList<>();
                ///apply filters on collected option
                boolean isfilter = false;
                for (RestrictionsBean filterRestriction : questionBean.getRestrictions()) {
                    if (filterRestriction.getType().equals(AppConfing.REST_GET_ANS_OPTION_FILTER)
                            || filterRestriction.getType().equals(AppConfing.REST_GET_ANS_OPTION_LOOPING_FILTER)) {
                        isfilter = true;
                        if (tempList.size() == 0) {
                            tempList = filterOnAnsOptFormRestriction(avlList, filterRestriction, questionBean, questionBeanFilledList);
                        } else {
                            if (!isOrCase) {
                                tempList = filterOnAnsOptFormRestriction(tempList, filterRestriction, questionBean, questionBeanFilledList);
                            } else {
                                tempList = getOrCaseListFormAnswerBean(tempList, filterOnAnsOptFormRestriction(avlList, filterRestriction, questionBean, questionBeanFilledList));
                            }

                        }

                    }
                }

                if (isfilter) {
                    avlList.clear();
                    avlList.addAll(tempList);
                }


                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(AppConfing.VAL_ADD_DEFAULT_OPTION_DY_AO)) {
                        avlList.addAll(questionBean.getAnswer_options());
                        break;
                    } else if (avlList.size() == 0 &&
                            validationBean.get_id().equals(AppConfing.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0)) {
                        avlList.addAll(questionBean.getAnswer_options());
                        break;
                    }
                }

            }
            break;


            case AppConfing.REST_DID_RELATION: {
                OrdersBean ordersBean = restrictionsBean.getOrders().get(0);
                avlList = getDIDAnswerFormRestriction(allOptions, ordersBean, questionBeanFilledList);
            }
            break;


        }

        return avlList;
    }

    public static List<AnswerOptionsBean> filterOnAnsOptFormRestriction
            (List<AnswerOptionsBean> allOptions, RestrictionsBean restrictionsBean, QuestionBean
                    questionBean, LinkedHashMap<String, QuestionBeanFilled> questionBeanFilledList) {

        List<AnswerOptionsBean> avlList = new ArrayList<>();
        List<AnswerOptionsBean> tempList = new ArrayList<>();

        if (restrictionsBean == null) {
            return allOptions;
        }

        switch (restrictionsBean.getType()) {
            case AppConfing.REST_GET_ANS_OPTION_FILTER: {
                List<Answers> answers = new ArrayList<>();
                if (restrictionsBean.getOrders().size() == 0) {
                    return allOptions;
                }
                List<OrdersBean> resOrder = restrictionsBean.getOrders();
                for (OrdersBean ordersBean : resOrder)
                    answers.addAll(getAnswerListbyOder(getRestrictionOrderUniqueId(ordersBean), null, questionBeanFilledList));
                //       int tempID = 1;

                if (answers.size() == restrictionsBean.getOrders().size()) {
                    for (int i = 0; i < answers.size(); i++) {
                        boolean isMatchPatten = Pattern.matches(resOrder.get(i).getValue(), answers.get(i).getValue());
                        boolean isMatchValue = answers.get(i).getValue().equals(resOrder.get(i).getValue());
                        if (isMatchPatten || isMatchValue) {
                            AnswerOptionsBean answerOptionsBean = new AnswerOptionsBean();
                            answerOptionsBean.setName(answers.get(i).getTextValue() + "" + answers.get(i).getValue() + " " + answers.get(i).getLabel());
                            answerOptionsBean.set_id(String.valueOf(i + 1));
                            answerOptionsBean.setDid(new ArrayList<Did>());
                            tempList.add(answerOptionsBean);
                        }

                    }
                }

                for (AnswerOptionsBean answerOptionsBean : allOptions) {
                    for (AnswerOptionsBean answerOptionsBeanTemp : tempList) {
                        if (answerOptionsBean.get_id().equals(answerOptionsBeanTemp.get_id())) {
                            avlList.add(answerOptionsBean);
                            break;
                        }

                    }
                }
            }
            break;

            case AppConfing.REST_GET_ANS_OPTION_LOOPING_FILTER: {
                List<Answers> answers = new ArrayList<>();
                if (restrictionsBean.getOrders().size() == 0 | questionBean.getParent().size() == 0) {
                    return allOptions;
                }
                List<OrdersBean> resOrder = restrictionsBean.getOrders();

                for (OrdersBean ordersBean : resOrder) {
                    answers.addAll(getNestedAnswerListFromNestedType(getParentUniqueId(questionBean.getParent().get(0)), getRestrictionOrderUniqueId(ordersBean), null, questionBeanFilledList));
                }
                //       int tempID = 1;

                if (answers.size() > 0 && restrictionsBean.getOrders().size() > 0) {
                    for (int i = 0; i < answers.size(); i++) {
                        boolean isMatchPatten = Pattern.matches(resOrder.get(0).getValue(), answers.get(i).getValue());
                        boolean isMatchValue = answers.get(i).getValue().equals(resOrder.get(0).getValue());
                        if (isMatchPatten || isMatchValue) {
                            AnswerOptionsBean answerOptionsBean = new AnswerOptionsBean();
                            answerOptionsBean.setName(answers.get(i).getTextValue() + answers.get(i).getValue() + " " + answers.get(i).getLabel());
                            answerOptionsBean.set_id(String.valueOf(i + 1));
                            answerOptionsBean.setDid(new ArrayList<Did>());
                            tempList.add(answerOptionsBean);
                        }

                    }
                }

                for (AnswerOptionsBean answerOptionsBean : allOptions) {
                    for (AnswerOptionsBean answerOptionsBeanTemp : tempList) {
                        if (answerOptionsBean.get_id().equals(answerOptionsBeanTemp.get_id())) {
                            avlList.add(answerOptionsBean);
                            break;
                        }

                    }
                }
            }
            break;

        }

        return avlList;
    }

    public String getDecimalOrder(String parentOrder, String childOrder, int decimalCount) {
        int childOrderInt = Integer.parseInt(childOrder);
        String newOrder = parentOrder + "." + childOrder;
        String decimalPlaces = String.format("%0" + decimalCount + "d", childOrderInt);
        if (childOrderInt % 10 == 0) {
            newOrder = parentOrder + "." + decimalPlaces.substring(0, decimalPlaces.length() - 1);
        } else {
            newOrder = parentOrder + "." + decimalPlaces;
        }
        return newOrder;
    }

    public static List<Answers> getNestedAnswerListFromNestedType(String parentQuestionUniqueKey, String nestedUniqueKey, String requiredValueOrPattern, LinkedHashMap<String, QuestionBeanFilled> answerList) {
        //
        List<Answers> finalAns = new ArrayList<>();
        if (parentQuestionUniqueKey != null && !parentQuestionUniqueKey.equals("") && nestedUniqueKey != null && !nestedUniqueKey.equals("")) {
            QuestionBeanFilled questionBeanFilled = answerList.get(parentQuestionUniqueKey);
            if (questionBeanFilled != null) {
                List<Nested> nestedList = questionBeanFilled.getNestedAnswer();
                List<Answers> tempAns = new ArrayList<>();
                if (nestedList != null && nestedList.size() > 0) {
                    for (Nested nested : nestedList) {
                        List<QuestionBeanFilled> answerNestedData = nested.getAnswerNestedData();
                        for (QuestionBeanFilled questionBeanFilled1 : answerNestedData) {
                            if (!questionBeanFilled1.getAnswer().isEmpty()) {
                                if (getAnswerUniqueId(questionBeanFilled1).equals(nestedUniqueKey)) {
                                    tempAns.add(questionBeanFilled1.getAnswer().get(0));
                                }
                            }
                        }

                    }
                    if (requiredValueOrPattern == null) {
                        finalAns.addAll(tempAns);
                    } else if (requiredValueOrPattern.equals("")) {
                        finalAns.addAll(tempAns);
                    } else {
                        for (Answers answers : tempAns) {
                            boolean isMatchPatten = Pattern.matches(requiredValueOrPattern, answers.getValue());
                            boolean isMatchValue = answers.getValue().equals(requiredValueOrPattern);
                            if (isMatchPatten || isMatchValue) {
                                finalAns.addAll(tempAns);
                                break;
                            }
                        }
                    }


                } else {
                    return finalAns;
                }

            }

        }

        return finalAns;
    }


    public static List<AnswerOptionsBean> getOrCaseListFormAnswerBean(List<AnswerOptionsBean> answerOptionsBeans1,
                                                                           List<AnswerOptionsBean> answerOptionsBeans2) {
        List<AnswerOptionsBean> oRCaseList = new ArrayList<>();
        HashMap<String, AnswerOptionsBean> oRCaseListMap = new HashMap<>();
        for (AnswerOptionsBean answerOptionsBean : answerOptionsBeans1) {
            oRCaseListMap.put(answerOptionsBean.get_id(), answerOptionsBean);
        }
        for (AnswerOptionsBean answerOptionsBean : answerOptionsBeans2) {
            oRCaseListMap.put(answerOptionsBean.get_id(), answerOptionsBean);
        }
        oRCaseList.addAll(oRCaseListMap.values());
        return oRCaseList;
    }

    public static List<AnswerOptionsBean> getDIDAnswerFormRestriction
            (List<AnswerOptionsBean> allOptions, OrdersBean ordersBean, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {

        List<AnswerOptionsBean> avlList = new ArrayList<>();

        if (ordersBean == null) {
            return allOptions;
        }
        List<Answers> didParentsAnswer;
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(getRestrictionOrderUniqueId(ordersBean));
        if (questionBeanFilled != null) {
            didParentsAnswer = questionBeanFilled.getAnswer();
            avlList = getDidAnswerFormParentAns(allOptions, didParentsAnswer);
        } else {
            avlList.addAll(allOptions);
        }
        return avlList;
    }

    public static boolean isValidFloat(String value) {
        if (value == null)
            return false;
        if (value.equals(""))
            return false;
        if (value.charAt(0) == '.')
            return false;
        return true;
    }

    public static List<AnswerOptionsBean> getDidAnswerFormParentAns
            (List<AnswerOptionsBean> allOptions, List<Answers> parentsAnswer) {

        List<AnswerOptionsBean> avlList = new ArrayList<>();

        if (parentsAnswer.size() == 0) {
            return allOptions;
        }
        for (AnswerOptionsBean aob : allOptions) {
            if (aob.getDid().size() > 0 && parentsAnswer.size() > 0) {
                boolean isInList = false;
                for (Answers as : parentsAnswer) {
                    for (Did did : aob.getDid()) {
                        String didValueOrPattern = did.getParent_option();
                        if (!as.getValue().equals("")) {
                            boolean isMatchPatten = matches(didValueOrPattern, as.getValue());
                            boolean isMatchValue = as.getValue().equals(didValueOrPattern);
                            isInList = isMatchPatten || isMatchValue;
                            break;
                        }
                    }
                    if (isInList) {
                        avlList.add(aob);
                        break;
                    }
                }


            } else
                avlList.add(aob);
        }
        return avlList;
    }

    public static List<Answers> getAnswerListbyOder(String uniqueKey, String requiredValueOrPattern, LinkedHashMap<String, QuestionBeanFilled> answerBeanList) {

        //

        List<Answers> finalAns = new ArrayList<>();
        if (uniqueKey != null && !uniqueKey.equals("")) {
            QuestionBeanFilled questionBeanFilled = answerBeanList.get(uniqueKey);
            if (questionBeanFilled != null) {
                List<Answers> p_ans = questionBeanFilled.getAnswer();

                if (requiredValueOrPattern == null) {
                    finalAns.addAll(p_ans);
                } else if (requiredValueOrPattern.equals("")) {
                    finalAns.addAll(p_ans);
                } else {
                    for (Answers answers : p_ans) {
                        boolean isMatchPatten = Pattern.matches(requiredValueOrPattern, answers.getValue());
                        boolean isMatchValue = answers.getValue().equals(requiredValueOrPattern);
                        if (isMatchPatten || isMatchValue) {
                            finalAns.addAll(p_ans);
                            break;
                        }
                    }


                }
            }


        }

        return finalAns;
    }

    public static boolean validateAnswerListWithAnswerOptions(List<Answers> answersList, List<AnswerOptionsBean> availableOpiton) {
        for (Answers answers : answersList) {
            boolean found = false;
            if (!answers.getValue().equals("")) {
                for (AnswerOptionsBean answerOptionsBean : availableOpiton) {
                    if (answers.getValue().trim().equals(answerOptionsBean.get_id().trim())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSelectType(String inputType) {
        return (inputType.equals(AppConfing.QUS_DROPDOWN)
                || inputType.equals(AppConfing.QUS_DROPDOWN_HIDE)
                || inputType.equals(AppConfing.QUS_MULTI_SELECT)
                || inputType.equals(AppConfing.QUS_MULTI_SELECT_HIDE)
                || inputType.equals(AppConfing.QUS_MULTI_SELECT_LIMITED)
                || inputType.equals(AppConfing.QUS_LOOPING)
                || inputType.equals(AppConfing.QUS_LOOPING_MILTISELECT)
        );
    }

    public static boolean isEditTextType(String inputType) {
        return (inputType.equals(AppConfing.QUS_TEXT)
                || inputType.equals(AppConfing.QUS_NUMBER)
                || inputType.equals(AppConfing.QUS_ADDRESS)
                || inputType.equals(AppConfing.QUS_AADHAAR)
        );
    }


    public static boolean validateMultiAnsRestriction(RestrictionsBean restrictionsBean, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList
            , LinkedHashMap<String, QuestionBean> questionBeenList) {

        if (!restrictionsBean.getOrders().isEmpty()) {
            OrdersBean ordersBean = restrictionsBean.getOrders().get(0);
            if (ordersBean != null) {
                String uniqueKey = getRestrictionOrderUniqueId(ordersBean);
                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(uniqueKey);
                QuestionBean parentQuestion = questionBeenList.get(uniqueKey);
                if (questionBeanFilled != null && parentQuestion != null) {
                    List<AnswerOptionsBean> allOptions = parentQuestion.getAnswer_options();
                    for (RestrictionsBean parentRestrictionBean : parentQuestion.getRestrictions()) {
                        String restrictionType = parentRestrictionBean.getType();
                        if (restrictionType.equals(AppConfing.REST_DID_RELATION) ||
                                restrictionType.equals(AppConfing.REST_GET_ANS_OPTION) ||
                                restrictionType.equals(AppConfing.REST_GET_ANS_OPTION_LOOPING) ||
                                restrictionType.equals(AppConfing.REST_GET_ANS_SUM_LOOPING)) {

                            allOptions = QuestionsUtils.getAnswerListFormRestriction(allOptions, parentRestrictionBean, parentQuestion, answerBeanHelperList);
                            break;
                        }

                    }
                    List<String> allAvailableOptionsForParent = new ArrayList<>();
                    for (AnswerOptionsBean answerOptionsBean : allOptions) {
                        allAvailableOptionsForParent.add(answerOptionsBean.get_id());
                    }

                    List<Answers> answersList = questionBeanFilled.getAnswer();
                    List<String> answerStringList = new ArrayList<>();
                    for (Answers answers : answersList) {
                        answerStringList.add(answers.getValue());
                    }

                    for (OrdersBean ordersBean1 : restrictionsBean.getOrders()) {
                        if (!answerStringList.contains(ordersBean1.getValue())
                                && allAvailableOptionsForParent.contains(ordersBean1.getValue())) {
                            return true;
                        }
                    }

                }
            }
        }

        return false;
    }

    private static List<Answers> getParentAnswer(QuestionBean questionBean, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList,
                                                      LinkedHashMap<String, QuestionBean> questionBeenList) {
        QuestionBean parentQuestion;
        List<Answers> parentAnswer = new ArrayList<>();
        for (ParentBean parentBean : questionBean.getParent()) {
            parentQuestion = questionBeenList.get(getParentUniqueId(parentBean));
            QuestionBeanFilled parentAnswerBean = answerBeanHelperList.get(getParentUniqueId(parentBean));
            if (parentQuestion != null && parentAnswerBean != null) {
                parentAnswer = parentAnswerBean.getAnswer();
                break;
            }
        }
        return parentAnswer;
    }


    public static List<AnswerOptionsBean> getAnsOptionFromQuestionAfterFilter(QuestionBean questionBean,
                                                                                   LinkedHashMap<String, QuestionBean> questionBeenList,
                                                                                   LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList,
                                                                                   String userLanguage,
                                                                                   int formId) {

        List<AnswerOptionsBean> filteredList = new ArrayList<>();
        List<AnswerOptionsBean> allOptions = questionBean.getAnswer_options();


        boolean masterVillage = false;
        boolean masterDistrict = false;
        boolean masterBlocl = false;
        boolean isDynamicOption = false;
        boolean isDefaultOptionWhen0 = false;
        boolean isDefaultOptionWhen = false;


        for (ValidationBean validationBean : questionBean.getValidation()) {
            switch (validationBean.get_id()) {
                case AppConfing.VAL_MASTER_DISTRICT:
                    masterDistrict = true;
                    break;
                case AppConfing.VAL_MASTER_BLOCK:
                    masterBlocl = true;
                    break;

                case AppConfing.VAL_MASTER_VILLAGE:
                    masterVillage = true;
                    break;
                case AppConfing.VAL_DYNAMIC_ANSWER_OPTION:
                    isDynamicOption = true;
                    break;
                case AppConfing.VAL_ADD_DEFAULT_OPTION_DY_AO:
                    isDefaultOptionWhen = true;
                    break;
                case AppConfing.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0:
                    isDefaultOptionWhen0 = true;
                    break;

            }

        }
        boolean isDynamic = masterDistrict || masterBlocl || masterVillage || isDynamicOption;
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

        if (questionBean.getRestrictions().size() > 0) {
            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                String restrictionType = restrictionsBean.getType();
                if (restrictionType.equals(AppConfing.REST_DID_RELATION) ||
                        restrictionType.equals(AppConfing.REST_GET_ANS_OPTION) ||
                        restrictionType.equals(AppConfing.REST_GET_ANS_OPTION_LOOPING) ||
                        restrictionType.equals(AppConfing.REST_GET_ANS_SUM_LOOPING)) {
                    allOptions = QuestionsUtils.getAnswerListFormRestriction(allOptions, restrictionsBean, questionBean, answerBeanHelperList);
                    break;
                }

            }
        }

        /////////////////////////////////to show option which are  in parent
        if (questionBean.getInput_type().equals(AppConfing.QUS_MULTI_SELECT)
                || questionBean.getInput_type().equals(AppConfing.QUS_DROPDOWN)
                || questionBean.getInput_type().equals(AppConfing.QUS_LOOPING)
                || questionBean.getInput_type().equals(AppConfing.QUS_LOOPING_MILTISELECT)
        ) {
            filteredList.addAll(allOptions);

        } else /*if (questionBean.getInput_type().equals(AppConfing.QUS_DROPDOWN_HIDE)
                || questionBean.getInput_type().equals(AppConfing.QUS_MULTI_SELECT_HIDE))*/ {

///////////////////////////////////to hide option in parent
            ////nested parent
            List<Answers> grandParentAns = new ArrayList<>();
            QuestionBean parentQuestion;
            for (ParentBean parentBean : questionBean.getParent()) {
                parentQuestion = questionBeenList.get(getParentUniqueId(parentBean));
                if (parentQuestion != null) {
                    String parentQuestionType = parentQuestion.getInput_type();
                    if (parentQuestionType.equals(AppConfing.QUS_DROPDOWN) ||
                            parentQuestionType.equals(AppConfing.QUS_MULTI_SELECT) ||
                            parentQuestionType.equals(AppConfing.QUS_MULTI_SELECT_LIMITED) ||
                            parentQuestionType.equals(AppConfing.QUS_DROPDOWN_HIDE) ||
                            parentQuestionType.equals(AppConfing.QUS_MULTI_SELECT_HIDE)) {
                        grandParentAns.addAll(getGrandParentAns(parentQuestion, questionBeenList, answerBeanHelperList));
                        break;
                    }
                }
            }

            filteredList = getSkipAnswerFormParentAns(allOptions, grandParentAns);

        }


        if (isDefaultOptionWhen) {
            filteredList.addAll(questionBean.getAnswer_options());
        } else if (isDefaultOptionWhen0 && filteredList.isEmpty()) {
            filteredList.addAll(questionBean.getAnswer_options());
        }
        return filteredList;
    }

    private static List<AnswerOptionsBean> prepareDistrictList(List<MasterDistrictBean> districtBeanRealmResults, String userLanguage) {
        List<AnswerOptionsBean> districtBeanList = new ArrayList<AnswerOptionsBean>();

        for (MasterDistrictBean districtBean : districtBeanRealmResults) {
            AnswerOptionsBean districtOption = new AnswerOptionsBean();
            if (districtBean != null) {
                districtOption.set_id(districtBean.getId());
                districtBeanList.add(districtOption);
                districtOption.setName(userLanguage.equals("en") ? districtBean.getName() : districtBean.getRegionalName());
            }
        }

        return districtBeanList;

    }

    private static List<AnswerOptionsBean> prepareVillageList(List  <MasterVillageBean> villageListByBlockId, String userLanguage) {
        List<AnswerOptionsBean> villageBeanList = new ArrayList<>();

        for (MasterVillageBean villageBean : villageListByBlockId) {
            AnswerOptionsBean villageOption = new AnswerOptionsBean();
            if (villageBean != null) {
                villageOption.setName(userLanguage.equals("en") ? villageBean.getName() : villageBean.getRegionalName());
                villageOption.set_id(villageBean.getId());
                villageBeanList.add(villageOption);
            }
        }


        return villageBeanList;
    }

    private static List<AnswerOptionsBean> prepareBlockList(List<MasterBlockBean> blockListByDistrictId, String userLanguage) {
        List<AnswerOptionsBean> blockBeanList = new ArrayList<>();

        for (MasterBlockBean blockBean : blockListByDistrictId) {
            AnswerOptionsBean blockOption = new AnswerOptionsBean();
            if (blockBean != null) {
                blockOption.set_id(blockBean.getId());
                blockOption.setName(userLanguage.equals("en") ? blockBean.getName() : blockBean.getRegionalName());
                blockBeanList.add(blockOption);
            }
        }
        return blockBeanList;
    }


    private static List<Answers> getGrandParentAns(QuestionBean questionBean, LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        List<Answers> finalAns = new ArrayList<>();
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(getQuestionUniqueId(questionBean));

        while (questionBean != null && questionBeanFilled != null) {
            List<Answers> p_ans = questionBeanFilled.getAnswer();
            finalAns.addAll(p_ans);
            QuestionBean nextQuestion = null;
            if (questionBean.getInput_type().equals(AppConfing.QUS_DROPDOWN_HIDE) ||
                    questionBean.getInput_type().equals(AppConfing.QUS_MULTI_SELECT_HIDE)) {

                for (ParentBean parentBean : questionBean.getParent()) {
                    QuestionBean tempQuestionBean = questionBeenList.get(getParentUniqueId(parentBean));
                    if (tempQuestionBean != null) {
                        String questionType = tempQuestionBean.getInput_type();
                        if (questionType.equals(AppConfing.QUS_DROPDOWN) ||
                                questionType.equals(AppConfing.QUS_MULTI_SELECT) ||
                                questionType.equals(AppConfing.QUS_MULTI_SELECT_LIMITED) ||
                                questionType.equals(AppConfing.QUS_DROPDOWN_HIDE) ||
                                questionType.equals(AppConfing.QUS_MULTI_SELECT_HIDE)) {
                            nextQuestion = tempQuestionBean;
                            break;
                        }

                    }
                }
            }
            questionBean = nextQuestion;
        }

        return finalAns;
    }

    private static List<AnswerOptionsBean> getSkipAnswerFormParentAns
            (List<AnswerOptionsBean> allOptions, List<Answers> parentsAnswer) {

        List<AnswerOptionsBean> avlList = new ArrayList<>();

        if (parentsAnswer.size() == 0) {
            return allOptions;
        }

        for (AnswerOptionsBean aob : allOptions) {
            if (parentsAnswer.size() > 0) {
                boolean isInList = false;
                for (Answers parentAns : parentsAnswer) {
                    if (parentAns.getValue().equals(aob.get_id())) {
                        isInList = true;
                        break;
                    }
                }
                if (!isInList) {
                    avlList.add(aob);
                }

            } else
                avlList.add(aob);
        }
        return avlList;
    }


    public static List<Answers> getNewAnswerList() {
        List<Answers> answerBeanHelperList = new ArrayList<>();
        Answers answers = new Answers();
        answers.setLabel("");
        answers.setReference("");
        answers.setValue("");
        answers.setTextValue("");
        answerBeanHelperList.add(answers);
        return answerBeanHelperList;
    }

    public static String getAnswerUniqueId(QuestionBeanFilled questionBeanFilled) {
        return questionBeanFilled.getOrder();
    }

    public static String getQuestionUniqueId(QuestionBean questionBean) {
        return questionBean.getOrder();
    }

    public static String getParentUniqueId(ParentBean parentBean) {
        return parentBean.getOrder();
    }

    public static String getRestrictionOrderUniqueId(OrdersBean ordersBean) {
        return ordersBean.getOrder();
    }

    public static String getChildUniqueId(ChildBean childBean) {
        return childBean.getOrder();
    }


    public static void sortAnsList(List<QuestionBeanFilled> questionBeanFilledList) {
        Collections.sort(questionBeanFilledList, (o1, o2) -> {
            try {
                int currentOrder = Integer.parseInt(o1.getViewSequence());
                int secondOrder = Integer.parseInt(o2.getViewSequence());
                return currentOrder - secondOrder;

            } catch (Exception e) {
                int currentOrder = Integer.parseInt(o1.getOrder());
                int secondOrder = Integer.parseInt(o2.getOrder());
                return currentOrder - secondOrder;
            }

        });
    }

    public static void sortQusList(List<QuestionBean> questionBeanFilledList) {
        Collections.sort(questionBeanFilledList, (o1, o2) -> {
            try {
                int currentOrder = Integer.parseInt(o1.getViewSequence());
                int secondOrder = Integer.parseInt(o2.getViewSequence());
                return currentOrder - secondOrder;

            } catch (Exception e) {
                int currentOrder = Integer.parseInt(o1.getOrder());
                int secondOrder = Integer.parseInt(o2.getOrder());
                return currentOrder - secondOrder;
            }

        });
    }

    public static void sortQusChildList(List<QuestionBean> questionBeanFilledList) {
        Collections.sort(questionBeanFilledList, (o1, o2) -> {
            try {
                int currentOrder = Integer.parseInt(o1.getViewSequence());
                int secondOrder = Integer.parseInt(o2.getViewSequence());
                return currentOrder - secondOrder;

            } catch (Exception e) {
                int currentOrder = Integer.parseInt(o1.getOrder());
                int secondOrder = Integer.parseInt(o2.getOrder());
                return currentOrder - secondOrder;
            }

        });
    }

    public static boolean isLoopingType(QuestionBeanFilled questionBeanFilled) {
        if (questionBeanFilled.getInput_type().equals(AppConfing.QUS_LOOPING_MILTISELECT)
                || questionBeanFilled.getInput_type().equals(AppConfing.QUS_LOOPING)) {
            return true;
        }
        return false;
    }

    public static boolean isMediaUploadTypeQuestion(String type) {
        return type.equals(AppConfing.QUS_IMAGE) ||
                type.equals(AppConfing.QUS_RECORD_AUDIO);
    }


    public static String getUpdatedChildOrder(String forParentValue, String questionQrder) {
        return forParentValue + "_" + questionQrder;
    }

    public static String getAnswer(Answers answers, String inputType) {
        if (inputType != null && !inputType.equals("")) {
            if (inputType.equals(AppConfing.QUS_TEXT) || inputType.equals(AppConfing.QUS_ADDRESS)) {
                return answers.getTextValue();
            } else {
                return answers.getValue();
            }
        } else {
            return answers.getValue();
        }

    }

    public static String getViewableStringFormAns(QuestionBeanFilled questionBeanFilled) {
        StringBuilder viewText = new StringBuilder();
        if (questionBeanFilled != null && !questionBeanFilled.getAnswer().isEmpty()) {
            switch (questionBeanFilled.getInput_type()) {
                case AppConfing.QUS_MULTI_SELECT:
                case AppConfing.QUS_MULTI_SELECT_HIDE:
                case AppConfing.QUS_LOOPING_MILTISELECT:
                    String tempFix = "";
                    for (Answers answers : questionBeanFilled.getAnswer()) {
                        viewText.append(tempFix).append(answers.getLabel());
                        tempFix = ",";
                    }
                    break;

                case AppConfing.QUS_DROPDOWN:
                case AppConfing.QUS_DROPDOWN_HIDE:
                case AppConfing.QUS_LOOPING:
                case AppConfing.QUS_RADIO_BUTTONS:
                    if (questionBeanFilled.getAnswer().get(0) != null) {
                        viewText = new StringBuilder(questionBeanFilled.getAnswer().get(0).getLabel());
                    }
                    break;

                case AppConfing.QUS_TEXT:
                case AppConfing.QUS_ADDRESS:

                    if (questionBeanFilled.getAnswer().get(0) != null) {
                        viewText = new StringBuilder(questionBeanFilled.getAnswer().get(0).getTextValue());
                    }
                    break;

                default:
                    if (questionBeanFilled.getAnswer().get(0) != null) {
                        viewText = new StringBuilder(questionBeanFilled.getAnswer().get(0).getValue());
                    }
                    break;

            }
        }
        return viewText.toString();
    }
}

package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;
import java.util.List;

import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SYNCED_BUT_EDITABLE;

public class SingleSelect extends BaseSelectType {
    private boolean iniitList = true;

    public SingleSelect(View view, final QuestionBean questionBean, int formStatus,
                        QuestionHelperCallback.DataListener dataListener,
                        LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList, int formId) {
        super(view, questionBean, formStatus, dataListener,
                questionBeenList, answerBeanHelperList);
        this.formId = formId;
        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == AppConfing.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            setData();
        }


        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }
        if (iniitList&&isClickable)
            initListener();


    }


    void setData() {
        //draft action
        if (answerBeanFilled != null) {

            //setAnswer
            final List<Answers> ans = answerBeanFilled.getAnswer();
            if (!ans.isEmpty()) {
                String optionId = null;
                String text = "";
                for (AnswerOptionsBean answerOptionsBean : questionBean.getAnswer_options()) {
                    if (answerOptionsBean.get_id().equals(ans.get(0).getValue())) {
                        text = answerOptionsBean.getName();
                        optionId = answerOptionsBean.get_id();
                        break;
                    }
                }
                if (optionId == null) {
                    text = ans.get(0).getLabel();
                }
                if (text != null && !text.trim().equals("")) {
                    dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
                }
                dynamicEditTextRow.setText(text);

                for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                    if (restrictionsBean.getType().equals(AppConfing.REST_VALUE_AS_TITLE_OF_CHILD)) {
                        changeTitleRequest(restrictionsBean, text);
                    }
                }
                for (ValidationBean validationBean : questionBean.getValidation()) {
                    String validationType = validationBean.get_id();
                    if (validationType.equals(AppConfing.VAL_VILLAGE_WISE_LIMIT)) {
                        resetAnswerDataCheck(questionBean, optionId, validationType);
                    }

                    //todo master cens here
                    /*else if (validationBean.get_id().equals(AppConfing.VAL_MASTER_DISTRICT)) {
                        Realm realm = Realm.getDefaultInstance();
                        MasterDistrictBean singleResult = realm.where(MasterDistrictBean.class).equalTo("id", ans.get(0).getValue()).findFirst();
                        if (singleResult != null) {
                            dynamicEditTextRow.setText(dataListener.getUserLanguage().equals("en") ? singleResult.getName() : singleResult.getRegionalName());
                        }
                    } else if (validationBean.get_id().equals(AppConfing.VAL_MASTER_BLOCK)) {

                        Realm realm = Realm.getDefaultInstance();
                        MasterBlockBean singleResult = realm.where(MasterBlockBean.class).equalTo("id", ans.get(0).getValue()).findFirst();
                        if (singleResult != null) {
                            dynamicEditTextRow.setText(dataListener.getUserLanguage().equals("en") ? singleResult.getName() : singleResult.getRegionalName());
                        }

                    } else if ((validationBean.get_id().equals(AppConfing.VAL_MASTER_VILLAGE))) {
                        Realm realm = Realm.getDefaultInstance();
                        MasterVillageBean singleResult = realm.where(MasterVillageBean.class).equalTo("id", ans.get(0).getValue()).findFirst();
                        if (singleResult != null) {
                            dynamicEditTextRow.setText(dataListener.getUserLanguage().equals("en") ? singleResult.getName() : singleResult.getRegionalName());
                        }
                    }*/
                }
            }
        }

    }

    private void resetAnswerDataCheck(QuestionBean questionBean, String ansOptionId, String validationType) {
     /*   if (AppConfing.VAL_VILLAGE_WISE_LIMIT.equals(validationType)) {
            if (formStatus == DRAFT) {
                Realm realm = Realm.getDefaultInstance();
                VillageWiseFormCount villageWiseFormCount = realm.where(VillageWiseFormCount.class)
                        .equalTo("formId", formId).findFirst();
                if (villageWiseFormCount != null) {
                    for (VillageWiseBean villageWiseBean : villageWiseFormCount.getVillageWise()) {
                        if (villageWiseBean.get_id().equals(ansOptionId)) {
                            long submmitedCount = realm.where(FilledForms.class)
                                    .equalTo("formId", String.valueOf(formId))
                                    .equalTo("upload_status", SUBMITTED)
                                    .equalTo("question.order", QuestionsUtils.getQuestionUniqueId(questionBean))
                                    .equalTo("question.answer.value", ansOptionId).count();
                            int syncCount = villageWiseBean.getCount();
                            int total = syncCount + (int) submmitedCount;
                            if (total >= villageWiseBean.getLimit()) {
                                dynamicEditTextRow.setInvalidate();
                                dataListener.clearQuestion(questionBean);
                            }
                        }
                    }
                }
                realm.close();
            }
        }*/
    }

    private void initListener() {

        dynamicEditTextRow.setOnCustomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                preventDoubleClicks(view);
                List<AnswerOptionsBean> filteredList;
                filteredList = QuestionsUtils.Companion.getAnsOptionFromQuestionAfterFilter(questionBean, questionBeenList,
                        answerBeanHelperList, dataListener.getUserLanguage(), formId);
                //filteredList = getAnsOptionFromQuestionAfterFilter(questionBean);
                createSelector(filteredList, questionBean,
                        questionBean.getTitle());
            }
        });


    }


}

package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;
import java.util.List;


import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SYNCED_BUT_EDITABLE;


public class LoopingSingleSelect extends BaseEditTextWithButtonType {

    public LoopingSingleSelect(View view, final QuestionBean questionBean, int formStatus, QuestionHelperCallback.DataListener dataListener,
                               LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList
            , final QuestionHelperCallback.QuestionButtonClickListener buttonClickListener, int form_id) {

        super(dataListener, view, questionBeenList, answerBeanHelperList, buttonClickListener, questionBean, formStatus, form_id);

        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == AppConfing.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            setData();
        }
        boolean iniitList = true;
        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }

        if (iniitList)
            initListener();

    }

    private void initListener() {
        dynamicLoopingView.setOnCustomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                preventDoubleClicks(view);

                List<AnswerOptionsBean> filteredList = QuestionsUtils.Companion.getAnsOptionFromQuestionAfterFilter(questionBean, questionBeenList,
                        answerBeanHelperList, dataListener.getUserLanguage(), formId);

                createSelector(filteredList, questionBean,
                        questionBean.getTitle());
            }
        });

    }

    private void setData() {
        if (answerBeanFilled != null) {
            //setAnswer
            final List<Answers> ans = answerBeanFilled.getAnswer();
            if (ans.size() > 0) {
                String text = "";
                if (!ans.get(0).getValue().equals("")) {
                    for (int i = 0; i < questionBean.getAnswer_options().size(); i++) {
                        if (questionBean.getAnswer_options().get(i).get_id().equals(ans.get(0).getValue())) {
                            text = questionBean.getAnswer_options().get(i).getName();
                            dynamicLoopingView.setText(text);
                            if (answerBeanFilled.isFilled()) {
                                dynamicLoopingView.changebuttonStatus(true, 1);
                            } else {
                                dynamicLoopingView.changebuttonStatus(false, 3);
                            }
                            break;
                        }
                    }
                    if (questionBean.getAnswer_options().size() == 0 && !ans.get(0).getValue().equals("")) {
                        if (answerBeanFilled.isFilled()) {
                            dynamicLoopingView.changebuttonStatus(true, 1);
                        } else {
                            dynamicLoopingView.changebuttonStatus(false, 3);
                        }
                    }


                } else {
                    dynamicLoopingView.changebuttonStatus(false, 0);
                }


                if (questionBean.getRestrictions().size() > 0) {
                    for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                        if (restrictionsBean.getType().equals(AppConfing.REST_VALUE_AS_TITLE_OF_CHILD)) {
                            changeTitleRequest(restrictionsBean, text);
                        }
                    }
                }


            }

        }


    }


}

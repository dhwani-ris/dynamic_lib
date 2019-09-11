package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SYNCED_BUT_EDITABLE;

public class LoopingMultiSelect extends BaseEditTextWithButtonType {

    public LoopingMultiSelect(View view, final QuestionBean questionBean, int formStatus, final QuestionHelperCallback.DataListener dataListener,
                              LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList, final QuestionHelperCallback.QuestionButtonClickListener buttonClickListener, int form_id) {
        super(dataListener, view, questionBeenList, answerBeanHelperList, buttonClickListener, questionBean, formStatus, form_id);

        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
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
                List<String> strings = new ArrayList<>();
                answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (answerBeanFilled != null) {
                    final List<Answers> ans2 = answerBeanFilled.getAnswer();
                    for (Answers answers : ans2) {
                        if (!answers.getValue().isEmpty()) {
                            strings.add(answers.getValue());
                        }
                    }
                }
                List<AnswerOptionsBean> filteredList = QuestionsUtils.Companion.getAnsOptionFromQuestionAfterFilter(questionBean, questionBeenList,
                        answerBeanHelperList, dataListener.getUserLanguage(), formId);

                int checkLimit = 100;
//limit the check with validations
                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_CHECKLIMIT)) {
                        String stringLimit = validationBean.getError_msg();
                        if (stringLimit != null && !stringLimit.equals(""))
                            checkLimit = Integer.parseInt(stringLimit);
                        break;
                    }
                }
                createMultiSelector(filteredList, strings, questionBean, questionBean.getTitle(), checkLimit);

            }
        });

    }

    private void setData() {
        if (answerBeanFilled != null) {
            //setAnswer
            final List<Answers> ans = answerBeanFilled.getAnswer();
            String viewableStringFormAns = QuestionsUtils.Companion.getViewableStringFormAns(answerBeanFilled, questionBean);

            if (QuestionsUtils.Companion.isItHasAns(ans)) {
                if (answerBeanFilled.isFilled()) {
                    dynamicLoopingView.changebuttonStatus(true, 1);

                    /// handel the default answerOption
                    if (questionBean.getAnswer_options().size() > 0 && ans.size() == 1) {
                        if (ans.get(0).getValue().equals(questionBean.getAnswer_option().get(0).get_id())) {
                            for (ValidationBean validationBean : questionBean.getValidation()) {
                                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0)) {
                                    dynamicLoopingView.changebuttonStatus(false, 0);
                                    break;
                                }
                            }
                        }
                    }


                } else {
                    dynamicLoopingView.changebuttonStatus(false, 3);
                }

                if (questionBean.getRestrictions().size() > 0) {
                    for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                        if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                            changeTitleRequest(restrictionsBean, viewableStringFormAns);
                        }
                    }
                }
                dynamicLoopingView.setText(viewableStringFormAns);

            } else {
                dynamicLoopingView.changebuttonStatus(false, 0);
            }

        }


    }


}

package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
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
import java.util.Locale;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SYNCED_BUT_EDITABLE;

public class MultiSelect extends BaseSelectType {

    boolean iniitList = true;

    public MultiSelect(View view, final QuestionBean questionBean, int formStatus,
                       QuestionHelperCallback.DataListener dataListener,
                       LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList, int formId) {
        super(view, questionBean, formStatus, dataListener,
                questionBeenList, answerBeanHelperList);
        dynamicEditTextRow.setMaxLines(10);
        this.formId = formId;
        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            setData();
        }
        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }

        if (iniitList && isClickable)
            initListener();


    }


    private void setData() {
        //draft action
        if (answerBeanFilled != null) {
            //setAnswer
            final List<Answers> ans = answerBeanFilled.getAnswer();
            if (!ans.isEmpty()) {
                String text = "";
                if (questionBean.getAnswer_options().size() >= 1) {
                    for (int i = 0; i < questionBean.getAnswer_options().size(); i++) {
                        for (int j = 0; j < ans.size(); j++) {

                            if (questionBean.getAnswer_options().get(i).get_id()
                                    .equals(ans.get(j).getValue())) {
                                if ((j + 1) == ans.size()) {
                                    text = String.format(Locale.getDefault(),
                                            "%s%s", text, questionBean.getAnswer_options()
                                                    .get(i).getName());
                                } else {
                                    text = String.format(Locale.getDefault(),
                                            "%s%s, ", text, questionBean
                                                    .getAnswer_options().get(i).getName());
                                }

                            }
                        }
                    }
                } else {
                    String prifix = "";
                    for (Answers answers : ans) {
                        text = String.format(Locale.getDefault(),
                                "%s%s%s ", prifix, text, answers.getLabel());
                        prifix = ", ";
                    }
                }
                if (!text.trim().equals("")) {
                    dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
                }

                if (questionBean.getRestrictions().size() > 0) {
                    for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                        if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                            changeTitleRequest(restrictionsBean, text);
                        }
                    }
                }


                dynamicEditTextRow.setText(text);
            }

        }
    }


    private void initListener() {

        dynamicEditTextRow.setOnCustomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //multiSelector dialloge
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
                List<AnswerOptionsBean> filteredList;
                filteredList = QuestionsUtils.Companion.getAnsOptionFromQuestionAfterFilter(questionBean, questionBeenList,
                        answerBeanHelperList, dataListener.getUserLanguage(), formId);

                //    filteredList = getAnsOptionFromQuestionAfterFilter(questionBean);
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


}

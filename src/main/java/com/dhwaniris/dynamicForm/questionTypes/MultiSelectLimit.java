package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.base.BaseActivity;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ParentBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SYNCED_BUT_EDITABLE;

public class MultiSelectLimit extends BaseSelectType {


    public MultiSelectLimit(View view, final QuestionBean questionBean, int formStatus,
                            QuestionHelperCallback.DataListener dataListener,
                            LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList
            , int formId) {
        super(view, questionBean, formStatus, dataListener,
                questionBeenList, answerBeanHelperList);

        this.formId = formId;

        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            setData();
        }

        boolean iniitList = true;
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
            if (ans.isEmpty()) {
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
                List<String> strings = new ArrayList<>();
                int checklimit = 100;
                answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (answerBeanFilled != null) {
                    final List<Answers> ans2 = answerBeanFilled.getAnswer();

                    if (ans2.size() > 0) {
                        for (int i = 0; i < ans2.size(); i++) {
                            if (ans2.get(i).getValue() != null && !ans2.get(i)
                                    .getValue().equals("")) {
                                strings.add(ans2.get(i).getValue());
                            }
                        }
                    }
                    final List<ParentBean> perent = questionBean.getParent();
                    List<Answers> p_ans = new ArrayList<>();
                    if (perent.size() > 0 && perent.get(0) != null) {
                        QuestionBeanFilled perentAnswer = answerBeanHelperList.get(QuestionsUtils.Companion.getParentUniqueId(perent.get(0)));
                        if (perentAnswer != null)
                            p_ans = perentAnswer.getAnswer();
                    }

                    String ans = "";
                    if (p_ans.size() > 0) {
                        if (perent.get(0).getType().equals(LibDynamicAppConfig.QUS_DROPDOWN)) {
                            ans = p_ans.get(0).getLabel();
                        } else {
                            ans = p_ans.get(0).getValue();
                        }

                        if (Pattern.matches("^[0-9]*$", ans.equals("") ? "xsd" : ans)) {
                            checklimit = Integer.parseInt(ans);
                        } else {
                            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, String.format(Locale.ENGLISH,
                                    "Wrong perent options. child Question ID :%s . Line no. 497",
                                    questionBean.get_id()), LibDynamicAppConfig.UNEXPECTED_ERROR,
                                    "BaseFormActivity");
                        }
                    }
                }


                List<AnswerOptionsBean> allOpt = questionBean.getAnswer_options();

                createMultiSelector(allOpt, strings, questionBean, questionBean.getTitle(), checklimit);
            }

        });

    }


}

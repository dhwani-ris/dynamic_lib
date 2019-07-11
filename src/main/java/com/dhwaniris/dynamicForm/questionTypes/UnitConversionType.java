package com.dhwaniris.dynamicForm.questionTypes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.customViews.BaseXMLView;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.customViews.UnitConversionViewXML;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;
import com.dhwaniris.dynamicForm.utils.UnitConversionHelper;

import java.util.LinkedHashMap;
import java.util.List;



import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;

/**
 * Created by ${Sahjad} on 5/14/2019.
 */
public class UnitConversionType extends BaseType {

    private UnitConversionViewXML unitView;
    QuestionHelperCallback.DataListener dataListener;
    QuestionHelperCallback.UnitConversionListener listener;
    UnitConversionHelper unitConversionHelper;

    public UnitConversionType(View view, int formStatus, QuestionBean questionBean,
                              LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled>
                                      answerBeanHelperList, QuestionHelperCallback.UnitConversionListener listener, QuestionHelperCallback.DataListener dataListener) {


        unitView = new UnitConversionViewXML(view);
        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
        this.questionBean = questionBean;
        this.formStatus = formStatus;
        this.dataListener = dataListener;
        this.listener = listener;
        unitConversionHelper = new UnitConversionHelper();
        setBasicFunctionality(questionBean, formStatus);
        boolean iniitList = true;
        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }

        if (iniitList) {
            initListener();
        }
        answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));

        if (formStatus == DRAFT ||
                formStatus == SUBMITTED ||
                formStatus == AppConfing.SYNCED_BUT_EDITABLE ||
                formStatus == AppConfing.EDITABLE_SUBMITTED ||
                formStatus == EDITABLE_DARFT) {
            setBasic();
        }

    }

    private void setBasic() {
        if (formStatus == SUBMITTED || formStatus == AppConfing.EDITABLE_SUBMITTED) {
            unitView.setEditable(false);
            unitView.setAnswerStatus(EditTextRowView.ANSWERED);
        } else if (formStatus == AppConfing.SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) {
            unitView.setEditable(questionBean.isEditable());
            if (!questionBean.isEditable()) {
                unitView.setFocusableForEditText(1);
            }
        }
        setData(answerBeanFilled);

    }

    void setData(QuestionBeanFilled answerBeanFilled) {
        if (answerBeanFilled != null) {
            final List<Answers> answers = answerBeanFilled.getAnswer();
            if (!answers.isEmpty() && !answers.get(0).getValue().isEmpty()) {
                Answers answers1 = answers.get(0);
                int pos = answers1.getLabel().equals("") ? 0 : Integer.parseInt(answers1.getLabel());
                unitView.setAns(answers1.getTextValue(), pos);

                if (answerBeanFilled.isValidAns()) {
                    unitView.setAnswerStatus(BaseXMLView.ANSWERED);
                }

            } else {
                unitView.reset();
            }

        }
    }

    private void setBasicFunctionality(QuestionBean questionBean, int formStatus) {

        unitView.setTitle(questionBean.getTitle());
        unitView.setOrder(questionBean.getLabel());
        if (questionBean.getInformation() != null && !questionBean.getInformation().trim().equals("")) {
            unitView.setInformation(questionBean.getInformation());
        }
        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                unitView.setVisibility(View.GONE);
            }
            if (dataListener != null)
                dataListener.createNewAnswerObject(questionBean, unitView.getVisivility() == View.VISIBLE);
        } else {
            if (checkValueForVisibility(questionBean)) {
                unitView.setVisibility(View.VISIBLE);
            } else {
                unitView.setVisibility(View.GONE);
            }
        }

        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                switch (validationBean.get_id()) {
                    case AppConfing.VAL_REQUIRED:
                        unitView.isRequired(true);
                        break;
                    case AppConfing.VAL_ADD_INFO_GPS:
                    case AppConfing.VAL_ADD_INFO_IMAGE:
                        unitView.setAdditionalInfoClick(v -> {
                            dataListener.clickOnAdditionalButton(questionBean);
                        });
                        break;
                    case AppConfing.VAL_UNIT_LENGTH:
                        unitView.setSpinnerValues(R.array.unit_length);
                        break;
                    case AppConfing.VAL_UNIT_AREA:
                        unitView.setSpinnerValues(R.array.unit_area);
                        break;
                    case AppConfing.VAL_UNIT_TEMPERATURE:
                        unitView.setSpinnerValues(R.array.unit_temperature);
                        break;
                    case AppConfing.VAL_UNIT_TIME:
                        unitView.setSpinnerValues(R.array.unit_time);
                        break;
                    case AppConfing.VAL_UNIT_MASS:
                        unitView.setSpinnerValues(R.array.unit_mass);
                        break;
                    case AppConfing.VAL_UNIT_VOLUME:
                        unitView.setSpinnerValues(R.array.unit_volume);
                        break;
                    case AppConfing.VAL_UNIT_SPEED:
                        unitView.setSpinnerValues(R.array.unit_speed);

                        break;

                }


            }
        }


    }

    private void initListener() {

        unitView.setTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onEvent();
            }
        });

        unitView.setUnitButtonClickListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onEvent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onEvent() {
        String unit = String.valueOf(unitView.getSelectedUnit());
        String value = unitView.getText();
        double convertedValue = 0.0;
        boolean isFound = false;
        for (ValidationBean validationBean : questionBean.getValidation()) {
            switch (validationBean.get_id()) {
                case AppConfing.VAL_UNIT_LENGTH:
                    convertedValue = unitConversionHelper.convertLengthIntoMetre(unitView.getValue(), Integer.parseInt(unit));
                    isFound = true;
                    break;
                case AppConfing.VAL_UNIT_AREA:
                    convertedValue = unitConversionHelper.convertAreaIntoSqMetre(unitView.getValue(), Integer.parseInt(unit));
                    isFound = true;
                    break;
                case AppConfing.VAL_UNIT_TEMPERATURE:
                    convertedValue = unitConversionHelper.convertTemperatureIntoCentigrade(unitView.getValue(), Integer.parseInt(unit));
                    isFound = true;
                    break;
                case AppConfing.VAL_UNIT_TIME:
                    convertedValue = unitConversionHelper.convertTimeIntoSecond(unitView.getValue(), Integer.parseInt(unit));
                    isFound = true;
                    break;
                case AppConfing.VAL_UNIT_MASS:
                    convertedValue = unitConversionHelper.convertMassIntoKoloGram(unitView.getValue(), Integer.parseInt(unit));
                    isFound = true;
                    break;
                case AppConfing.VAL_UNIT_VOLUME:
                    convertedValue = unitConversionHelper.convertMassIntoKoloGram(unitView.getValue(), Integer.parseInt(unit));
                    isFound = true;
                    break;
                case AppConfing.VAL_UNIT_SPEED:
                    convertedValue = unitConversionHelper.convertSpeedIntokmhr(unitView.getValue(), Integer.parseInt(unit));

                    isFound = true;
                    break;

            }

            if (isFound)
                break;
        }
        String standardValue = String.format("%.4f", convertedValue);
        listener.saveValue(value, standardValue, unit, questionBean);
    }


    @Override
    public void superChangeStatus(int status) {
        unitView.setAnswerStatus(status);
    }

    @Override
    public void superResetQuestion() {
        unitView.reset();
    }

    @Override
    public void superChangeTitle(String title) {
        unitView.setTitle(title);
    }

    @Override
    public void superSetErrorMsg(String errorMsg) {
        unitView.setErrorMsg(errorMsg);
    }

    @Override
    public void setAdditionalVisibility(int visibility) {
        unitView.setAdditionalInfoVisibility(visibility);
    }

    @Override
    public void setAdditionalButtonStatus(int status) {
        unitView.setAdditionalStatus(status);
    }

    @Override
    public void superSetEditable(boolean isEditable, String questionType) {
        unitView.setEditable(isEditable);
    }

    @Override
    public void superSetAnswer(QuestionBeanFilled questionBeanFilled) {
        setData(questionBeanFilled);
    }
}

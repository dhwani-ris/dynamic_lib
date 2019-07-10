package com.dhwaniris.dynamicForm.customViews;


import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.questionTypes.RadioButtonType;

import java.util.ArrayList;
import java.util.List;



public class RadioRowViewXML extends BaseXMLView{

    private List<RadioButton> buttonsList = new ArrayList<>();
    private RadioGroup radioGroup;
    public RadioRowViewXML(View view) {
        orderTextView = view.findViewById(R.id.txt_order);
        titleText = view.findViewById(R.id.titletext);
        star = view.findViewById(R.id.star);
        radioGroup = view.findViewById(R.id.radio_group);
        information = view.findViewById(R.id.info);
        error_msg = view.findViewById(R.id.error_msg);
        additionalInfo = view.findViewById(R.id.additionalInfo);
        this.view = view;
        float density = view.getResources().getDisplayMetrics().density;

    }


    //change focus on answerEditText
    public void setFocusable(boolean focusable) {
        for (RadioButton radioButton : buttonsList) {
            radioButton.setClickable(focusable);
            radioButton.setEnabled(focusable);
        }

    }

    //change focus on answerEditText
    public void setFocusable(int focusable) {
        if (focusable != FOCUSABLE) {
            radioGroup.setFocusable(false);
        }

    }
    RadioButtonType.RadioButtonInnerListener radioButtonListener;

    //setClickListener
    public void setRadioButtonsCheckChangeListener(RadioButtonType.RadioButtonInnerListener listener) {
        radioButtonListener = listener;
    }




    public void setRadioButtonsTextItems(List<AnswerOptionsBean> answerOptionsBeans) {
        int numAnsOption;
        numAnsOption = answerOptionsBeans.size();
        if (numAnsOption < 15) {

            for (AnswerOptionsBean answerOptionsBean : answerOptionsBeans) {
                RadioButton radioButton = new RadioButton(view.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                /*    layoutParams.setMargins(20, radioButton.getResources().getDimensionPixelSize(R.dimen.mat_padding), 0, 0);
                 */
                radioButton.setLayoutParams(layoutParams);
                radioButton.setText(answerOptionsBean.getName());
                radioButton.setTag(answerOptionsBean.get_id());
                radioButton.setBackgroundColor(radioButton.getResources().getColor(R.color.bg_grey));
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b && radioButtonListener != null) {
                            radioButtonListener.onRadioButtonCheck(compoundButton.getTag().toString(), compoundButton.getText().toString());
                        }
                    }
                });
                buttonsList.add(radioButton);
                radioGroup.addView(radioButton);
            }
        }
    }

    public void checkButtonByIdOrName(String id, String name) {
        if (id != null) {
            for (RadioButton radioButton : buttonsList) {
                if (radioButton.getTag().toString().equals(id)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        } else if (name != null) {
            for (RadioButton radioButton : buttonsList) {
                if (radioButton.getText().toString().equals(name)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }

    }

    public void reset() {
        radioGroup.clearCheck();
        setAnswerStatus(RadioRowViewXML.NONE);
    }

}

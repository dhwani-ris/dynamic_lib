package com.dhwaniris.dynamicForm.customViews;


import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.utils.DecimalDigitsInputFilter;

public class UnitConversionViewXML extends BaseXMLView {

    private TextView tvDuration;
    private Spinner spUnit;
    private Button btnConvert;
    private EditText edtValue;

    public UnitConversionViewXML(View view) {
        orderTextView = view.findViewById(R.id.txt_order);
        titleText = view.findViewById(R.id.titletext);
        star = view.findViewById(R.id.star);
        information = view.findViewById(R.id.info);
        error_msg = view.findViewById(R.id.error_msg);
        additionalInfo = view.findViewById(R.id.additionalInfo);

        btnConvert = view.findViewById(R.id.btn_calculate);
        edtValue = view.findViewById(R.id.edt_value);
        spUnit = view.findViewById(R.id.sp_unit);

        this.view = view;
        float density = view.getResources().getDisplayMetrics().density;

        edtValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15, 2)});

    }

    public void setAns(String value, int position) {

        edtValue.setText(value);
        setAnswerStatus(ANSWERED);
        spUnit.setSelection(position);
    }


    public void setEditable(boolean isEditable) {

        edtValue.setEnabled(isEditable);
        btnConvert.setEnabled(isEditable);
        spUnit.setClickable(isEditable);
        spUnit.setEnabled(isEditable);
        if (isEditable) {
            setFocusableForEditText(0);
        }
    }

    public void setSpinnerValues(int textArrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(spUnit.getContext(), textArrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnit.setAdapter(adapter);
    }

    public void setSelect(int pos) {
        spUnit.setSelection(pos);
    }

    public void setTextChangeListener(TextWatcher textChangeListener) {
        edtValue.addTextChangedListener(textChangeListener);
    }
    public void reset() {
        edtValue.setText("");
        setAnswerStatus(UnitConversionViewXML.NONE);
    }
    public void setConvertButtonClickListener(View.OnClickListener onClickListener) {
        btnConvert.setOnClickListener(onClickListener);
    }

    public void setUnitButtonClickListener(AdapterView.OnItemSelectedListener listener) {
        spUnit.setOnItemSelectedListener(listener);
    }


    public int getSelectedUnit() {
        return spUnit.getSelectedItemPosition();
    }

    public String getText() {
        return edtValue.getText().toString();
    }

    public Double getValue() {
        String value = edtValue.getText().toString();

        if (!value.equals("")) {
            if (value.length() == 1 && value.charAt(0) == '.') {
                return 0.0;
            }
            return Double.parseDouble(value);
        }
        return 0.0;
    }

    public void setFocusableForEditText(int focusable) {
        if (focusable == FOCUSABLE) {
            edtValue.setFocusable(true);
            edtValue.setClickable(true);
            edtValue.setEnabled(true);
        } else {
            edtValue.setFocusable(false);
            edtValue.setClickable(false);
            edtValue.setEnabled(false);
        }

    }
}

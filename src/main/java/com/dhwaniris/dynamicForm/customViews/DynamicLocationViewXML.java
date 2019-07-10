package com.dhwaniris.dynamicForm.customViews;

import android.view.View;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;

import static android.graphics.Color.GRAY;

public class DynamicLocationViewXML extends BaseXMLView {
    private TextView tv_accuracy;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_btn_location;

    public DynamicLocationViewXML(View view) {
        orderTextView = view.findViewById(R.id.txt_order);
        star = view.findViewById(R.id.star);
        titleText = view.findViewById(R.id.titletext);
        information = view.findViewById(R.id.info);
        tv_accuracy = view.findViewById(R.id.tv_accuracy);
        tv_latitude = view.findViewById(R.id.tv_latitude);
        tv_longitude = view.findViewById(R.id.tv_longitude);
        tv_btn_location = view.findViewById(R.id.btnGetLoaction);
        error_msg = view.findViewById(R.id.error_msg);
        additionalInfo = view.findViewById(R.id.additionalInfo);
        this.view = view;
    }


    public void setLocation(String accuracy, String latitude, String longitude) {

        tv_accuracy.setText(accuracy);
        tv_latitude.setText(latitude);
        tv_longitude.setText(longitude);
    }



    public void stopBtn(boolean isValid) {
        tv_btn_location.setClickable(isValid);
        tv_btn_location.setFocusable(isValid);
        tv_btn_location.setBackgroundColor(GRAY);

    }
    //setClickListener
    public void setOnCustomClickListener(View.OnClickListener clickListener) {
        tv_btn_location.setOnClickListener(clickListener);
    }

    public void reset() {
        tv_longitude.setText("");
        tv_accuracy.setText("");
        tv_latitude.setText("");
        setAnswerStatus(RadioRowViewXML.NONE);
    }

}

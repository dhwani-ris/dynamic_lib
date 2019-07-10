package com.dhwaniris.dynamicForm.customViews;

import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;

import static android.view.View.VISIBLE;

/**
 * Created by ${Sahjad} on 6/10/2019.
 */
public class BaseXMLView {

     TextView orderTextView;
     TextView titleText;
     TextView star;
     View view;
     Button additionalInfo;
     ImageView information;
     ImageView error_msg;
    public static final int NONE = 0;
    public static final int ANSWERED = 1;
    public static final int NOT_ANSWERED = 2;
    public static final int FOCUSABLE = 0;

    public View getView() {
        return view;
    }

    public TextView getStar() {
        return star;
    }

    public void setAdditionalInfoVisibility(int visibility) {
        additionalInfo.setVisibility(visibility);
    }

    public void setAdditionalStatus(int status) {
        if (status == ANSWERED) {
            additionalInfo.setBackgroundColor(view.getResources().getColor(R.color.green));
        } else {
            additionalInfo.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
        }
    }

    public void setVisibility(int visibility) {
        view.setVisibility(visibility);
    }

    public int getVisivility() {
        return view.getVisibility();
    }


    public void setTitle(String hint) {
        if (hint == null) {
            hint = "";
        }
        titleText.setText(hint);
        titleText.invalidate();
    }

    public void setOrder(String order) {
        orderTextView.setText(order);
        orderTextView.invalidate();
    }

    public void setAnswerStatus(int val) {
        switch (val) {
            case 1:
                changeOrder(orderTextView, R.drawable.green_circle, R.color.white);
                break;

            case 2:
                changeOrder(orderTextView, R.drawable.red_circle, R.color.white);
                break;

            default:
                changeOrder(orderTextView, R.drawable.white_circle, R.color.black);
                break;
        }
    }

    private void changeOrder(TextView textView, int drawable, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            orderTextView.setBackground(view.getResources().getDrawable(drawable, null));
        } else {
            orderTextView.setBackground(view.getResources().getDrawable(drawable));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextColor(view.getResources().getColor(color, null));
        } else {
            textView.setTextColor(view.getResources().getColor(color));
        }
    }

    //get order from view
    public String getOrder() {
        return orderTextView.getText().toString().trim();
    }

    public void setAdditionalInfoClick(View.OnClickListener clickListener) {
        additionalInfo.setOnClickListener(clickListener);
    }


    //get text from hintTextView
    public String getTitle() {
        return titleText.getText().toString().trim();
    }

    public void isRequired(boolean b) {
        if (b) {
            star.setText("*");
        } else {
            star.setText("");
        }
    }


    //set hint to information
    public void setInformation(final String infoMsg) {
        if (infoMsg != null && !infoMsg.equals("")) {
            // information.setText(hint);
            information.setVisibility(VISIBLE);
            information.setOnClickListener(view -> showInformationDialog(infoMsg));
        }
    }

    private void showInformationDialog(String string) {

        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle(view.getContext().getString(R.string.information));
        alertDialog.setIcon(R.drawable.ic_information);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, view.getContext().getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    public void reset() {
        setAnswerStatus(RadioRowViewXML.NONE);
    }

    public void setErrorMsg(final String infoMsg) {
        if (infoMsg != null && !infoMsg.equals("")) {
            // information.setText(hint);
            error_msg.setVisibility(VISIBLE);
            error_msg.setOnClickListener(view -> showErrorMsgDialog(infoMsg));
        }
    }

    private void showErrorMsgDialog(String string) {

        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle(view.getContext().getString(R.string.error_msg));
        alertDialog.setIcon(R.drawable.ic_error_msg);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, view.getContext().getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}

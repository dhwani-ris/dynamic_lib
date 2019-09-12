package com.dhwaniris.dynamicForm.customViews;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;

import static android.graphics.Color.RED;


public class EditTextRowView extends LinearLayout {

    private TextView orderTextView;
    private TextView hintTextView;
    private TextView star;
    EditText answerEditText;
    ImageView information;
    ImageView errorMsg;
    LayoutParams lp;

    Button gotoExtra;


    public static final int NONE = 0;
    public static final int ANSWERED = 1;
    public static final int NOT_ANSWERED = 2;
    public static final int FOCUSABLE = 0;

    public EditTextRowView(Context context) {
        super(context);
        init(context);
    }

    public EditTextRowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setAttribute(context, attrs);
    }

    public EditTextRowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setAttribute(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditTextRowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        setAttribute(context, attrs);
    }

    //initializing
    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);

        orderTextView = new TextView(context);

        //mase size depending on screen size
        final float scale = getResources().getDisplayMetrics().density;
        int size = (int) (30 * scale + 0.5f);


        orderTextView.setHeight(size);
        orderTextView.setWidth(size);
        orderTextView.setGravity(Gravity.CENTER);

        orderTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (10 * scale + 0.5f));

        lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            orderTextView.setBackground(context.getResources().getDrawable(R.drawable.white_circle, null));
        } else {
            orderTextView.setBackground(context.getResources().getDrawable(R.drawable.white_circle));
        }
        orderTextView.setText("1");


        hintTextView = new TextView(context);

        int pad8 = (int) (8 * scale + 0.5f);
        hintTextView.setPadding(pad8, 0, 0, 0);
        lp.setMargins(4, 1, 4, 4);
        hintTextView.setGravity(Gravity.CENTER_VERTICAL);

        information = new ImageView(context);
        information.setVisibility(GONE);
        information.setImageResource(R.drawable.ic_information);
        information.setPadding(0, 0, 4, 0);

        errorMsg = new ImageView(context);
        errorMsg.setVisibility(GONE);
        errorMsg.setImageResource(R.drawable.ic_error_msg);
        errorMsg.setPadding(0, 0, 4, 0);


        star = new TextView(context);
        star.setTextColor(RED);
        star.setGravity(Gravity.END);

        linearLayout.addView(orderTextView);

        LayoutParams layoutParams1 = new LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT);
        layoutParams1.weight = 1;
        hintTextView.setLayoutParams(layoutParams1);
        linearLayout.addView(hintTextView);
        linearLayout.addView(information);
        linearLayout.addView(errorMsg);

        LayoutParams starParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        linearLayout.addView(star, starParams);

        LayoutParams linearParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setLayoutParams(linearParams);

        addView(linearLayout);

        answerEditText = new EditText(context);
        answerEditText.setScrollContainer(true);
        addView(answerEditText, lp);

        /*answerTextView = new TextView(context);
        answerTextView.setVisibility(GONE);
        lp.setMargins(4, 1, 4, 4);
        addView(answerTextView, lp);*/


        // addView(information, lp);


        LayoutParams paramsButton = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gotoExtra = new Button(getContext());

        gotoExtra.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        gotoExtra.setTextColor(getResources().getColor(R.color.white));
        gotoExtra.setText(R.string.additional_info);
        //   gotoButton.setPadding(1,0,0,);
        gotoExtra.setLayoutParams(paramsButton);
        addView(gotoExtra);
        gotoExtra.setVisibility(GONE);
        linearLayout.invalidate();
    }

    public void setAdditionalInfoVisibility(int visibility) {
        gotoExtra.setVisibility(visibility);
    }

    public void setAdditionalStatus(int status) {
        if (status == ANSWERED) {
            gotoExtra.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            gotoExtra.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public void setAdditionalInfoClick(View.OnClickListener clickListener) {
        gotoExtra.setOnClickListener(clickListener);
    }


    public EditText getEditTextView() {
        return answerEditText;
    }

    //make changes from XML attributes
    public void setAttribute(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.EditTextRowView);

        String hint = arr.getString(R.styleable.EditTextRowView_hint);
        setHint(hint);

        String editTextHint = arr.getString(R.styleable.EditTextRowView_edit_text_hint);
        setEditTextHint(editTextHint);

        /*String bottomHint = arr.getString(R.styleable.DynamicEditTextRow_bottom_hint);
        setInformation(bottomHint);*/

        String answer = arr.getString(R.styleable.EditTextRowView_answer);
        setText(answer);

        String order = arr.getString(R.styleable.EditTextRowView_order);
        setOrder(order);

        int answer_status = arr.getInt(R.styleable.EditTextRowView_answer_status, 0);
        setAnswerStatus(answer_status);

        int maxLines = arr.getInt(R.styleable.EditTextRowView_maxLines, 0);
        setMaxLines(maxLines);

        int maxLength = arr.getInt(R.styleable.EditTextRowView_maxLength, 2000);
        setMaxLength(maxLength);

        /*int isFocusable = arr.getInt(R.styleable.DynamicEditTextRow_focusable, 0);
        setFocusable(isFocusable);*/

        arr.recycle();
    }


    public void setMaxLength(int maxLength) {
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(maxLength);
        answerEditText.setFilters(inputFilters);
    }

    //set hint to hintTextView
    public void setHint(String hint) {
        if (hint == null) {
            hint = "";
        }
        hintTextView.setText(hint);
        hintTextView.invalidate();
    }

    public void setAnswerEditText(String ans) {
        if (ans == null) {
            ans = "";
        }
        answerEditText.setText(ans);
        answerEditText.invalidate();
    }


    //set hint to information
    public void setInformation(final String infoMsg) {
        if (infoMsg != null && !infoMsg.equals("")) {
            // information.setText(hint);
            information.setVisibility(VISIBLE);
            information.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInformationDialog(infoMsg);
                }
            });
        }
    }

    private void showInformationDialog(String string) {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getContext().getString(R.string.information));
        alertDialog.setIcon(R.drawable.ic_information);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void setErrorMsg(final String infoMsg) {
        if (infoMsg != null && !infoMsg.equals("")) {
            // information.setText(hint);
            errorMsg.setVisibility(VISIBLE);
            errorMsg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showErrorMsgDialog(infoMsg);
                }
            });
        }
    }

    private void showErrorMsgDialog(String string) {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getContext().getString(R.string.error_msg));
        alertDialog.setIcon(R.drawable.ic_error_msg);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    //set Order no. to OrderTextView
    public void setOrder(String order) {
        orderTextView.setText(order);
        /*if (order != 0) {
        } else {
            orderTextView.setVisibility(GONE);
        }*/
        orderTextView.invalidate();
    }

    //set Text from resources
    public void setText(int resourceString) {
        setText(getResources().getString(resourceString));
    }

    //set hint from string to editText
    public void setEditTextHint(String text) {
        if (text == null) {
            text = "";
        }
        answerEditText.setHint(text);
    }

    //set Text from string
    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        answerEditText.setText(text);
    }

    //change focus on answerEditText
    public void setFocusable(boolean focusable) {
        answerEditText.setFocusable(focusable);
    }

    //change focus on answerEditText
    public void setFocusableForEditText(int focusable) {
        if (focusable == FOCUSABLE) {
            answerEditText.setFocusable(true);
            answerEditText.setClickable(true);
        } else {
            answerEditText.setFocusable(false);
            answerEditText.setClickable(false);
            answerEditText.setEnabled(false);
        }

    }

    //change backdroud drawable of order depending upon the status of answer
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

    //changing text color to white
    private void changeOrder(TextView textView, int drawable, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            orderTextView.setBackground(getResources().getDrawable(drawable, null));
        } else {
            orderTextView.setBackground(getResources().getDrawable(drawable));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextColor(getResources().getColor(color, null));
        } else {
            textView.setTextColor(getResources().getColor(color));
        }
    }


    //set regex on answerEdiTextView
    public void setRegex(final String regex, boolean isNumber) {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder(dest);
                builder.replace(dstart, dend, source
                        .subSequence


                                (start, end).toString());
                if (!builder.toString().matches(regex)) {
                    setAnswerStatus(ANSWERED);
                    if (source.length() == 0) {
                        return dest.subSequence(dstart, dend);
                    }
                    return "";
                } else {
                    setAnswerStatus(NONE);
                }
                return null;
            }
        };

        answerEditText.setFilters(new InputFilter[]{filter});

        //  do not allow to enter "." at the beginning if input type is decimal
        //  NOTE: decimal and number is handle by regex
        if (isNumber) {
            answerEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        if (s.toString().equals(".")) {
                            answerEditText.setText("");
                        }
                    }
                }
            });
        }
    }

    //add textWatcher to answerEditText
    public void attachTextWatcher(TextWatcher watcher) {
        answerEditText.addTextChangedListener(watcher);
    }


    //set InputType same as EditText
    public void setInputType(int type) {
        answerEditText.setInputType(type | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    //setClickListener
    public void setOnCustomClickListener(OnClickListener listener) {
        answerEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        answerEditText.setCursorVisible(false);
        answerEditText.setOnClickListener(listener);
        //this.setOnClickListener(listener);
    }

    //set touch listener
    public void setOnTouchListner(OnTouchListener onTouchListner) {
        answerEditText.setOnTouchListener(onTouchListner);
    }

    //reset view
    public void setInvalidate() {
        setAnswerStatus(EditTextRowView.NONE);
        answerEditText.setText("");
    }

    //get text from answerEditText
    public String getText() {
        return answerEditText.getText().toString().trim();
    }

    //get order from view
    public String getOrder() {
        return orderTextView.getText().toString().trim();
    }

    //get text from hintTextView
    public String getHint() {
        return hintTextView.getText().toString().trim();
    }

    public void setMaxLines(int i) {
        if (i != 0) {
            answerEditText.setMaxLines(i);
        }
    }

    public void singleLine(boolean isSingleLine) {
        answerEditText.setSingleLine(isSingleLine);
    }

    public void setFocusChangeListener(OnFocusChangeListener focusChangeListner) {
        answerEditText.setOnFocusChangeListener(focusChangeListner);
    }

    public void isRequired(boolean b) {
        if (b) {
            star.setText("*");
        } else {
            star.setText("");
        }
    }


    public void setFocusableForEditText(boolean focusable, String questionType) {


        answerEditText.setFocusable(focusable);
        answerEditText.setEnabled(focusable);
        answerEditText.setClickable(focusable);
        if (questionType.equals("1") || questionType.equals("13") || questionType.equals("2")|| questionType.equals("15")) {
            answerEditText.setFocusableInTouchMode(focusable);
        }


    }

}

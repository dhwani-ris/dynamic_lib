package com.dhwaniris.dynamicForm.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dhwaniris.dynamicForm.R;

public class ImageRowView extends LinearLayout {

    private TextView orderTextView;
    private TextView hintTextView;
    private TextView gallery;
    private TextView camera;
    private TextView star;
    private ImageView answerImageView;
    private ImageView information;
    private ImageView error_msg;

    public static final int NONE = 0;
    public static final int ANSWERED = 1;
    public static final int NOT_ANSWERED = 2;

    public ImageRowView(Context context) {
        super(context);
        init(context);
    }

    public ImageRowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setAttribute(context, attrs);
    }

    public ImageRowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setAttribute(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageRowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        // orderTextView.setTextSize(getResources().getDimension(R.dimen.ordertextsize));
        orderTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (10 * scale + 0.5f));


        LayoutParams lp = new LayoutParams(
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

        error_msg = new ImageView(context);
        error_msg.setVisibility(GONE);
        error_msg.setImageResource(R.drawable.ic_error_msg);
        error_msg.setPadding(0, 0, 4, 0);

        star = new TextView(context);
        star.setTextColor(Color.RED);
        star.setGravity(Gravity.END);

        linearLayout.addView(orderTextView);

        LayoutParams layoutParams1 = new LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT);
        layoutParams1.weight = 1;
        hintTextView.setLayoutParams(layoutParams1);
        linearLayout.addView(hintTextView);
        linearLayout.addView(information);
        linearLayout.addView(error_msg);

        LayoutParams starParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        linearLayout.addView(star, starParams);
        addView(linearLayout);


        int ImageDP = (int) (250 * scale + 0.5f);
        lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                ImageDP);

        answerImageView = new ImageView(context);
        answerImageView.setVisibility(GONE);
        lp.setMargins(16, 16, 16, 16);
        addView(answerImageView, lp);

        lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayoutButtons = new LinearLayout(context);
        linearLayoutButtons.setOrientation(LinearLayout.HORIZONTAL);

        lp.weight = 1;
        lp.setMargins(8, 8, 8, 8);


        gallery = new TextView(context);
        gallery.setText(R.string.gallery);
        gallery.setWidth(LayoutParams.MATCH_PARENT);
        gallery.setPadding(pad8, pad8, pad8, pad8);

        gallery.setGravity(Gravity.CENTER);
        gallery.setCompoundDrawablePadding(4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gallery.setBackground(getResources().getDrawable(R.drawable.btn_boundry, null));
            gallery.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.ic_image, null), null, null);
        } else {
            gallery.setBackground(getResources().getDrawable(R.drawable.btn_boundry));
            gallery.setCompoundDrawables(null, getResources().getDrawable(R.drawable.ic_image), null, null);
        }
        gallery.setLayoutParams(lp);


        camera = new TextView(context);
        camera.setText(R.string.camera);
        camera.setWidth(LayoutParams.MATCH_PARENT);
        camera.setPadding(pad8, pad8, pad8, pad8);
        camera.setGravity(Gravity.CENTER);
        camera.setCompoundDrawablePadding(4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            camera.setBackground(getResources().getDrawable(R.drawable.btn_boundry, null));
            camera.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.ic_camera_new, null), null, null);
        } else {
            camera.setBackground(getResources().getDrawable(R.drawable.btn_boundry));
            camera.setCompoundDrawables(null, getResources().getDrawable(R.drawable.ic_camera_new), null, null);
        }
        camera.setLayoutParams(lp);

        linearLayoutButtons.addView(camera);
        linearLayoutButtons.addView(gallery);


        addView(linearLayoutButtons, lp);

        linearLayout.invalidate();
    }

    //make changes from XML attributes
    private void setAttribute(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ImageRowView);

        String title = arr.getString(R.styleable.ImageRowView_title);
        setTitle(title);

        String order = arr.getString(R.styleable.ImageRowView_order);
        setOrder(order);

        int answer_status = arr.getInt(R.styleable.ImageRowView_answer_status, 0);
        setAnswerStatus(answer_status);

        arr.recycle();
    }

    //set hint to hintTextView
    public void setTitle(String hint) {
        if (hint == null) {
            hint = "";
        }
        hintTextView.setText(hint);
        hintTextView.invalidate();
    }

    //set Order no. to OrderTextView
    public void setOrder(String order) {
        orderTextView.setText(order);
        orderTextView.invalidate();
    }

    //set Text from string
    public void setSrc(String path) {


        answerImageView.setVisibility(VISIBLE);

        Glide.with(getContext())
                .load(path)
                .fitCenter()
//                .placeholder(R.drawable.ic_camera_new)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(answerImageView);
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

        orderTextView.invalidate();
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

    public void setCameraClickListener(OnClickListener listener) {
        camera.setOnClickListener(listener);
    }

    public void setGalleryClickListener(OnClickListener listener) {
        gallery.setOnClickListener(listener);
    }

    public void isRequired(boolean b) {
        if (b) {
            star.setText("*");
        } else {
            star.setText("");
        }
    }

    public void hideButtons() {
        camera.setVisibility(GONE);
        gallery.setVisibility(GONE);
    }

    public void showButtons() {
        camera.setVisibility(VISIBLE);
        gallery.setVisibility(VISIBLE);
    }

    public void setInvalidate() {
        answerImageView.setVisibility(GONE);
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

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getContext().getString(R.string.information));
        alertDialog.setIcon(R.drawable.ic_information);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    public void setErrorMsg(final String infoMsg) {
        if (infoMsg != null && !infoMsg.equals("")) {
            // information.setText(hint);
            error_msg.setVisibility(VISIBLE);
            error_msg.setOnClickListener(view -> showErrorMsgDialog(infoMsg));
        }
    }

    private void showErrorMsgDialog(String string) {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getContext().getString(R.string.error_msg));
        alertDialog.setIcon(R.drawable.ic_error_view);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}

package com.dhwaniris.dynamicForm.questionTypes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.ui.activities.FullScreenImageActivity;
import com.dhwaniris.dynamicForm.utils.TouchImageView;

import java.util.LinkedHashMap;



import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.NEW_FORM;
import static com.dhwaniris.dynamicForm.base.BaseActivity.checkNetwork;


public class BaseLabelType extends BaseType {

    View view;

    private TouchImageView labelImage;

    public BaseLabelType(View view) {
        this.view = view;
    }


    public void setAnswerAnsQuestionData(LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList, LinkedHashMap<String, QuestionBean> questionBeenList) {
        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
    }

    public void setBasicFunctionality(View view, final QuestionBean questionBean, int formStatus) {

        TextView label = view.findViewById(R.id.label);
        labelImage = view.findViewById(R.id.label_image);
        label.setText(questionBean.getTitle());
        for (ValidationBean validationBean : questionBean.getValidation()) {
            switch (validationBean.get_id()) {
                case LibDynamicAppConfig.VAL_LABEL_AS_INSTRUCTION:
                    label.setGravity(Gravity.START);
                    label.setTextColor(view.getResources().getColor(R.color.black));
                    label.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            view.getResources().getDimension(R.dimen.dialog_item_text));
                    label.setTypeface(null, Typeface.NORMAL);

                    break;
                case LibDynamicAppConfig.VAL_LABEL_AS_HTML_TEXT:
                    label.setGravity(Gravity.START);
                    label.setTextColor(view.getResources().getColor(R.color.black));
                    label.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            view.getResources().getDimension(R.dimen.dialog_item_text));
                    label.setTypeface(null, Typeface.NORMAL);
                    String htmltext = questionBean.getTitle();
                    //  String htmltext = "<font \"color\"= \"000000\"><b>T</b>his text is bold.</font>";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        label.setText(Html.fromHtml(htmltext, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        label.setText(Html.fromHtml(htmltext));
                    }
                    break;
                case LibDynamicAppConfig.VAL_LABEL_AS_TV_IMAGE:
                    labelImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String imagePath = questionBean.getResource_urls().get(0).getUrl().trim();
                            Intent intent = new Intent(labelImage.getContext(), FullScreenImageActivity.class);
                            intent.putExtra("imageurl", imagePath);
                            intent.putExtra("titletext", questionBean.getTitle());
                            labelImage.getContext().startActivity(intent);
                            ((Activity) labelImage.getContext()).overridePendingTransition(R.anim.from_right, R.anim.to_right);


                        }
                    });
                    showImage(questionBean);
                    break;
            }
        }

        label.setTag(questionBean.get_id());

        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                view.setVisibility(View.GONE);
            }
        }
        //check if draft and is field filled or not
        else {

            if (checkValueForVisibility(questionBean)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void superChangeStatus(int status) {

    }


    @Override
    public void superChangeTitle(String title) {

    }

    private void showImage(QuestionBean questionBean) {
        String imageUrl;
        if (questionBean.getResource_urls().size() > 0 && questionBean.getResource_urls().get(0).getUrl() != null
                && !questionBean.getResource_urls().get(0).getUrl().trim().equals("")) {
            labelImage.setVisibility(View.VISIBLE);
            imageUrl = questionBean.getResource_urls().get(0).getUrl().trim();

            if (checkNetwork(labelImage.getContext())) {
                Glide.with(labelImage.getContext()).load(imageUrl)
                        .thumbnail(Glide.with(labelImage.getContext()).load(R.drawable.loading))
                        .into(labelImage);
            } else {
                //when no internet ..default image set ..not avilable
                Glide.with(labelImage.getContext()).load(R.drawable.not_available_image)
                        .into(labelImage);
            }

        }


    }
}




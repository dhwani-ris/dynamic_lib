package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;


public class ImageType extends BaseImageType {


    public ImageType(View view, final QuestionBean questionBean, int formStatus, QuestionHelperCallback.ImageViewListener ImageviewListener,
                     LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled>  answerBeanHelperList) {

        createDynamicView(view);
        setImageViewListener(ImageviewListener);

        this.questionBeenList = questionBeenList;
        this.answerBeanHelperList = answerBeanHelperList;
        setBasicFunctionality(questionBean, formStatus);

        this.questionBean = questionBean;
        this.formStatus = formStatus;
        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                dynamicImageViewRow.setVisibility(View.GONE);
            }
            createNewImageObject(questionBean, dynamicImageViewRow.getVisibility() == View.VISIBLE);
        } else {
            //draft action
            answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            if (answerBeanFilled != null && answerBeanFilled.isRequired()) {
                dynamicImageViewRow.isRequired(true);
            }
        }
        boolean iniitList = true;
        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }

        if (iniitList)
            initiOnClick();
    }

    private void initiOnClick() {
        dynamicImageViewRow.setCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                clickImage(questionBean);
            }
        });

        dynamicImageViewRow.setGalleryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                pickImage(questionBean);

            }
        });

    }


}

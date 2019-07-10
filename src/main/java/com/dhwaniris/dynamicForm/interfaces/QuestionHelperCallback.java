package com.dhwaniris.dynamicForm.interfaces;

import android.app.Dialog;

import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.questionTypes.AudioRecordType;

import java.util.List;




public class QuestionHelperCallback {
    interface AnswerInterface {
        void onAnswerReceived(QuestionBean questionBean);
    }


    public interface DataListener {
        void onhideKeyboard();

        void createSingleSelector(List<AnswerOptionsBean> answerOptionsBeen,
                                  QuestionBean questionBean, String header);

        void createMultiSelector(List<AnswerOptionsBean> ansOpt, List<String> selectedList,
                                 QuestionBean questionBean,
                                 String header, int checkLimit);

        void createTextData(String text, QuestionBean questionBean);

        void showAlert(String text, QuestionBean questionBean);

        void createDatePicker(QuestionBean questionBean, long max, long min);

        void createNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList);

        void changeTitle(RestrictionsBean restrictionsBean, String text);

        void clearQuestion(QuestionBean questionBean);

        void clickOnAdditionalButton(QuestionBean questionBean);

        String getUserLanguage();

    }

    public interface ImageViewListener {

        void clickImage(QuestionBean questionBean);

        void pickImage(QuestionBean questionBean);

        void onhidekeyboard();

        void createNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList);

    }

    public interface QuestionButtonClickListener {
        void onclickOnQuestionButton(QuestionBean questionBean);
    }

    public interface RecordAudioResponseListener {
        void onCreateNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList);

        void onRequestMicPermission(AudioRecordType audioRecordType);

        void onhidekeyboard();

        void onRequestForNewFilePath(AudioRecordType audioRecordType);

        void onVoiceRecorded(QuestionBean questionBean, String path, String length, boolean isRecored);
    }

    public interface RadioButtonListener {
        void onRadioButtonsChecked(QuestionBean questionBean, String id, String label);

        void onCreateNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList);

        void clickOnAdditionalButton(QuestionBean questionBean);
    }

    public interface ConsentListener {
        void onConsentButtonClick(QuestionBean questionBean);
    }

    public interface ConsentClickListener {
        void onConsentClick(QuestionBean questionBean, String value, String label, Dialog dialog);
    }

    public interface GeoTraceListener {
        void onTraceButtonClick(QuestionBean questionBean);

        void onDeleteButtonClick(QuestionBean questionBean);
    }

    public interface UnitConversionListener {
        void onUnitClick(QuestionBean questionBean);

        void saveValue(String value,String standardValue,String unit,QuestionBean questionBean);
    }

}

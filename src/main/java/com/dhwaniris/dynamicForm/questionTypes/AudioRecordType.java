package com.dhwaniris.dynamicForm.questionTypes;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.customViews.RecordAudioRowViewXML;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SYNCED_BUT_EDITABLE;


public class AudioRecordType extends BaseType {
    private QuestionHelperCallback.RecordAudioResponseListener recordAudioResponseListener;
    private RecordAudioRowViewXML dynamicRecordAudioView;
    public boolean isPermission = false;
    private boolean isSubmitted = false;

    public AudioRecordType(View view, int formstatus, QuestionBean questionBean,
                           LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled>
                                   answerBeanHelperList, QuestionHelperCallback.RecordAudioResponseListener recordAudioResponseListener) {
        dynamicRecordAudioView = new RecordAudioRowViewXML(view);
        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
        this.questionBean = questionBean;
        this.formStatus = formstatus;
        this.recordAudioResponseListener = recordAudioResponseListener;
        setBasicFunctionality(questionBean, formstatus);
        initListener();
        if (formstatus == SUBMITTED || formstatus == EDITABLE_SUBMITTED) {
            isSubmitted = true;
        }

    }


    private void initListener() {
        View.OnClickListener onClickListener = view -> {
//            switch (view.getId()) {
                if(view.getId()==R.id.btnRecord) {
                    RecordTask();
                }else if(view.getId() == R.id.btnPlay) {
                    if (voice_file != null)
                        PlayAudio();
                }else if(view.getId() == R.id.btnStop) {
                    StopRecodindAndAudio();
                }else if(view.getId() == R.id.btnReset){
                    resetAudio();
                }
//            }

        };

        dynamicRecordAudioView.setOnCustomClickListener(onClickListener);
    }

    void setBasicFunctionality(QuestionBean questionBean, int formStatus) {
        dynamicRecordAudioView.setTitle(questionBean.getTitle());
        dynamicRecordAudioView.setOrder(questionBean.getLabel());

        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                dynamicRecordAudioView.setVisibility(View.GONE);
            }
            recordAudioResponseListener.onCreateNewAnswerObject(questionBean, dynamicRecordAudioView.getVisivility() == View.VISIBLE);
            dynamicRecordAudioView.freshState();
        }
        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(AppConfing.VAL_REQUIRED)) {
                    dynamicRecordAudioView.isRequired(true);
                    break;
                }
            }
        }
        //check if draft and is field filled or not
        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT || formStatus == EDITABLE_SUBMITTED) {
            ///this check for if qestion come frist time in draft

            if (checkValueForVisibility(questionBean)) {
                dynamicRecordAudioView.setVisibility(View.VISIBLE);
            } else {
                dynamicRecordAudioView.setVisibility(View.GONE);
            }

            answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            if (answerBeanFilled != null && !answerBeanFilled.getAnswer().isEmpty()) {
                Answers answers = answerBeanFilled.getAnswer().get(0);
                if (answers != null) {
                    String path = answers.getValue();
                    if (!path.equals("")) {
                        voice_file = new File(path);
                        dynamicRecordAudioView.recorded();
                    } else {
                        dynamicRecordAudioView.freshState();
                    }
                }

            }

            if (isSubmitted) {
                dynamicRecordAudioView.submitted();
            }
        }

    }

    void hideKeyboard() {
        if (recordAudioResponseListener != null) {
            recordAudioResponseListener.onhidekeyboard();
        }
    }


    private File voice_file;
    private MediaPlayer mp;
    private boolean isPlaying;
    private boolean isRecording;
    private MediaRecorder myAudioRecorder;
    private CountDownTimer countDownTimer;

    public void setHint(String string) {
        dynamicRecordAudioView.setTitle(string);
    }

    public void setFilePath(File file) {
        voice_file = file;
    }

    private void RecordTask() {
        if (!isPermission) {
            recordAudioResponseListener.onRequestMicPermission(this);
        } else {
            startRecording();
        }
    }

    private void PlayAudio() {
        if (!isSubmitted) {
            dynamicRecordAudioView.startPlaying();
        } else {
            dynamicRecordAudioView.submitPlaying();
        }
        try {
            mp = new MediaPlayer();
            mp.setDataSource(voice_file.getAbsolutePath());
            mp.prepareAsync();
            mp.setOnPreparedListener(mp -> {

                try {
                    isPlaying = true;
                    isRecording = false;
                    mp.start();
                } catch (IllegalStateException e) {
                    mp.stop();
                    isPlaying = false;
                    isRecording = false;

                }
            });
            mp.setOnCompletionListener(mediaPlayer -> {

                isPlaying = false;
                isRecording = false;
                if (!isSubmitted)
                    dynamicRecordAudioView.recorded();
                else {
                    dynamicRecordAudioView.submitted();
                }
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
                mp = null;
            });
        } catch (IOException e) {
             Log.e("error",e.getMessage());;
        }

    }

    private void resetAudio() {

        dynamicRecordAudioView.freshState();
        isPlaying = false;
        isRecording = false;

        if (voice_file != null && voice_file.exists()) {
            voice_file.delete();
        }
        recordAudioResponseListener.onVoiceRecorded(questionBean, "", "", false);
        recordAudioResponseListener.onRequestForNewFilePath(this);
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = null;
            }
        }
    }


    public void startRecording() {
        recordAudioResponseListener.onRequestForNewFilePath(this);
        isPlaying = false;
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(String.valueOf(voice_file));


        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
            TimeCount();
            isRecording = true;
            dynamicRecordAudioView.startRecording();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
             Log.e("error",e.getMessage());;
        } catch (IOException e) {
            // TODO Auto-generated catch block
             Log.e("error",e.getMessage());;
        }

    }

    private void TimeCount() {

        countDownTimer = new CountDownTimer(60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                try {
                    dynamicRecordAudioView.setRecordButtonText(String.format(Locale.US, "%02d", seconds / 60)
                            + ":" + String.format(Locale.US, "%02d", seconds % 60), 0);

                } catch (Exception e) {

                    countDownTimer.cancel();
                }
            }

            public void onFinish() {

                try {
                    stopRecoding();
                } catch (Exception ignored) {
                }


            }
        }.start();

    }

    private void StopRecodindAndAudio() {

        if (isRecording && !isPlaying) {

            stopRecoding();

        } else if (!isRecording && isPlaying) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
            mp = null;
            if (!isSubmitted) {
                dynamicRecordAudioView.recorded();
            } else {
                dynamicRecordAudioView.submitted();
            }
            isRecording = false;
            isPlaying = false;
        }
    }

    private void stopRecoding() {

        isRecording = false;
        isPlaying = false;

        dynamicRecordAudioView.recorded();
        recordAudioResponseListener.onVoiceRecorded(questionBean, voice_file.getAbsolutePath(), "Audio", true);

        if (myAudioRecorder != null) {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
        }


        countDownTimer.cancel();
    }


    @Override
    public void superChangeStatus(int status) {
        dynamicRecordAudioView.setAnswerStatus(status);
    }


    @Override
    public void superResetQuestion() {
        resetAudio();
    }

    @Override
    public void superChangeTitle(String title) {
        dynamicRecordAudioView.setTitle(title);
    }

    @Override
    public void superSetErrorMsg(String errorMsg) {
        dynamicRecordAudioView.setErrorMsg(errorMsg);
    }

}


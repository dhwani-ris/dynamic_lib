package com.dhwaniris.dynamicForm.customViews;



import android.view.View;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class RecordAudioRowViewXML extends BaseXMLView {

    private TextView recordButton;
    private TextView playButton;
    private TextView stopButton;
    private TextView resetButton;

    public TextView getRecordButton() {
        return recordButton;
    }

    public TextView getPlayButton() {
        return playButton;
    }

    public TextView getStopButton() {
        return stopButton;
    }

    public TextView getResetButton() {
        return resetButton;
    }

    public RecordAudioRowViewXML(View view) {
        orderTextView = view.findViewById(R.id.txt_order);
        titleText = view.findViewById(R.id.titletext);
        star = view.findViewById(R.id.star);
        playButton = view.findViewById(R.id.btnPlay);
        stopButton = view.findViewById(R.id.btnStop);
        recordButton = view.findViewById(R.id.btnRecord);
        resetButton = view.findViewById(R.id.btnReset);
        information = view.findViewById(R.id.info);
        error_msg = view.findViewById(R.id.error_msg);
        this.view = view;

    }


    //change focus on answerEditText
    public void setFocusable(boolean focusable) {
        if (!focusable) {
            playButton.setVisibility(VISIBLE);
            recordButton.setVisibility(GONE);
            resetButton.setVisibility(GONE);
        }

    }

    //change focus on answerEditText
    public void setFocusable(int focusable) {
        if (focusable != FOCUSABLE) {
            playButton.setVisibility(VISIBLE);
            recordButton.setVisibility(GONE);
            resetButton.setVisibility(GONE);
        }

    }


    //setClickListener
    public void setOnCustomClickListener(View.OnClickListener clickListener) {
        playButton.setOnClickListener(clickListener);
        recordButton.setOnClickListener(clickListener);
        resetButton.setOnClickListener(clickListener);
        stopButton.setOnClickListener(clickListener);

    }


    //reset view
    public void setInvalidate() {
        setAnswerStatus(RecordAudioRowViewXML.NONE);

    }




    public void setRecordButtonText(String text, int id) {
        recordButton.setText(text);
    }

    public void startRecording() {
        resetButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(GONE);
        stopButton.setVisibility(VISIBLE);
        recordButton.setEnabled(false);
    }

    public void startPlaying() {
        resetButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(GONE);
        stopButton.setVisibility(VISIBLE);
        recordButton.setEnabled(false);
    }

    public void recorded() {
        playButton.setVisibility(VISIBLE);
        stopButton.setVisibility(GONE);
        resetButton.setVisibility(VISIBLE);
        recordButton.setEnabled(false);
        setAnswerStatus(1);

    }

    public void freshState() {
        playButton.setVisibility(GONE);
        stopButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        recordButton.setEnabled(true);
        recordButton.setText(R.string.record);
        setAnswerStatus(0);
    }

    public void submitted() {
        playButton.setVisibility(VISIBLE);
        stopButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        recordButton.setVisibility(GONE);
        setAnswerStatus(1);
    }

    public void submitPlaying() {
        playButton.setVisibility(GONE);
        stopButton.setVisibility(VISIBLE);
    }

}

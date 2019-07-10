package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.customViews.ImageRowView;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;

/**
 * Created by ${Sahjad} on 3/15/2019.
 */
public class SpecialImageView {
    private QuestionHelperCallback.ImageViewListener imageViewListener;
    ImageRowView dynamicImageViewRow;

    public SpecialImageView(QuestionHelperCallback.ImageViewListener imageViewListener,
                            View view) {
        this.imageViewListener = imageViewListener;
        dynamicImageViewRow = view.findViewById(R.id.custom);

        dynamicImageViewRow.setCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                clickImage();
            }
        });

        dynamicImageViewRow.setGalleryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                pickImage();

            }
        });
    }

    public void setData(String string) {

    }

    private void hideKeyboard() {
        imageViewListener.onhidekeyboard();
    }

    private void clickImage() {
        imageViewListener.clickImage(null);

    }

    private void pickImage() {
        imageViewListener.pickImage(null);
    }
}

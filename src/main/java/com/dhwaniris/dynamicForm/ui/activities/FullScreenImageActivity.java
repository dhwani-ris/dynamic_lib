package com.dhwaniris.dynamicForm.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.utils.TouchImageView;

import static com.dhwaniris.dynamicForm.base.BaseActivity.checkNetwork;


public class FullScreenImageActivity extends AppCompatActivity {

    TextView questionTitle;
    TouchImageView image;
    ImageView btnBack;
    private Context ctx;

    private String titleText;
    private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dy_activity_full_screen_image);
        bindView();
        ctx = FullScreenImageActivity.this;
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            titleText = intent.getStringExtra("titletext");
            imageUrl = intent.getStringExtra("imageurl");
            showImage();
        } else {
            Toast.makeText(ctx, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    void bindView() {
        questionTitle = findViewById(R.id.questionTitle);
        image = findViewById(R.id.image);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    void showImage() {

        if (checkNetwork(ctx)) {
            Glide.with(ctx).load(imageUrl)
                    .thumbnail(Glide.with(ctx).load(R.drawable.loading))
                    .into(image);
        } else {
            //when no internet default image set not avilable
            Glide.with(ctx).load(R.drawable.not_available_image)
                    .into(image);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.from_left, R.anim.to_left);
    }

}
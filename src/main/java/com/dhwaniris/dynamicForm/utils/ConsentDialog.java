package com.dhwaniris.dynamicForm.utils;

import android.app.Dialog;
import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;

import java.util.List;


/**
 * Created by ${Sahjad} on 4/30/2019.
 */
public class ConsentDialog {

    Context context;
    Dialog dialog;


    public ConsentDialog(Context context) {
        this.context = context;

    }

    public Dialog showConsent(String htmltext, QuestionHelperCallback.ConsentClickListener consentClickListener, boolean isFilled, QuestionBean questionBean) {

        List<AnswerOptionsBean> answer_option = questionBean.getAnswer_option();
        String acceptString = context.getString(R.string.accept);
        String rejectString = context.getString(R.string.reject);
        if (answer_option.size() == 2) {
            acceptString = answer_option.get(0).getName();
            rejectString = answer_option.get(1).getName();
        }


        final Dialog dialoga = new Dialog(context, R.style.AppFullScreenTheme);
        dialoga.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialoga.getWindow().getAttributes());
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialoga.getWindow().setAttributes(lp);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dy_consent_dialog, null);
        TextView title = view.findViewById(R.id.title);
        ImageButton close = view.findViewById(R.id.close);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setVisibility(View.GONE);
        Button btnAccept = view.findViewById(R.id.btn_accept);
        Button btnReject = view.findViewById(R.id.btn_reject);
        close.setVisibility(View.GONE);

        btnAccept.setText(acceptString);
        btnReject.setText(rejectString);
        ConstraintLayout buttonsLayout = view.findViewById(R.id.bottom_layout);
        /*LinearLayout checkLayout = view.findViewById(R.id.layout_read_check);
        CheckBox checkBox = view.findViewById(R.id.checkBox_terms_con);
        */
        WebView webView = view.findViewById(R.id.webview);
        webView.getSettings().setBuiltInZoomControls(true);
        title.setText(questionBean.getHint());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialoga.dismiss();
            }
        });
        //   String formatedHtmlText = Html.fromHtml(htmltext).toString();
        String formatedHtmlText = htmltext;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(formatedHtmlText, "text/html", "UTF-8");
        String finalAcceptString = acceptString;
        btnAccept.setOnClickListener(v -> {
            consentClickListener.onConsentClick(questionBean, "1", finalAcceptString, dialoga);
        });
        String finalRejectString = rejectString;
        btnReject.setOnClickListener(v -> {
            consentClickListener.onConsentClick(questionBean, "2", finalRejectString, dialoga);

        });
        buttonsLayout.setVisibility(isFilled ? View.GONE : View.VISIBLE);
        dialoga.setContentView(view);
        dialoga.show();

        return dialoga;
    }

}

package com.dhwaniris.dynamicForm.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.interfaces.UnansweredListener;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.HashMap;
import java.util.List;


public class UnansweredQusAdapter extends RecyclerView.Adapter<UnansweredQusAdapter.MyViewHolder> {

    public Context context;
    List<QuestionBeanFilled> ansList;
    private AlertDialog dialog;
    private UnansweredListener formListener;
    private boolean showAll;
    HashMap<String, QuestionBean> questionBeanHashMap;


    public UnansweredQusAdapter(UnansweredListener formListener, List<QuestionBeanFilled> list,
                                AlertDialog dialog, boolean showAll, HashMap<String, QuestionBean> questionBeanHashMap) {
        this.formListener = formListener;
        this.dialog = dialog;
        this.showAll = showAll;
        ansList = list;
        this.questionBeanHashMap = questionBeanHashMap;

    }

    @Override
    public UnansweredQusAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dy_item_pending_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
      /*  if (!ansList.get(pos).isRequired() && (!showAll || ansList.get(pos).isValid()) ||
                ansList.get(pos).getInput_type().equals(LibDynamicAppConfig.QUS_LABEL)) {

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0);
            holder.mainView.setLayoutParams(lp);
        } else {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.mainView.setLayoutParams(lp);
        }
*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.mainView.setLayoutParams(lp);

        QuestionBean questionBean = questionBeanHashMap.get(QuestionsUtils.Companion.getAnswerUniqueId(ansList.get(pos)));

        if (questionBean != null) {
            holder.text.setText(questionBean.getTitle());
            holder.order.setText(questionBean.getLabel());

        } else {
            holder.text.setText(ansList.get(pos).getTitle());
            holder.order.setText(ansList.get(pos).getLabel());
        }

        if (showAll) {
            if (ansList.get(pos).isFilled()) {
                changeOrder(holder.order, R.drawable.green_circle, R.color.white);
            } else {
                changeOrder(holder.order, R.drawable.white_circle, R.color.black);
            }
        } else {
            changeOrder(holder.order, R.drawable.red_circle, R.color.white);
        }

       /* if (pos == ansList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }*/

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formListener.Question(QuestionsUtils.Companion.getAnswerUniqueId(ansList.get(pos)));
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ansList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text, order;
        LinearLayout mainView;
        RelativeLayout divider;

        public MyViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.text);
            order = v.findViewById(R.id.order);
            mainView = v.findViewById(R.id.mainView);
            divider = v.findViewById(R.id.divider);

        }
    }

    //changing txtVillageName color to white
    private void changeOrder(TextView textView, int drawable, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(textView.getResources().getDrawable(drawable, null));
        } else {
            textView.setBackground(textView.getResources().getDrawable(drawable));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextColor(textView.getResources().getColor(color, null));
        } else {
            textView.setTextColor(textView.getResources().getColor(color));
        }
    }

    void setDataAndShowAllStatus(List<QuestionBeanFilled> list,
                                 boolean showAll) {
        this.ansList = list;
        this.showAll = showAll;
        notifyDataSetChanged();

    }

}
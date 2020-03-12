package com.dhwaniris.dynamicForm.adapters;

import android.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.interfaces.SelectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;




public class SingleSelectAdapter extends RecyclerView.Adapter<SingleSelectAdapter.MyViewHolder> implements Filterable {

    private List<AnswerOptionsBean> list;
    private QuestionBean questionBean;
    private AlertDialog dialog;
    //    private DropDownListener formListener;
    private SelectListener formListener;
    private ArrayList<AnswerOptionsBean> arrayList;


//    private List<Answers> p_ans;

    //    public SingleSelectAdapter(Context context, DropDownListener formListener, List<AnswerOptionsBean> list,
    public SingleSelectAdapter(SelectListener formListener, List<AnswerOptionsBean> list,
                               QuestionBean questionBean, AlertDialog dialog) {
        this.list = list;
        this.formListener = formListener;
        this.questionBean = questionBean;
        this.dialog = dialog;
        arrayList = new ArrayList<AnswerOptionsBean>();
        this.arrayList.addAll(list);

        //   this.p_ans = p_ans;
    }

    @Override
    public SingleSelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dy_item_dialog_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override


    public void onBindViewHolder(MyViewHolder holder, final int pos) {
        holder.text.setText(list.get(pos).getName());
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formListener.SingleSelector(questionBean, list.get(pos).getName(), list.get(pos).get_id(),list.size()==1);
                formListener = null;
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        LinearLayout mainView;


        public MyViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.text);
            mainView = v.findViewById(R.id.mainView);
        }
    }


    public void search(String s) {
        String searchTxt = s.trim().toLowerCase(Locale.getDefault());
        list.clear();
        if (searchTxt.length() == 0) {
            list.addAll(arrayList);
        } else {
            for (AnswerOptionsBean answerOptionsBean : arrayList) {
                if (answerOptionsBean.getName().toLowerCase(Locale.getDefault()).contains(searchTxt)) {
                    list.add(answerOptionsBean);
                }
            }
        }
        notifyDataSetChanged();
    }


}
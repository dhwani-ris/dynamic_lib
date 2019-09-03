package com.dhwaniris.dynamicForm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.SelectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;





public class MultiSelectorAdapter extends RecyclerView.Adapter<MultiSelectorAdapter.MyViewHolder> implements Filterable {

    private List<AnswerOptionsBean> list;
    private SelectListener formListener;
    private int checkLimit = 0;
    private List<String> checkedList;
    private ArrayList<AnswerOptionsBean> arrayList;
    private List<String> itemsCheckAlone = new ArrayList<>();


    //    public SingleSelectAdapter(Context context, DropDownListener formListener, List<AnswerOptionsBean> list,
    public MultiSelectorAdapter(SelectListener formListener, List<AnswerOptionsBean> list,
                                int checkLimit, List<String> checkedList, QuestionBean questionBean) {

        this.list = list;
        this.formListener = formListener;
        this.checkLimit = checkLimit;
        this.checkedList = checkedList;
        arrayList = new ArrayList<AnswerOptionsBean>();
        this.arrayList.addAll(list);

        if (questionBean.getValidation().size() > 0) {
            for (ValidationBean validationBean : questionBean.getValidation()) {
                if (validationBean != null && validationBean.get_id().equals(LibDynamicAppConfig.VAL_DESELECT_ALL)) {
                    itemsCheckAlone.add(validationBean.getError_msg());
                }
            }
        }


    }

    @Override
    public MultiSelectorAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dy_item_multi_selector_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        holder.checkBox.setText(list.get(pos).getName());
        if (checkedList.contains(list.get(pos).get_id())) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        if (checkedList.size() >= checkLimit && !checkedList.contains(list.get(pos).get_id())) {
            holder.checkBox.setEnabled(false);
        } else {
            holder.checkBox.setEnabled(true);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    if (!checkedList.contains(list.get(pos).get_id())) {

                        ///if check alone item available
                        if (itemsCheckAlone.size() > 0) {
                            if (itemsCheckAlone.contains(list.get(pos).get_id()) || itemsCheckAlone.containsAll(checkedList)) {
                                checkedList.clear();
                                formListener.SingleSelector(null, "ClearAll", null,false);
                                checkedList.add(list.get(pos).get_id());
                                formListener.SingleSelector(null, null, list.get(pos).get_id(),false);
                            } else {
                                checkedList.add(list.get(pos).get_id());
                                formListener.SingleSelector(null, null, list.get(pos).get_id(),false);
                            }
                            notifyDataSetChanged();
                        } else {
                            checkedList.add(list.get(pos).get_id());
                            formListener.SingleSelector(null, null, list.get(pos).get_id(),false);
                            notifyItemChanged(pos);
                        }

                    }

                } else {
                    if (checkedList.contains(list.get(pos).get_id())) {
                        checkedList.remove(list.get(pos).get_id());
                        formListener.SingleSelector(null, list.get(pos).get_id(), null,false);

                    }
                    notifyItemChanged(pos);
                }
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
        CheckBox checkBox;
        LinearLayout mainView;


        public MyViewHolder(View v) {
            super(v);
            mainView = (LinearLayout) v.findViewById(R.id.mainView);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox_item);
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
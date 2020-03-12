package com.dhwaniris.dynamicForm.adapters;

import android.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.interfaces.ProfileFormListener;
import com.dhwaniris.dynamicForm.pojo.DropDownHelper;

import java.util.List;



public class LocalSingleAdapter extends RecyclerView.Adapter<LocalSingleAdapter.MyViewHolder> implements Filterable {

    private List<DropDownHelper> list;
    private String label;
    private AlertDialog dialog;
    //    private DropDownListener formListener;
    private ProfileFormListener formListener;


    //    public SingleSelectAdapter(Context context, DropDownListener formListener, List<AnswerOptionsBean> list,
    public LocalSingleAdapter( ProfileFormListener formListener, List<DropDownHelper> list,
                              AlertDialog dialog, String label) {
        this.list = list;
        this.formListener = formListener;
        this.label = label;
        this.dialog = dialog;
    }

    @Override
    public LocalSingleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dy_item_dialog_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int pos) {
        holder.text.setText(list.get(pos).getLabel());
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formListener.SingleSelector(list.get(pos).getLabel(), String.valueOf(list.get(pos).getId()), label);
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

        public MyViewHolder(View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.text);
        }
    }


}
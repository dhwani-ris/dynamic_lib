package com.dhwaniris.dynamicForm.utils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.dhwaniris.dynamicForm.db.dbhelper.form.Form;

import java.util.List;

public class ActorDiffCallback extends DiffUtil.Callback {

    private final List<Form> oldList;
    private final List<Form> newList;

    public ActorDiffCallback(List<Form> oldList, List<Form> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getFormId() == newList.get(newItemPosition).getFormId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Form oldItem = oldList.get(oldItemPosition);
        final Form newItem = newList.get(newItemPosition);

        return oldItem.getFormId() == newItem.getFormId();
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

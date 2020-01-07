package com.smalsus.redhorizonvbr.util;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.smalsus.redhorizonvbr.model.GroupModel;

import java.util.List;

public class GroupListDiffCallback extends DiffUtil.Callback {

    private final List<GroupModel> mOldGrouplist;
    private final List<GroupModel> mNewGroupList;

    public GroupListDiffCallback(List<GroupModel> oldGroupList, List<GroupModel> newGroupList) {
        this.mOldGrouplist = oldGroupList;
        this.mNewGroupList = newGroupList;
    }

    @Override
    public int getOldListSize() {
        return mOldGrouplist.size();
    }

    @Override
    public int getNewListSize() {
        return mNewGroupList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldGrouplist.get(oldItemPosition).getId().equals(mNewGroupList.get(
                newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final GroupModel oldEmployee = mOldGrouplist.get(oldItemPosition);
        final GroupModel newEmployee = mNewGroupList.get(newItemPosition);

        return oldEmployee.getId().equals(newEmployee.getId());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
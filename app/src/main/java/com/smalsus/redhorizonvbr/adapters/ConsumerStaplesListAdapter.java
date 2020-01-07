package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.StaplesGridLayoutBinding;
import com.smalsus.redhorizonvbr.model.ConsumerStaplesModel;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.utils.RecycleItemDecoration;
import com.smalsus.redhorizonvbr.view.activities.ConsumerStaplesSubItem;
import com.smalsus.redhorizonvbr.view.fragments.ConsumerStaples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConsumerStaplesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ConsumerStaplesModel> consumerStaplesList;


    public ConsumerStaplesListAdapter(Context mContext) {
        this.mContext = mContext;
        this.consumerStaplesList=new ArrayList<>();
    }

    public void updateEmployeeListItems(List<ConsumerStaplesModel> list) {
        final ContactListDiffCallBack diffCallback = new ContactListDiffCallBack(this.consumerStaplesList, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.consumerStaplesList.clear();
        this.consumerStaplesList.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StaplesGridLayoutBinding itemBinding = StaplesGridLayoutBinding.inflate(layoutInflater, parent, false);
        return new ConsumerStaplesViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ConsumerStaplesListAdapter.ConsumerStaplesViewHolder) {
            ((ConsumerStaplesListAdapter.ConsumerStaplesViewHolder) holder).bindTo(consumerStaplesList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return consumerStaplesList.size();
    }

    private class ConsumerStaplesViewHolder extends RecyclerView.ViewHolder {

        private StaplesGridLayoutBinding binding;

        private ConsumerStaplesViewHolder(StaplesGridLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(ConsumerStaplesModel consumerStaplesModel) {
            binding.viewAllCatBtn.setOnClickListener(view -> {
                Intent intent=new Intent(mContext, ConsumerStaplesSubItem.class);
                intent.putExtra(ConsumerStaplesSubItem.CATEGORY_ID_VALUE,consumerStaplesModel.getValue());
                intent.putExtra(ConsumerStaplesSubItem.CATEGORY_Name_VALUE,consumerStaplesModel.getCategoryName());
                mContext.startActivity(intent);
            });
            binding.categoryName.setText(consumerStaplesModel.getCategoryName());
            int[] rainbow = mContext.getResources().getIntArray(R.array.rainbow);
            binding.staplesGridMainLayout.setBackgroundColor(rainbow[new Random().nextInt(rainbow.length)]);
            binding.consumerStaplesCategoryItemList.setLayoutManager(new GridLayoutManager(mContext, 2));
            ConsumerStaplesItemAdapter consumerStaplesItemAdapter = new ConsumerStaplesItemAdapter(mContext,consumerStaplesModel.getSubcategoryItemList(),consumerStaplesModel.getValue());
            binding.consumerStaplesCategoryItemList.setAdapter(consumerStaplesItemAdapter);
            binding.consumerStaplesCategoryItemList.addItemDecoration(new RecycleItemDecoration(
                    mContext.getResources().getDimensionPixelSize(R.dimen.grid_item_divider),
                    mContext.getResources().getInteger(R.integer.photo_list_preview_columns)));
        }
    }

    public class ContactListDiffCallBack extends DiffUtil.Callback {

        private final List<ConsumerStaplesModel> mOldEmployeeList;
        private final List<ConsumerStaplesModel> mNewEmployeeList;

        public ContactListDiffCallBack(List<ConsumerStaplesModel> oldEmployeeList, List<ConsumerStaplesModel> newEmployeeList) {
            this.mOldEmployeeList = oldEmployeeList;
            this.mNewEmployeeList = newEmployeeList;
        }

        @Override
        public int getOldListSize() {
            return mOldEmployeeList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewEmployeeList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldEmployeeList.get(oldItemPosition).get_id().equals(mNewEmployeeList.get(
                    newItemPosition).get_id());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final ConsumerStaplesModel oldEmployee = mOldEmployeeList.get(oldItemPosition);
            final ConsumerStaplesModel newEmployee = mNewEmployeeList.get(newItemPosition);

            return oldEmployee.get_id().equals(newEmployee.get_id());
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}

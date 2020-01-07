package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.smalsus.redhorizonvbr.databinding.StaplesGridLayoutItemBinding;
import com.smalsus.redhorizonvbr.model.ConsumerStaplesSubcategoryItem;
import com.smalsus.redhorizonvbr.view.activities.StaplesSubcategoryItemProductActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConsumerStaplesItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ConsumerStaplesSubcategoryItem> subcategoryItemList;
    private int categoryValue;

    public ConsumerStaplesItemAdapter(Context mContext,List<ConsumerStaplesSubcategoryItem> subcategoryItemList,int categoryValue) {
        this.mContext = mContext;
        this.subcategoryItemList=subcategoryItemList;
        this.categoryValue=categoryValue;
    }

    public void updateEmployeeListItems(List<ConsumerStaplesSubcategoryItem> list) {
        final ContactListDiffCallBack diffCallback = new ContactListDiffCallBack(this.subcategoryItemList, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.subcategoryItemList.clear();
        this.subcategoryItemList.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StaplesGridLayoutItemBinding itemBinding = StaplesGridLayoutItemBinding.inflate(layoutInflater, parent, false);
        return new ConsumerStaplesItemViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ConsumerStaplesItemAdapter.ConsumerStaplesItemViewHolder) {
            ((ConsumerStaplesItemAdapter.ConsumerStaplesItemViewHolder) holder).bindTo(subcategoryItemList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return subcategoryItemList.size();
    }

    private class ConsumerStaplesItemViewHolder extends RecyclerView.ViewHolder {

        private StaplesGridLayoutItemBinding binding;

        private ConsumerStaplesItemViewHolder(StaplesGridLayoutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(ConsumerStaplesSubcategoryItem consumerStaplesSubcategoryItem) {

            binding.getRoot().setOnClickListener(view -> {
                Intent intent=new Intent(mContext, StaplesSubcategoryItemProductActivity.class);
                intent.putExtra(StaplesSubcategoryItemProductActivity.CATEGORY_ID_VALUE,categoryValue);
                intent.putExtra(StaplesSubcategoryItemProductActivity.SUBCATEGORY_ID_VALUE,consumerStaplesSubcategoryItem.getWorth());
                mContext.startActivity(intent);
            });
            binding.subcategoryName.setText(consumerStaplesSubcategoryItem.getName());
            Picasso.get().load(consumerStaplesSubcategoryItem.getImageURL()).into(binding.dashboardItemIc);
        }
    }

    public class ContactListDiffCallBack extends DiffUtil.Callback {

        private final List<ConsumerStaplesSubcategoryItem> mOldEmployeeList;
        private final List<ConsumerStaplesSubcategoryItem> mNewEmployeeList;

        public ContactListDiffCallBack(List<ConsumerStaplesSubcategoryItem> oldEmployeeList, List<ConsumerStaplesSubcategoryItem> newEmployeeList) {
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
            return mOldEmployeeList.get(oldItemPosition).getWorth()==(mNewEmployeeList.get(
                    newItemPosition).getWorth());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final ConsumerStaplesSubcategoryItem oldEmployee = mOldEmployeeList.get(oldItemPosition);
            final ConsumerStaplesSubcategoryItem newEmployee = mNewEmployeeList.get(newItemPosition);

            return oldEmployee.getWorth()==(newEmployee.getWorth());
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}


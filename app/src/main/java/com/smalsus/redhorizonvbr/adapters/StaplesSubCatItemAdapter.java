package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.smalsus.redhorizonvbr.databinding.VenderProductItemViewBinding;
import com.smalsus.redhorizonvbr.interfaces.ProductItemClickListner;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.squareup.picasso.Picasso;
import java.util.List;

public class StaplesSubCatItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ProductItemModelClass> subcategoryItemList;
    private ProductItemClickListner productItemClickListner;

    public StaplesSubCatItemAdapter(Context mContext,List<ProductItemModelClass> subcategoryItemList) {
        this.mContext = mContext;
        this.subcategoryItemList=subcategoryItemList;
    }
    public void setProductItemClickedListner(ProductItemClickListner productItemClickListner){
        this.productItemClickListner=productItemClickListner;
    }

    public void updateListItems(List<ProductItemModelClass> list) {
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
        VenderProductItemViewBinding itemBinding = VenderProductItemViewBinding.inflate(layoutInflater, parent, false);
        return new ProductItemViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductItemViewHolder) {
            ((ProductItemViewHolder) holder).bindTo(subcategoryItemList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return subcategoryItemList.size();
    }

    private class ProductItemViewHolder extends RecyclerView.ViewHolder {

        private VenderProductItemViewBinding binding;

        private ProductItemViewHolder(VenderProductItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(ProductItemModelClass article) {
            binding.productname.setText(article.getProductName());
            if (article.getImageUrl() != null && article.getImageUrl().size() > 0) {
                Picasso.get().load(article.getImageUrl().get(0)).into(binding.productItemImage);
            }
            binding.productprice.setText(String.valueOf(article.getPrice()));
            binding.getRoot().setOnClickListener(view -> {
                if(productItemClickListner!=null){
                    productItemClickListner.productitemClicked(article);
                }
            }); }
    }

    public class ContactListDiffCallBack extends DiffUtil.Callback {

        private final List<ProductItemModelClass> mOldEmployeeList;
        private final List<ProductItemModelClass> mNewEmployeeList;

        public ContactListDiffCallBack(List<ProductItemModelClass> oldEmployeeList, List<ProductItemModelClass> newEmployeeList) {
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
            final ProductItemModelClass oldEmployee = mOldEmployeeList.get(oldItemPosition);
            final ProductItemModelClass newEmployee = mNewEmployeeList.get(newItemPosition);
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


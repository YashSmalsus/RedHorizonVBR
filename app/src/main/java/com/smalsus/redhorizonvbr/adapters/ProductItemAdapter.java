package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.databinding.NetworkItemBinding;
import com.smalsus.redhorizonvbr.databinding.VenderProductItemViewBinding;
import com.smalsus.redhorizonvbr.interfaces.ProductItemClickListner;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.squareup.picasso.Picasso;

public class ProductItemAdapter extends PagedListAdapter<ProductItemModelClass, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private NetworkState networkState;
    private ProductItemClickListner productItemClickListner;

    public ProductItemAdapter(Context context) {
        super(ProductItemModelClass.DIFF_CALLBACK);
        this.context = context;
    }
    public void setProductItemClickListner(ProductItemClickListner productItemClickListner){
        this.productItemClickListner=productItemClickListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            NetworkItemBinding headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
            return new NetworkStateItemViewHolder(headerBinding);

        } else {
            VenderProductItemViewBinding itemBinding = VenderProductItemViewBinding.inflate(layoutInflater, parent, false);
            return new ProductItemViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductItemAdapter.ProductItemViewHolder) {
            ((ProductItemAdapter.ProductItemViewHolder) holder).bindTo(getItem(position));
        } else {
            ((ProductItemAdapter.NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }


    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }


    public class ProductItemViewHolder extends RecyclerView.ViewHolder {

        private VenderProductItemViewBinding binding;

        public ProductItemViewHolder(VenderProductItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindTo(ProductItemModelClass article) {

            binding.productname.setText(article.getProductName());
            if (article.getImageUrl() != null && article.getImageUrl().size() > 0) {
                Picasso.get().load(article.getImageUrl().get(0)).into(binding.productItemImage);
            }
            binding.productprice.setText(String.valueOf(article.getPrice()));
            binding.getRoot().setOnClickListener(view -> {
                if(productItemClickListner!=null){
                    productItemClickListner.productitemClicked(article);
                }
            });

        }
    }


    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private NetworkItemBinding binding;

        public NetworkStateItemViewHolder(NetworkItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.errorMsg.setText(networkState.getMsg());
            } else {
                binding.errorMsg.setVisibility(View.GONE);
            }
        }
    }


}

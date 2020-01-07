package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.databinding.ProductItemViewLayoutBinding;
import com.smalsus.redhorizonvbr.interfaces.ProductItemClickListner;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RelatedProductItemAdapter extends RecyclerView.Adapter<RelatedProductItemAdapter.ProductItemViewHolder> {
    private Context context;
    private List<ProductItemModelClass> productItemModelClassList;

    private ProductItemClickListner productItemClickListner;

    public RelatedProductItemAdapter(Context context, List<ProductItemModelClass> productItemModelClassList) {

        this.context = context;
        this.productItemModelClassList = productItemModelClassList;
    }

    public void setProductItemClickListner(ProductItemClickListner productItemClickListner) {
        this.productItemClickListner = productItemClickListner;
    }

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ProductItemViewLayoutBinding itemBinding = ProductItemViewLayoutBinding.inflate(layoutInflater, parent, false);
        ProductItemViewHolder viewHolder = new ProductItemViewHolder(itemBinding);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        holder.bindTo(productItemModelClassList.get(position));
    }


    @Override
    public int getItemCount() {
        return productItemModelClassList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }



    public class ProductItemViewHolder extends RecyclerView.ViewHolder {

        private ProductItemViewLayoutBinding binding;

        public ProductItemViewHolder(ProductItemViewLayoutBinding binding) {
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
                if (productItemClickListner != null) {
                    productItemClickListner.productitemClicked(article);
                }
            });

        }
    }


}

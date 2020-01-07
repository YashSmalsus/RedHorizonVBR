package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.OrderStatusItemLayoutBinding;
import com.smalsus.redhorizonvbr.model.OrderStatus;
import com.smalsus.redhorizonvbr.model.ProductTrackingModel;
import com.smalsus.redhorizonvbr.utils.VectorDrawableUtils;

import java.util.List;

public class ProductDeliveryStatusAdapter extends RecyclerView.Adapter<ProductDeliveryStatusAdapter.ProductStatusViewHolder> {

    private Context mContext;
    private List<ProductTrackingModel> productTrackingModelList;


    public ProductDeliveryStatusAdapter(Context context, List<ProductTrackingModel> productTrackingModelList) {
        this.mContext = context;
        this.productTrackingModelList = productTrackingModelList;
    }


    @NonNull
    @Override
    public ProductStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        OrderStatusItemLayoutBinding itemBinding = OrderStatusItemLayoutBinding.inflate(layoutInflater, parent, false);
        return new ProductStatusViewHolder(itemBinding,viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductStatusViewHolder holder, int position) {
        holder.bindView(productTrackingModelList.get(position));
    }


    private void setMarker(OrderStatusItemLayoutBinding binding, int drawableResId, int colorFilter) {
        binding.timeline.setMarker(VectorDrawableUtils.getDrawable(binding.getRoot().getContext(), drawableResId, ContextCompat.getColor(binding.getRoot().getContext(), colorFilter)));

    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }


    @Override
    public int getItemCount() {
        return productTrackingModelList.size();
    }

    public class ProductStatusViewHolder extends RecyclerView.ViewHolder {
        private OrderStatusItemLayoutBinding binding;


        public ProductStatusViewHolder(OrderStatusItemLayoutBinding binding,int viewType) {
            super(binding.getRoot());
            this.binding = binding;
            binding.timeline.initLine(viewType);

        }

        public void bindView(ProductTrackingModel productTrackingModel) {
            if (productTrackingModel.getStatus() == OrderStatus.INACTIVE) {
               setMarker(binding, R.drawable.ic_marker_inactive, R.color.grey_transparent_20);
            } else if (productTrackingModel.getStatus() == OrderStatus.ACTIVE) {
               setMarker(binding, R.drawable.ic_marker_active, R.color.grey_transparent_50);
            } else {
               setMarker(binding, R.drawable.ic_marker, R.color.grey_transparent_50);
            }

            if (!productTrackingModel.getDate().isEmpty()) {
                this.binding.textTimelineDate.setVisibility(View.VISIBLE);
                this.binding.textTimelineDate.setText(productTrackingModel.getDate());

            } else
                this.binding.textTimelineDate.setVisibility(View.GONE);

            this.binding.textTimelineTitle.setText(productTrackingModel.getMessage());
        }
    }
}

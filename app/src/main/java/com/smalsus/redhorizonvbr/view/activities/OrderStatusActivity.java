package com.smalsus.redhorizonvbr.view.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.ProductDeliveryStatusAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityOrderStatusBinding;
import com.smalsus.redhorizonvbr.model.OrderStatus;
import com.smalsus.redhorizonvbr.model.ProductTrackingModel;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusActivity extends AppCompatActivity {

    private ActivityOrderStatusBinding orderStatusBinding;
    private List<ProductTrackingModel> mDataList = new ArrayList<>();
    private ProductDeliveryStatusAdapter deliveryStatusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderStatusBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_status);
        setDataListItems();
        initRecyclerView();

    }

    private void setDataListItems() {
        mDataList.add(new ProductTrackingModel("Item successfully delivered", "", OrderStatus.INACTIVE));
        mDataList.add(new ProductTrackingModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE));
        mDataList.add(new ProductTrackingModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
        mDataList.add(new ProductTrackingModel("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.COMPLETED));
        mDataList.add(new ProductTrackingModel("Item is packed and will dispatch soon", "2017-02-11 09:30", OrderStatus.COMPLETED));
        mDataList.add(new ProductTrackingModel("Order is being readied for dispatch", "2017-02-11 08:00", OrderStatus.COMPLETED));
        mDataList.add(new ProductTrackingModel("Order processing initiated", "2017-02-10 15:00", OrderStatus.COMPLETED));
        mDataList.add(new ProductTrackingModel("Order confirmed by seller", "2017-02-10 14:30", OrderStatus.COMPLETED));
        mDataList.add(new ProductTrackingModel("Order placed successfully", "2017-02-10 14:00", OrderStatus.COMPLETED));
    }

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        orderStatusBinding.statusTrakingTimeline.setLayoutManager(mLayoutManager);
        deliveryStatusAdapter = new ProductDeliveryStatusAdapter(this, mDataList);
        orderStatusBinding.statusTrakingTimeline.setAdapter(deliveryStatusAdapter);
        orderStatusBinding.statusTrakingTimeline.setNestedScrollingEnabled(false);
        orderStatusBinding.statusTrakingTimeline.setHasFixedSize(true);
    }

}

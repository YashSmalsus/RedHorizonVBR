package com.smalsus.redhorizonvbr.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.ConsumerStaplesItemAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityConsumerStaplesSubItemBinding;
import com.smalsus.redhorizonvbr.model.ConsumerStaplesSubcategoryItem;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.utils.RecycleItemDecoration;
import com.smalsus.redhorizonvbr.viewmodel.ConsumerStaplesSubItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class ConsumerStaplesSubItem extends AppCompatActivity {
    private ActivityConsumerStaplesSubItemBinding consumerStaplesSubItemBinding;
    private ConsumerStaplesItemAdapter consumerStaplesItemAdapter;
    private ConsumerStaplesSubItemViewModel consumerStaplesSubItemViewModel;
    public static final  String CATEGORY_ID_VALUE="category_id_value";
    public static final  String CATEGORY_Name_VALUE="category_name_value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consumerStaplesSubItemBinding = DataBindingUtil.setContentView(this, R.layout.activity_consumer_staples_sub_item);
        List<ConsumerStaplesSubcategoryItem> subcategoryItemList = new ArrayList<>();
        Intent intent=getIntent();
        int catId=intent.getIntExtra(CATEGORY_ID_VALUE,0);
        String name=intent.getStringExtra(CATEGORY_Name_VALUE);
        consumerStaplesSubItemViewModel= ViewModelProviders.of(this).get(ConsumerStaplesSubItemViewModel.class);
        consumerStaplesSubItemBinding.consumerStaplesSubItemListView.setLayoutManager(new GridLayoutManager(this, 2));
        consumerStaplesItemAdapter = new ConsumerStaplesItemAdapter(this, subcategoryItemList,catId);

        consumerStaplesSubItemBinding.consumerStaplesSubItemListView.setAdapter(consumerStaplesItemAdapter);
        consumerStaplesSubItemBinding.consumerStaplesSubItemListView.addItemDecoration(new RecycleItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.grid_item_divider),
                getResources().getInteger(R.integer.photo_list_preview_columns)));

        getNetworkState();
        getSubCategoryItemList(catId);
        consumerStaplesSubItemBinding.categoryName.setText(name);
        consumerStaplesSubItemBinding.addEventBackbtn.setOnClickListener(view -> finish());
    }

    private void getSubCategoryItemList(int catId){
        consumerStaplesSubItemViewModel.getStaplesSubItem(catId).observe(this, consumerStaplesSubcategoryItems -> {
            consumerStaplesItemAdapter.updateEmployeeListItems(consumerStaplesSubcategoryItems);
        });
    }

    private void getNetworkState(){
        consumerStaplesSubItemViewModel.getNetworkState().observe(this, networkState -> {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                consumerStaplesSubItemBinding.loaderProgressbar.setVisibility(View.VISIBLE);
            } else {
                consumerStaplesSubItemBinding.loaderProgressbar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                Toast.makeText(this,networkState.getMsg(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}


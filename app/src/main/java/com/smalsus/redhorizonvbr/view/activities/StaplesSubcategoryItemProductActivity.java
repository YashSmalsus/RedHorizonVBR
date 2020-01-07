package com.smalsus.redhorizonvbr.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.StaplesSubCatItemAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityStaplesSubcategoryItemProductBinding;
import com.smalsus.redhorizonvbr.interfaces.ProductItemClickListner;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.viewmodel.StaplesSubCategoryProductItemViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StaplesSubcategoryItemProductActivity extends AppCompatActivity implements ProductItemClickListner {

    public static final String CATEGORY_ID_VALUE = "category_id_value";
    public static final String SUBCATEGORY_ID_VALUE = "subcategory_id_value";
    private ActivityStaplesSubcategoryItemProductBinding subcategoryItemProductBinding;
    private StaplesSubCatItemAdapter subCatItemAdapter;
    private StaplesSubCategoryProductItemViewModel subCategoryProductItemViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subcategoryItemProductBinding = DataBindingUtil.setContentView(this, R.layout.activity_staples_subcategory_item_product);
        subCategoryProductItemViewModel = ViewModelProviders.of(this).get(StaplesSubCategoryProductItemViewModel.class);
        subcategoryItemProductBinding.productList.setLayoutManager(new LinearLayoutManager(this));
        List<ProductItemModelClass> productItemModelClassList = new ArrayList<>();
        subCatItemAdapter = new StaplesSubCatItemAdapter(this, productItemModelClassList);
        subCatItemAdapter.setProductItemClickedListner(this);
        subcategoryItemProductBinding.productList.setAdapter(subCatItemAdapter);
        Intent intent = getIntent();
        int cat = intent.getIntExtra(CATEGORY_ID_VALUE, 0);
        int subCat = intent.getIntExtra(SUBCATEGORY_ID_VALUE, 0);
        getNetworkState();
        getSubCategoryProductItem(cat, subCat);
        subcategoryItemProductBinding.staplesSubcategoryItemBack.setOnClickListener(view -> finish());

        subcategoryItemProductBinding.postTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
              if(editable.toString().length()==0){
                  getSubCategoryProductItem(cat,subCat);
              }

            }
        });

        subcategoryItemProductBinding.postTextview.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        subCategoryProductItemViewModel.searchItem(subcategoryItemProductBinding.postTextview.getText().toString());
        subcategoryItemProductBinding.postTextview.clearFocus();
        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(subcategoryItemProductBinding.postTextview.getWindowToken(), 0);
    }

    @Override
    public void productitemClicked(ProductItemModelClass productItem) {
        Intent intent = new Intent(StaplesSubcategoryItemProductActivity.this, ProductDetailsActivity.class);
        intent.putExtra("PRODUCT_DETAILS_ITEM",(Parcelable) productItem);
        intent.putExtra(ProductDetailsActivity.PRODUCT_DESCRIPTION_KEY, (Serializable) productItem.getDescription());
        intent.putExtra(ProductDetailsActivity.PRODUCT_SPECIFIATION_KEY, (Serializable) productItem.getSpecification());
        startActivity(intent);
    }

    private void getSubCategoryProductItem(int cat, int subCat) {
        subCategoryProductItemViewModel.getStaplesSubItem(cat, subCat).observe(this, productItemModelClasses -> {
            subCatItemAdapter.updateListItems(productItemModelClasses);
        });
    }

    private void getNetworkState() {
        subCategoryProductItemViewModel.getNetworkState().observe(this, networkState -> {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                subcategoryItemProductBinding.progressbarLoader.setVisibility(View.VISIBLE);
            } else {
                subcategoryItemProductBinding.progressbarLoader.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                Toast.makeText(this, networkState.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


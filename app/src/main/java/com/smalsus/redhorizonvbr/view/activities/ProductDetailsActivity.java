package com.smalsus.redhorizonvbr.view.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.RelatedProductItemAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityProductDetailsBinding;
import com.smalsus.redhorizonvbr.databinding.ProductDescriptionLayoutViewBinding;
import com.smalsus.redhorizonvbr.interfaces.ProductItemClickListner;
import com.smalsus.redhorizonvbr.model.MyCartProductItemModel;
import com.smalsus.redhorizonvbr.model.ProductDetailsModel;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.rest.AddCartAPI;
import com.smalsus.redhorizonvbr.rest.GetMyCart;
import com.smalsus.redhorizonvbr.rest.PrductDetailsApi;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import com.smalsus.redhorizonvbr.utils.RecycleItemDecoration;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends CoreBaseActivity implements ProductItemClickListner {
    public static final String PRODUCT_DESCRIPTION_KEY = "preoduct_desscription_key";
    public static final String PRODUCT_SPECIFIATION_KEY = "preoduct_specification_key";
    public static final String MY_CART_PRODUCT_ITEM = "my_cart_product_item";
    private List<ProductItemModelClass> productItemModelClassList;
    private RelatedProductItemAdapter relatedProductItemAdapter;
    private ActivityProductDetailsBinding productDetailsBinding;
    private List<ProductItemModelClass> cartProductItemModel;
    private ProductItemModelClass productItemModelClass;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        setSupportActionBar(productDetailsBinding.toolbar);
        productDetailsBinding.myCartButton.setOnClickListener(view -> {
            goToMyCartScreen();
        });

        userInfo = HRpreference.getInstance(this).getUserInfo();
        getMyCartProduct(userInfo.getAuthToken());
        Intent intent = getIntent();
        productItemModelClass = intent.getParcelableExtra("PRODUCT_DETAILS_ITEM");
        setProductDetails(productItemModelClass);
        HashMap<String, String> productDescription = (HashMap<String, String>) intent.getSerializableExtra(PRODUCT_DESCRIPTION_KEY);
        HashMap<String, String> productSpecification = (HashMap<String, String>) intent.getSerializableExtra(PRODUCT_SPECIFIATION_KEY);
        setProductDescription(productDescription);
        productItemModelClassList = new ArrayList<>();
        productDetailsBinding.productDetialsBack.setOnClickListener(view -> finish());
        productDetailsBinding.nestedLayout.productImageItemContainer.setOnClickListener(view -> {
            Intent intent1 = new Intent(ProductDetailsActivity.this, ImageSliderActivity.class);
            intent1.putExtra(ImageSliderActivity.IMAGE_SLIDER_LIST, (Serializable) productItemModelClass.getImageUrl());
            startActivity(intent1);
        });

        productDetailsBinding.nestedLayout.dashboardlist.setLayoutManager(new GridLayoutManager(this, 2));
        relatedProductItemAdapter = new RelatedProductItemAdapter(this, productItemModelClassList);
        relatedProductItemAdapter.setProductItemClickListner(this);
        productDetailsBinding.nestedLayout.dashboardlist.setAdapter(relatedProductItemAdapter);
        productDetailsBinding.nestedLayout.dashboardlist.addItemDecoration(new RecycleItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.fab_margin),
                getResources().getInteger(R.integer.photo_list_preview_columns)));

        getProductDetails(productItemModelClass.get_id());
        productDetailsBinding.nestedLayout.buyNowButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(ProductDetailsActivity.this, OrderSummaryScreen.class);
            intent1.putExtra("PRODUCT_DETAILS_ITEM", productItemModelClass);
            startActivity(intent1);
        });
        productDetailsBinding.nestedLayout.productDescriptionAction.setOnClickListener(view -> {
            onShowPopup(productDescription, productSpecification, productItemModelClass);
        });


    }

    private void setProductDetails(ProductItemModelClass productItemModelClass) {
        productDetailsBinding.nestedLayout.productName.setText(productItemModelClass.getProductName());
        productDetailsBinding.nestedLayout.productPrice.setText(String.format("$ %s", String.valueOf(productItemModelClass.getPrice())));
        if (productItemModelClass.getImageUrl().size() > 0)
            Picasso.get().load(productItemModelClass.getImageUrl().get(0)).into(productDetailsBinding.nestedLayout.productDetailsImage);
        setProductImage(productItemModelClass.getImageUrl());
    }

    private void setProductDescription(HashMap<String, String> productDescription) {
        if (productDescription != null) {
            for (Map.Entry<String, String> entry : productDescription.entrySet()) {
                View layout2 = LayoutInflater.from(this).inflate(R.layout.product_description_item_view, productDetailsBinding.nestedLayout.productDetailsContainer, false);
                TextView textView = layout2.findViewById(R.id.descptionTitle);
                TextView imageView = layout2.findViewById(R.id.descptionValue);
                textView.setText(entry.getKey());
                imageView.setText(entry.getValue());
                productDetailsBinding.nestedLayout.productDetailsContainer.addView(layout2);
            }
        } else {
            productDetailsBinding.nestedLayout.productDetailsContainerTop.setVisibility(View.GONE);
        }


    }


    private void getProductDetails(String productId) {
        RestApiFactory.cteateService(PrductDetailsApi.class).fetchProductDetials(productId)
                .enqueue(new Callback<ProductDetailsModel>() {
                    @Override
                    public void onResponse(Call<ProductDetailsModel> call, Response<ProductDetailsModel> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            productItemModelClassList.addAll(response.body().getRelatedItem());
                            relatedProductItemAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductDetailsModel> call, Throwable t) {
                        String errorMessage = t.getMessage();

                    }
                });
    }

    public void onShowPopup(HashMap<String, String> productDescription, HashMap<String, String> productSpecification, ProductItemModelClass productItemModelClass) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ProductDescriptionLayoutViewBinding headerBinding = ProductDescriptionLayoutViewBinding.inflate(layoutInflater, null, false);
        if (productItemModelClass != null) {
            headerBinding.productName.setText(productItemModelClass.getProductName());
            headerBinding.productPrice.setText(String.format("$ %s", String.valueOf(productItemModelClass.getPrice())));
            Picasso.get().load(productItemModelClass.getImageUrl().get(0)).into(headerBinding.productImage);
        }
        if (productDescription != null) {
            for (Map.Entry<String, String> entry : productDescription.entrySet()) {
                View layout2 = LayoutInflater.from(this).inflate(R.layout.product_description_item_view, productDetailsBinding.nestedLayout.productDetailsContainer, false);
                TextView textView = layout2.findViewById(R.id.descptionTitle);
                TextView imageView = layout2.findViewById(R.id.descptionValue);
                textView.setText(entry.getKey());
                imageView.setText(entry.getValue());
                headerBinding.productDescriptionContainer.addView(layout2);
            }
        } else {
            headerBinding.productDescriptionContainerTop.setVisibility(View.GONE);
        }
        if (productSpecification != null) {
            for (Map.Entry<String, String> entry : productSpecification.entrySet()) {
                View layout2 = LayoutInflater.from(this).inflate(R.layout.product_description_item_view, productDetailsBinding.nestedLayout.productDetailsContainer, false);
                TextView textView = layout2.findViewById(R.id.descptionTitle);
                TextView imageView = layout2.findViewById(R.id.descptionValue);
                textView.setText(entry.getKey());
                imageView.setText(entry.getValue());
                headerBinding.productSpecificationContainer.addView(layout2);
            }
        } else {
            headerBinding.productSpecificationContainerTop.setVisibility(View.GONE);
        }

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        PopupWindow popWindow = new PopupWindow(headerBinding.getRoot(), width, height - 50, true);
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_grey_border_layout));
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.PopupAnimation);
        popWindow.showAtLocation(productDetailsBinding.getRoot(), Gravity.BOTTOM, 0, 100);
        popWindow.setOnDismissListener(() -> {

        });
        headerBinding.closePupup.setOnClickListener(view -> {
            popWindow.dismiss();
        });
    }

    private void setProductImage(List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            View layout2 = LayoutInflater.from(this).inflate(R.layout.product_image_item_view, productDetailsBinding.nestedLayout.productImageItemContainer, false);
            TextView textView = layout2.findViewById(R.id.imageCountView);
            ImageView imageView = layout2.findViewById(R.id.productItemImage);
            Picasso.get().load(imageUrls.get(i)).into(imageView);
            productDetailsBinding.nestedLayout.productImageItemContainer.addView(layout2);
            if (i > 2) {
                textView.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    private void setMyCartItemCount() {
        productDetailsBinding.cartCount.setText(String.valueOf(cartProductItemModel.size()));
        if (isItemAvailbleInCart()) {
            productDetailsBinding.nestedLayout.addTOCart.setText("Go to Cart");
            productDetailsBinding.nestedLayout.addTOCart.setOnClickListener(view -> {
                goToMyCartScreen();
            });
        } else {
            productDetailsBinding.nestedLayout.addTOCart.setOnClickListener(view -> {
                addProductToCart(productItemModelClass.get_id(), userInfo.getAuthToken());
            });
        }
    }

    private boolean isItemAvailbleInCart() {
        boolean isAvailble = false;
        for (ProductItemModelClass productItem : cartProductItemModel) {
            if (productItem.get_id().equals(productItemModelClass.get_id()))
                isAvailble = true;
        }
        return isAvailble;
    }

    private void getMyCartProduct(String header) {
        Call<MyCartProductItemModel> call = RestApiFactory.cteateService(GetMyCart.class).getMyCartData(header);
        call.enqueue(new Callback<MyCartProductItemModel>() {
            @Override
            public void onResponse(@NonNull Call<MyCartProductItemModel> call, @NonNull Response<MyCartProductItemModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartProductItemModel = response.body().getProduct();
                    setMyCartItemCount();
                }
            }

            @Override
            public void onFailure(Call<MyCartProductItemModel> call, Throwable t) {
                String error = "Unknown Error " + t.getLocalizedMessage();

            }
        });
    }


    private void addProductToCart(String id, String header) {
        Call<Object> call = RestApiFactory.cteateService(AddCartAPI.class).addTOCart(id, header);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    if (!isItemAvailbleInCart()) {
                        cartProductItemModel.add(productItemModelClass);
                        showSnackBar();
                        setMyCartItemCount();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                String error = "Unknown Error " + t.getLocalizedMessage();
            }
        });
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(productDetailsBinding.productDetailsViewGroup, "Item Successfully Added to Cart", Snackbar.LENGTH_LONG)
                .setAction("Go to Cart", view -> goToMyCartScreen());

        snackbar.show();
    }

    private void goToMyCartScreen() {
        Intent intent = new Intent(ProductDetailsActivity.this, MyCartScreen.class);
        intent.putParcelableArrayListExtra(MY_CART_PRODUCT_ITEM, (ArrayList<? extends Parcelable>) cartProductItemModel);
        startActivity(intent);
    }

    @Override
    public void productitemClicked(ProductItemModelClass productItem) {
        productItemModelClass=productItem;
        setProductDetails(productItem);
       // setProductDescription((HashMap<String, String>) productItem.getDescription());
        setMyCartItemCount();
    }
}

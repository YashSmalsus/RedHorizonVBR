package com.smalsus.redhorizonvbr.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.MyCartProductItemBinding;
import com.smalsus.redhorizonvbr.databinding.MyCartScreenBinding;
import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.PaymentRequest;
import com.smalsus.redhorizonvbr.rest.AddCartAPI;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartScreen extends AppCompatActivity {
    public static final int REQUEST_PAYMENT_CODE = 1001;
    private MyCartScreenBinding myCartScreenBinding;
    private UserInfo userInfo;
    private List<ProductItemModelClass> cartProductItemModel;
    private float price = 0;
    private String deliveryAddress;
    private String userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCartScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_cart_screen);
        HRpreference hRpreference = HRpreference.getInstance(getApplicationContext());
        Intent intent = getIntent();
        cartProductItemModel = intent.getParcelableArrayListExtra(ProductDetailsActivity.MY_CART_PRODUCT_ITEM);
        if (cartProductItemModel != null)
            setMyCartScreenBindingData(cartProductItemModel);
        myCartScreenBinding.myCartBackButton.setOnClickListener(view -> {
            finish();
        });
        myCartScreenBinding.continueButton.setOnClickListener(view -> {
            if (deliveryAddress!=null && deliveryAddress.length() > 10) {
                Intent intent1 = new Intent(MyCartScreen.this, PaymentScreen.class);
                intent1.putExtra(PaymentScreen.PRODUCTS_ITEM_PRICES, price);
                if (cartProductItemModel != null) {
                    intent1.putExtra(PaymentScreen.PRODUCT_ITEM_COUNTS, cartProductItemModel.size());
                }
                startActivityForResult(intent1, REQUEST_PAYMENT_CODE);
            }else{
                Toast.makeText(MyCartScreen.this,"Please Add Delivery Address",Toast.LENGTH_LONG).show();
            }

        });
        userInfo = hRpreference.getUserInfo();

        myCartScreenBinding.changeOrAddAddress.setOnClickListener(view -> {
            Intent intent1 = new Intent(MyCartScreen.this, SelectAddressScreen.class);
            startActivity(intent1);
        });

    }

    private void setUserAddress(UserDeliveryAddress userDeliveryAddress) {
        if (userDeliveryAddress != null) {
            deliveryAddress = " " + userDeliveryAddress.getName() + " , " + userDeliveryAddress.getPin_code();
            userAddress = userDeliveryAddress.getBuildingName() + userDeliveryAddress.getArea() + " ," + userDeliveryAddress.getCity() + " ," + userDeliveryAddress.getState();
            myCartScreenBinding.userNameTV.setText(String.format("Deliver to - %s", deliveryAddress));
            myCartScreenBinding.useraddressTV.setText(userAddress);
            myCartScreenBinding.changeOrAddAddress.setText("Change");
        } else {
            myCartScreenBinding.changeOrAddAddress.setText("Add Address");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        UserDeliveryAddress userDeliveryAddress = HRpreference.getInstance(getApplicationContext()).getUserAddressInfo();
        setUserAddress(userDeliveryAddress);
    }

    private void setMyCartScreenBindingData(List<ProductItemModelClass> productItemModelClassList) {

        if (productItemModelClassList != null && productItemModelClassList.size() > 0) {
            myCartScreenBinding.emptyItemView.setVisibility(View.GONE);
            for (ProductItemModelClass productItemModelClass : productItemModelClassList) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                MyCartProductItemBinding headerBinding = MyCartProductItemBinding.inflate(layoutInflater, myCartScreenBinding.productItemViewContainer, false);
                headerBinding.removeFromListBtn.setTag(productItemModelClass.get_id());
                headerBinding.removeFromListBtn.setOnClickListener(view -> {
                    int size = myCartScreenBinding.productItemViewContainer.getChildCount();
                    showDeleteItemConfiramtion(productItemModelClass, size - 1, headerBinding);
                });

                if (productItemModelClass.getImageUrl() != null && productItemModelClass.getImageUrl().size() > 0)
                    Picasso.get().load(productItemModelClass.getImageUrl().get(0)).into(headerBinding.productItemImage);

                headerBinding.myCartItemProductName.setText(productItemModelClass.getProductName());
                headerBinding.priceTv.setText(String.format("$ %s", String.valueOf(productItemModelClass.getPrice())));
                myCartScreenBinding.productItemViewContainer.addView(headerBinding.getRoot());
                price = price + productItemModelClass.getPrice();
                setPriceDetailsData(price, productItemModelClassList.size());
            }

        } else {
            myCartScreenBinding.emptyItemView.setVisibility(View.VISIBLE);
        }
    }

    private void setPriceDetailsData(float price, int itemCount) {
        if (itemCount > 0) {
            myCartScreenBinding.priceTitleTv.setText(String.format("Price(%d Item )", itemCount));
            myCartScreenBinding.priceValueTv.setText(String.format("$ %s", String.valueOf(price)));
            myCartScreenBinding.payableAmountPrice.setText(String.format("$ %s", String.valueOf(price)));
            myCartScreenBinding.allItemPriceTv.setText(String.format("$ %s", String.valueOf(price)));
        } else {
            myCartScreenBinding.emptyItemView.setVisibility(View.VISIBLE);
        }

    }


    private void deleteProductFromCart(String id, String header) {
        Call<Object> call = RestApiFactory.cteateService(AddCartAPI.class).deleteFromCart(id, header);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MyCartScreen.this, "Item Successfully Deleted from Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                String error = "Unknown Error " + t.getLocalizedMessage();
            }
        });
    }

    private void showDeleteItemConfiramtion(ProductItemModelClass productItemModelClass, int size, MyCartProductItemBinding headerBinding) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.confirm_connection_popup_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        TextView confirmMessageView = customView.findViewById(R.id.confirmMessage);
        ImageView profileImage = customView.findViewById(R.id.confirmUserProfile);
        profileImage.setVisibility(View.GONE);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        String confirmMessgae = "<font color='red'>" + "Do want to remove this item from Cart?" + "</font>";
        confirmMessageView.setText(Html.fromHtml(confirmMessgae));
        declineButton.setText("Cancel");
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            alertDialog.dismiss();
            deleteProductFromCart(productItemModelClass.get_id(), userInfo.getAuthToken());
            myCartScreenBinding.productItemViewContainer.removeView(headerBinding.getRoot());
            price = price - productItemModelClass.getPrice();
            setPriceDetailsData(price, size);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }


    private void placeOrderRequest(JSONArray product, String address, int paymentType, String token) {
        PaymentRequest request = new PaymentRequest();
        request.placeOrderRequest(product, address, paymentType, token, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(MyCartScreen.this, "Your Order has been successfully places", Toast.LENGTH_LONG).show();
                        goToBackScreen();
                    });

                }
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYMENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                UserInfo userInfo = HRpreference.getInstance(getApplicationContext()).getUserInfo();
                JSONArray jsonArray = new JSONArray();
                for (ProductItemModelClass productItemModelClass : cartProductItemModel) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", productItemModelClass.get_id());
                        jsonObject.put("quantity", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }
                String deliveryAddressValue = deliveryAddress + " \n " + userAddress;
                placeOrderRequest(jsonArray, deliveryAddressValue, 1, userInfo.getAuthToken());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void goToBackScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}

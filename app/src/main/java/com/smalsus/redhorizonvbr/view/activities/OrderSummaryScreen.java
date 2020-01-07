package com.smalsus.redhorizonvbr.view.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.ActivityOrderSummaryBinding;
import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.PaymentRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class OrderSummaryScreen extends AppCompatActivity {
    public static final int REQUEST_PAYMENT_CODE = 1001;
    public static String USER_DELIVERY_ADDRESS = "user_delivery_address";
    private ActivityOrderSummaryBinding orderSummaryBinding;
    private UserDeliveryAddress userDeliveryAddress;
    private ProductItemModelClass productItemModelClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderSummaryBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary_screen);
        setSupportActionBar(orderSummaryBinding.toolbar);
        Intent intent = getIntent();
        productItemModelClass = intent.getParcelableExtra("PRODUCT_DETAILS_ITEM");
        orderSummaryBinding.setProductDetails(productItemModelClass);
        orderSummaryBinding.orderSummaryContent.addAddressButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(OrderSummaryScreen.this, SelectAddressScreen.class);
            intent1.putExtra(USER_DELIVERY_ADDRESS, userDeliveryAddress);
            startActivity(intent1);
        });
        orderSummaryBinding.orderSummaryBack.setOnClickListener(view -> finish());
        if (productItemModelClass.getImageUrl() != null && productItemModelClass.getImageUrl().size() > 0)
            Picasso.get().load(productItemModelClass.getImageUrl().get(0)).into(orderSummaryBinding.orderSummaryContent.productItemImage);


        orderSummaryBinding.orderSummaryContent.continueButton.setOnClickListener(view -> {
            if(userDeliveryAddress!=null){
                Intent intent1 = new Intent(OrderSummaryScreen.this, PaymentScreen.class);
                intent1.putExtra(PaymentScreen.PRODUCTS_ITEM_PRICES, productItemModelClass.getPrice());
                intent1.putExtra(PaymentScreen.PRODUCT_ITEM_COUNTS, 1);
                startActivityForResult(intent1, REQUEST_PAYMENT_CODE);
            }else{
                Toast.makeText(OrderSummaryScreen.this,"Please Add Delivery Address",Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userDeliveryAddress = HRpreference.getInstance(getApplicationContext()).getUserAddressInfo();
        orderSummaryBinding.setUserAddress(userDeliveryAddress);
    }


    private void placeOrderRequest(JSONArray product, String address, int paymentType, String token) {
        PaymentRequest request = new PaymentRequest();
        request.placeOrderRequest(product, address, paymentType, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    Toast.makeText(OrderSummaryScreen.this,"Your Order has been successfully placed",Toast.LENGTH_LONG).show();
                    goToBackScreen();
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
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", productItemModelClass.get_id());
                    jsonObject.put("quantity", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
                String address=userDeliveryAddress.getName()+" \n "+userDeliveryAddress.getBuildingName()+", "+userDeliveryAddress.getArea()+"\n"+userDeliveryAddress.getCity()+" ,"+userDeliveryAddress.getState()+" - "+userDeliveryAddress.getPin_code();
                placeOrderRequest(jsonArray, address, 1, userInfo.getAuthToken());
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

package com.smalsus.redhorizonvbr.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.PaymentScreenBinding;
import com.smalsus.redhorizonvbr.utils.PaymentsUtil;
import com.smalsus.redhorizonvbr.view.fragments.AcceptFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentScreen extends CoreBaseActivity {
    public static final String PRODUCTS_ITEM_PRICES = "product_item_prices";
    public static final String PRODUCT_ITEM_COUNTS = "product_item_counts";
    private static final String TAG_FRAGMENT_CHECKOUT = "TAG_FRAGMENT_CHECKOUT";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private PaymentsClient mPaymentsClient;
    private PaymentScreenBinding paymentScreenBinding;
    private float price;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_screen);
        setSupportActionBar(paymentScreenBinding.toolbar);
        mPaymentsClient = PaymentsUtil.createPaymentsClient(this);
        possiblyShowGooglePayButton();
        Intent intent = getIntent();
        price = intent.getFloatExtra(PRODUCTS_ITEM_PRICES, 0f);
        count = intent.getIntExtra(PRODUCT_ITEM_COUNTS, 0);
        paymentScreenBinding.paymentScreenContaint.buttonRegularCheckout.setOnClickListener(view -> launchAcceptFragment());
        paymentScreenBinding.paymentScreenContaint.googlepayButton.setOnClickListener(this::requestPayment);
        setPriceDetailsData(price, count);
        paymentScreenBinding.paymentScreenBack.setOnClickListener(view -> {
            FragmentManager fm =getSupportFragmentManager();
            if(fm.getBackStackEntryCount()>0) {
                fm.popBackStack();
            }else{
                showDiscardPaymentConfirmation();
            }

        });
    }

    private void setPriceDetailsData(float price, int itemCount) {
        if (itemCount > 0) {
            paymentScreenBinding.paymentScreenContaint.priceTitleTv.setText(String.format("Price(%d Item )", itemCount));
            paymentScreenBinding.paymentScreenContaint.priceValueTv.setText(String.format("$ %s", String.valueOf(price)));
            paymentScreenBinding.paymentScreenContaint.payableAmountPrice.setText(String.format("$ %s", String.valueOf(price)));
        }
    }


    private void launchAcceptFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AcceptFragment checkoutFragment =
                (AcceptFragment) fragmentManager.findFragmentByTag(TAG_FRAGMENT_CHECKOUT);
        if (checkoutFragment == null) {
            checkoutFragment =
                    AcceptFragment.getInstance(price,count);
            fragmentManager.beginTransaction()
                    .replace(R.id.layout_container, checkoutFragment, TAG_FRAGMENT_CHECKOUT)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void possiblyShowGooglePayButton() {
        final JSONObject isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (isReadyToPayJson == null) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
        if (request == null) {
            return;
        }

        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null) {
                        setGooglePayAvailable(task1.getResult());
                    } else {
                        Log.w("isReadyToPay failed", task1.getException());
                    }
                });
    }

    private void setGooglePayAvailable(boolean available) {
        if (available) {
            paymentScreenBinding.paymentScreenContaint.googlepayButton.setVisibility(View.VISIBLE);
        } else {
            paymentScreenBinding.paymentScreenContaint.googlepayButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }
                paymentScreenBinding.paymentScreenContaint.googlepayButton.setClickable(true);
                break;
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;
        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d("BillingName", billingName);
            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG)
                    .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }


    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    public void requestPayment(View view) {
        paymentScreenBinding.paymentScreenContaint.googlepayButton.setClickable(false);
        String price = "500";
        JSONObject paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (paymentDataRequestJson == null) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    private void showDiscardPaymentConfirmation() {
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
        String confirmMessgae = "<font color='red'>" + "Do want to Discard the Payment?" + "</font>";
        confirmMessageView.setText(Html.fromHtml(confirmMessgae));
        declineButton.setText("Cancel");
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            alertDialog.dismiss();
            finish();
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm =getSupportFragmentManager();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }else{
            showDiscardPaymentConfirmation();
        }
    }
}

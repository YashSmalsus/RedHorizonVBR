package com.smalsus.redhorizonvbr.view.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.ProductDetailsModel;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.model.TextItemQRModel;
import com.smalsus.redhorizonvbr.rest.PrductDetailsApi;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import com.smalsus.redhorizonvbr.view.activities.ProductDetailsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QrCodeScannerGenrator extends Fragment implements ZXingScannerView.ResultHandler {

    private final String HUAWEI = "huawei";
    private final int MY_CAMERA_REQUEST_CODE = 6515;
    private OnFragmentInteractionListener mListener;
    private ZXingScannerView qrCodeScanner;
    private ImageButton searchRecentHistory, closeBottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private TextView textInfoView;
    private RelativeLayout textDetailsContainer;
    private CardView productsDetailsRoot;
    private ImageView productImageView;
    private TextView productname, producPrice;

    public QrCodeScannerGenrator() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static QrCodeScannerGenrator newInstance() {
        QrCodeScannerGenrator fragment = new QrCodeScannerGenrator();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code_scanner_genrator, container, false);
        qrCodeScanner = view.findViewById(R.id.qrCodeScanner);
        productsDetailsRoot = view.findViewById(R.id.productsDetailsRoot);
        textDetailsContainer = view.findViewById(R.id.textDetailsContainer);
        LinearLayout bottom_sheet = view.findViewById(R.id.textQRCodeBottomDetailsheet);
        searchRecentHistory = view.findViewById(R.id.recentHistory);
        ImageButton genrateQrCodeBtn = view.findViewById(R.id.genrateBarcode);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        textInfoView = view.findViewById(R.id.textInfoView);
        productImageView = view.findViewById(R.id.productImageView);
        productname = view.findViewById(R.id.productname);
        producPrice = view.findViewById(R.id.producPrice);
        closeBottomSheet = view.findViewById(R.id.closeBottomSheet);
        closeBottomSheet.setOnClickListener(view1 -> sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        genrateQrCodeBtn.setOnClickListener(view12 -> {
            if (mListener != null)
                mListener.qrCodeScannerTabIntaction();
        });


        setScannerProperties();

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    private void setScannerProperties() {
        qrCodeScanner.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.qr_color_laser);
        qrCodeScanner.setMaskColor(R.color.qr_color_laser);
        if (Build.MANUFACTURER.equals(HUAWEI))
            qrCodeScanner.setAspectTolerance(0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                return;
            }
        }
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeScanner.stopCamera();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void handleResult(Result result) {
        if (result != null) {
            String resultString = result.getText();
            // String decryptedString = EncryptionHelper.getInstance().getDecryptionString(resultString);
            Object json = null;
            try {
                json = new JSONTokener(resultString).nextValue();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (json instanceof JSONObject) {
                try {
                    JSONObject jsonObject = new JSONObject(resultString);
                    if (jsonObject.has("qrCodeType")) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<TextItemQRModel>() {
                        }.getType();
                        TextItemQRModel eventList = gson.fromJson(resultString, type);
                        if (jsonObject.getInt("qrCodeType") == 4) {
                            if (eventList != null) {
                              getProductDetails(eventList.getProductItemViewModel());
                            } else {
                                showApplicationClosedPupup();
                                Toast.makeText(getContext(), "No Item Available on this type ", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            showBottomPopup(eventList);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (json instanceof JSONArray) {
                Toast.makeText(getContext(), "No Item Available on this type", Toast.LENGTH_SHORT).show();
                showApplicationClosedPupup();
            } else {
                Toast.makeText(getContext(), "No Item Available on this type", Toast.LENGTH_SHORT).show();
                showApplicationClosedPupup();
            }


        } else {
            Toast.makeText(getContext(), "No Item Available on this type", Toast.LENGTH_SHORT).show();
            showApplicationClosedPupup();
        }
    }

    private void showBottomPopup(TextItemQRModel textItemQRModel) {

        textDetailsContainer.setVisibility(View.VISIBLE);
        productsDetailsRoot.setVisibility(View.GONE);
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            textInfoView.setText(textItemQRModel.getTextDescription());
        }

        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            textInfoView.setText(textItemQRModel.getTextDescription());
        }
    }

    private void showBottomPopup(ProductItemModelClass textItemQRModel) {
        textDetailsContainer.setVisibility(View.GONE);
        productsDetailsRoot.setVisibility(View.VISIBLE);
        productsDetailsRoot.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
            intent.putExtra("PRODUCT_DETAILS_ITEM", textItemQRModel);
            intent.putExtra(ProductDetailsActivity.PRODUCT_DESCRIPTION_KEY, (Serializable) textItemQRModel.getDescription());
            intent.putExtra(ProductDetailsActivity.PRODUCT_SPECIFIATION_KEY, (Serializable) textItemQRModel.getSpecification());
            getContext().startActivity(intent);
        });
        if (textItemQRModel != null) {
            List<String> imageUrls = textItemQRModel.getImageUrl();
            if (imageUrls.size() > 0)
                Picasso.get().load(imageUrls.get(0)).into(productImageView);

            productname.setText(textItemQRModel.getProductName());
            producPrice.setText(String.valueOf(textItemQRModel.getPrice()));
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

    }

    private void openCamera() {
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);
    }

    private void resumeCamera() {
        qrCodeScanner.resumeCameraPreview(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera();
        }
    }


    private void showApplicationClosedPupup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.confirm_connection_popup_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        // reference the textview of custom_popup_dialog
        TextView confirmMessageView = customView.findViewById(R.id.confirmMessage);
        ImageView profileImage = customView.findViewById(R.id.confirmUserProfile);
        profileImage.setImageResource(R.drawable.redhorizon_icon);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        confirmButton.setVisibility(View.VISIBLE);
        declineButton.setVisibility(View.GONE);
        confirmButton.setText("OK");
        String confirmMessgae = "<font color='red'>" + "No Item availble for this type" + "</font>";
        confirmMessageView.setText(Html.fromHtml(confirmMessgae));
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                openCamera();
            }
        });


        alertDialog.show();
    }

    private void getProductDetails(String productId) {
        RestApiFactory.cteateService(PrductDetailsApi.class).fetchProductDetial(productId)
                .enqueue(new Callback<ProductItemModelClass>() {
                    @Override
                    public void onResponse(Call<ProductItemModelClass> call, Response<ProductItemModelClass> response) {
                        assert !response.isSuccessful() || response.body() != null;
                        showBottomPopup(response.body());
                    }

                    @Override
                    public void onFailure(Call<ProductItemModelClass> call, Throwable t) {
                        String errorMessage = t.getMessage();

                    }
                });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void qrCodeScannerTabIntaction();
    }
}

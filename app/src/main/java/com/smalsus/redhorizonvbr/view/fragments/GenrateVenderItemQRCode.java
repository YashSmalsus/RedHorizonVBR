package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.ProductItemAdapter;
import com.smalsus.redhorizonvbr.databinding.ProductItemListBinding;
import com.smalsus.redhorizonvbr.datasource.factory.SocialNewsFeedViewModelFactory;
import com.smalsus.redhorizonvbr.interfaces.ProductItemClickListner;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.model.TextItemQRModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.utils.QRCodeHelper;
import com.smalsus.redhorizonvbr.viewmodel.ProductItemViewModel;

import java.io.ByteArrayOutputStream;

public class GenrateVenderItemQRCode extends Fragment implements ProductItemClickListner {

    private OnListFragmentInteractionListener mListener;
    private ProductItemListBinding productItemListBinding;
    private ProductItemAdapter productItemAdapter;
    private ProductItemViewModel productItemViewModel;

    public GenrateVenderItemQRCode() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GenrateVenderItemQRCode newInstance(int columnCount) {
        GenrateVenderItemQRCode fragment = new GenrateVenderItemQRCode();
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

        productItemListBinding = DataBindingUtil.inflate(inflater, R.layout.product_item_list, container, false);
        productItemListBinding.productList.setLayoutManager(new LinearLayoutManager(getContext()));
        productItemAdapter = new ProductItemAdapter(getContext());
        productItemAdapter.setProductItemClickListner(this);
        productItemListBinding.productList.setAdapter(productItemAdapter);

        productItemListBinding.searchTextEditView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        return productItemListBinding.getRoot();
    }

    private void performSearch() {
        productItemListBinding.searchTextEditView.clearFocus();
        InputMethodManager in = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(productItemListBinding.searchTextEditView.getWindowToken(), 0);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserInfo token = HRpreference.getInstance(getContext()).getUserInfo();
        productItemViewModel = ViewModelProviders.of(this, new SocialNewsFeedViewModelFactory(token.getAuthToken(), false, token.getId())).get(ProductItemViewModel.class);
        productItemViewModel.getArticleLiveData().observe(this, socialNewsFeedViewModels -> {
            productItemAdapter.submitList(socialNewsFeedViewModels);

        });

        productItemViewModel.getNetworkState().observe(this, networkState -> {
            productItemAdapter.setNetworkState(networkState);
        });
        // TODO: Use the ViewModel
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void productitemClicked(ProductItemModelClass productItem) {
        TextItemQRModel textItemQRModel = new TextItemQRModel("", 4, productItem.get_id());
        String encryptedString = new Gson().toJson(textItemQRModel);
        new QRCodeGenration().execute(encryptedString);

//        Intent intent=new Intent(getContext(), ProductDetailsActivity.class);
//        intent.putExtra("PRODUCT_DETAILS_ITEM",productItem);
//        intent.putExtra(ProductDetailsActivity.PRODUCT_DESCRIPTION_KEY, (Serializable) productItem.getDescription());
//        intent.putExtra(ProductDetailsActivity.PRODUCT_SPECIFIATION_KEY, (Serializable) productItem.getSpecification());
//        getContext().startActivity(intent);
    }

    private void share_bitMap_to_Apps(Bitmap bitmap) {
        productItemListBinding.progressUpdate.setVisibility(View.GONE);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
    /*compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();*/

        i.putExtra(Intent.EXTRA_STREAM, getImageUri(getContext(), bitmap));
        try {
            startActivity(Intent.createChooser(i, "Share via.."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }


    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction();
    }

    private class QRCodeGenration extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            return QRCodeHelper
                    .newInstance(getContext())
                    .setContent(params[0])
                    .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                    .setMargin(2)
                    .getQRCOde();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            share_bitMap_to_Apps(result);
        }

        @Override
        protected void onPreExecute() {
            productItemListBinding.progressUpdate.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}

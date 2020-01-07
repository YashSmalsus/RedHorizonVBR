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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.RedHorizonValidator;
import com.smalsus.redhorizonvbr.databinding.FragmentGenerateTextQRCode;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.model.TextItemQRModel;
import com.smalsus.redhorizonvbr.utils.QRCodeHelper;
import com.smalsus.redhorizonvbr.viewmodel.ProductItemViewModel;

import java.io.ByteArrayOutputStream;

public class GenerateTextItemQrCode extends Fragment implements
        AdapterView.OnItemSelectedListener {
    private OnFragmentInteractionListener mListener;
    private FragmentGenerateTextQRCode fragmentGenerateTextQRCode;
    private int type;

    private String[] country = {"Text", "Email", "Website", "Product"};

    public GenerateTextItemQrCode() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GenerateTextItemQrCode newInstance(String param1, String param2) {
        GenerateTextItemQrCode fragment = new GenerateTextItemQrCode();
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

        fragmentGenerateTextQRCode = DataBindingUtil.inflate(inflater, R.layout.fragment_generate_text_qrcode, container, false);

        fragmentGenerateTextQRCode.textItemQrCodeType.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentGenerateTextQRCode.textItemQrCodeType.setAdapter(aa);
        fragmentGenerateTextQRCode.generateCode.setOnClickListener(view -> {
            genrateQRCode();
        });

        return fragmentGenerateTextQRCode.getRoot();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void genrateQRCode() {
        String textInfo = fragmentGenerateTextQRCode.textInfoView.getText().toString();

        if (!RedHorizonValidator.isEmpty(textInfo)) {
            TextItemQRModel textItemQRModel = new TextItemQRModel(textInfo, type,"");
            String encryptedString = new Gson().toJson(textItemQRModel);
            new QRCodeGenration().execute(encryptedString);

        }


    }

    public void share_bitMap_to_Apps(Bitmap bitmap) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
    /*compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();*/

        i.putExtra(Intent.EXTRA_STREAM, getImageUri(getContext(), bitmap));
        try {
            startActivity(Intent.createChooser(i, "My Profile ..."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        type = i + 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            fragmentGenerateTextQRCode.textItemQrCodeType.setVisibility(View.GONE);
            share_bitMap_to_Apps(result);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            fragmentGenerateTextQRCode.textItemQrCodeType.setVisibility(View.VISIBLE);
        }
    }
}

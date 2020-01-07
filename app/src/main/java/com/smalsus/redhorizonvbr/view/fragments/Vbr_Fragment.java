package com.smalsus.redhorizonvbr.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.GetEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Vbr_Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_OPEN = 12;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WebView mWebview;
    private ImageButton ppt_btn, doc_btn, pdf_btn, image_btn;
    private List<GetEvent> eventDetail;
    private OnFragmentInteractionListener mListener;
    final int SELECT_IMAGE = 1234;
    public Vbr_Fragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static Vbr_Fragment newInstance(String param1, String param2) {
        Vbr_Fragment fragment = new Vbr_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDetail = new ArrayList<>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vbr_, container, false);
        ppt_btn = view.findViewById(R.id.ppt_btn);
        doc_btn = view.findViewById(R.id.doc_btn);
        pdf_btn = view.findViewById(R.id.pdf_btn);
        image_btn = view.findViewById(R.id.image_btn);
        ppt_btn.setOnClickListener(this);
        doc_btn.setOnClickListener(this);
        pdf_btn.setOnClickListener(this);
        image_btn.setOnClickListener(this);
        mWebview = view.findViewById(R.id.webView);
        mWebview.setBackgroundColor(Color.BLACK);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setWebViewClient(new WebViewController());
        return view;
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onClick(View view) {

        if (view == ppt_btn) {
            mWebview.loadUrl("https://docs.google.com/gview?embedded=true&url=http://myurl.com/mySlide.ppt");
            image_btn.setBackgroundResource(R.drawable.unactive_img);
            doc_btn.setBackgroundResource(R.drawable.unactive_doc);
            pdf_btn.setBackgroundResource(R.drawable.unactive_pdf);
            ppt_btn.setBackgroundResource(R.drawable.active_ppt);


        } else if (view == image_btn) {

            image_btn.setBackgroundResource(R.drawable.active_img);
            doc_btn.setBackgroundResource(R.drawable.unactive_doc);
            pdf_btn.setBackgroundResource(R.drawable.unactive_pdf);
            ppt_btn.setBackgroundResource(R.drawable.unactive_ppt);


            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);

        } else if (view == doc_btn) {
//            mWebview.loadUrl("https://docs.google.com/gview?embedded=true&url=http://myurl.com/mySlide.ppt");
            image_btn.setBackgroundResource(R.drawable.unactive_img);
            doc_btn.setBackgroundResource(R.drawable.active_doc);
            pdf_btn.setBackgroundResource(R.drawable.unactive_pdf);
            ppt_btn.setBackgroundResource(R.drawable.unactive_ppt);

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(intent, REQUEST_CODE_OPEN);



        } else if (view == pdf_btn) {
            mWebview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=http://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf");
            image_btn.setBackgroundResource(R.drawable.unactive_img);
            doc_btn.setBackgroundResource(R.drawable.unactive_doc);
            pdf_btn.setBackgroundResource(R.drawable.pdf);
            ppt_btn.setBackgroundResource(R.drawable.unactive_ppt);


        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

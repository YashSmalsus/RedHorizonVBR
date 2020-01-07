package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.ConsumerStaplesListAdapter;
import com.smalsus.redhorizonvbr.adapters.CroudelAdapter;
import com.smalsus.redhorizonvbr.databinding.ConsumerStaplesBinding;
import com.smalsus.redhorizonvbr.model.MyCartProductItemModel;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.ProductItemModelClass;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.rest.GetMyCart;
import com.smalsus.redhorizonvbr.rest.RestApiFactory;
import com.smalsus.redhorizonvbr.view.activities.MyCartScreen;
import com.smalsus.redhorizonvbr.view.activities.ProductDetailsActivity;
import com.smalsus.redhorizonvbr.viewmodel.ConsumerStapesViewModel;

import java.util.ArrayList;
import java.util.List;
import com.smalsus.redhorizonvbr.model.ConsumerStaplesModel;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smalsus.redhorizonvbr.view.activities.ProductDetailsActivity.MY_CART_PRODUCT_ITEM;


public class ConsumerStaples extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ConsumerStaplesBinding consumerStaplesBinding;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ConsumerStapesViewModel consumerStapesViewModel;
    private ConsumerStaplesListAdapter consumerStaplesListAdapter;
    private List<ProductItemModelClass> cartProductItemModel;


    public ConsumerStaples() {
        // Required empty public constructor
    }


    public static ConsumerStaples newInstance() {

        return new ConsumerStaples();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        consumerStaplesBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_consumer_staples,container,false);
        init();
        consumerStaplesListAdapter=new ConsumerStaplesListAdapter(getContext());
        consumerStaplesBinding.consumerStaplesList.setLayoutManager(new LinearLayoutManager(getContext()));
        consumerStaplesBinding.consumerStaplesList.setAdapter(consumerStaplesListAdapter);
        consumerStaplesBinding.consumerStaplesList.setNestedScrollingEnabled(true);
        consumerStaplesBinding.myCartBtn.setOnClickListener(view -> {
           goToMyCartScreen();
        });
        return consumerStaplesBinding.getRoot();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        consumerStapesViewModel= ViewModelProviders.of(this).get(ConsumerStapesViewModel.class);
        getConsumerStaples();
        getNetworkState();
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfo userInfo= HRpreference.getInstance(getContext()).getUserInfo();
        getMyCartProduct(userInfo.getAuthToken());
    }

    private void getConsumerStaples(){
        consumerStapesViewModel.getStaples().observe(this, consumerStaples -> {
            consumerStaplesListAdapter.updateEmployeeListItems(consumerStaples);
        });
    }
    private void getNetworkState(){
        consumerStapesViewModel.getNetworkState().observe(this, networkState -> {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                consumerStaplesBinding.progressBar.setVisibility(View.VISIBLE);
            } else {
                consumerStaplesBinding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                Toast.makeText(getContext(),networkState.getMsg(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMyCartItemCount() {
        consumerStaplesBinding.cartCountView.setText(String.valueOf(cartProductItemModel.size()));

    }
    private void goToMyCartScreen() {
        Intent intent = new Intent(getContext(), MyCartScreen.class);
        intent.putParcelableArrayListExtra(MY_CART_PRODUCT_ITEM, (ArrayList<? extends Parcelable>) cartProductItemModel);
        startActivity(intent);
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void init() {
        consumerStaplesBinding.imageSlider.setAdapter(new CroudelAdapter(getContext()));
        consumerStaplesBinding.indicator.setViewPager(consumerStaplesBinding.imageSlider);
       NUM_PAGES =4;
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                consumerStaplesBinding.imageSlider.setCurrentItem(currentPage++, true);

            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

    }
}

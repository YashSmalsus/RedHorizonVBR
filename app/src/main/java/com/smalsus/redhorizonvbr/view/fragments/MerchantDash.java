package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.FragmentMerchantDashBinding;
import com.smalsus.redhorizonvbr.view.activities.GenrateQRCodeActivity;

import java.util.ArrayList;
import java.util.List;

public class MerchantDash extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FragmentMerchantDashBinding merchantDashBinding;

    public MerchantDash() {
        // Required empty public constructor
    }

    public static MerchantDash newInstance() {

        return new MerchantDash();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        merchantDashBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_merchant_dash,container,false);
        setupViewPager();
        merchantDashBinding.merchanttabBar.setupWithViewPager(merchantDashBinding.merchantDashViewPager);
        return merchantDashBinding.getRoot();
    }

    private void setupViewPager() {
       Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new ConsumerStaples(), "Consumer Staples");
        adapter.addFragment(new ConsumerDiscrationary(), "Consumer Discreationary");
        merchantDashBinding.merchantDashViewPager.setAdapter(adapter);

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

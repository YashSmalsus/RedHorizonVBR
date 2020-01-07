package com.smalsus.redhorizonvbr.view.activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.ActivityGenrateQRCodeBinding;
import com.smalsus.redhorizonvbr.view.fragments.GenerateTextItemQrCode;
import com.smalsus.redhorizonvbr.view.fragments.GenrateVenderItemQRCode;

import java.util.ArrayList;
import java.util.List;

public class GenrateQRCodeActivity extends AppCompatActivity implements GenerateTextItemQrCode.OnFragmentInteractionListener, GenrateVenderItemQRCode.OnListFragmentInteractionListener {

    private ActivityGenrateQRCodeBinding activityGenrateQRCodeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGenrateQRCodeBinding = DataBindingUtil.setContentView(this, R.layout.activity_genrate_qrcode);
        setupViewPager();
        activityGenrateQRCodeBinding.qrCodeScannerPageTababar.setupWithViewPager(activityGenrateQRCodeBinding.qrCodeScannerViewPager);

    }

    private void setupViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new GenerateTextItemQrCode(), "Text");
        adapter.addFragment(new GenrateVenderItemQRCode(), "Item");
        activityGenrateQRCodeBinding.qrCodeScannerViewPager.setAdapter(adapter);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction() {

    }

    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

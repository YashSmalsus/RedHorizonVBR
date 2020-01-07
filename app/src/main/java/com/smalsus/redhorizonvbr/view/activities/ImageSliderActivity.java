package com.smalsus.redhorizonvbr.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.SlidingImage_Adapter;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class ImageSliderActivity extends AppCompatActivity {
    private ViewPager mPager;
    public static final String IMAGE_SLIDER_LIST="image_slider_list";
    private List<String> ImagesArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        Intent intent=getIntent();
        ImagesArray = (List<String>) intent.getSerializableExtra(IMAGE_SLIDER_LIST);
        init();
    }

    private void init() {
        mPager = findViewById(R.id.imageSlider);
        mPager.setAdapter(new SlidingImage_Adapter(ImageSliderActivity.this, ImagesArray));
        CircleIndicator indicator =  findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }

}
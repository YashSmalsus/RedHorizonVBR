package com.smalsus.redhorizonvbr.view.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smalsus.redhorizonvbr.R;

public class SearchEventActivity extends CoreBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        FloatingActionButton fab = findViewById(R.id.fab);



    }

}


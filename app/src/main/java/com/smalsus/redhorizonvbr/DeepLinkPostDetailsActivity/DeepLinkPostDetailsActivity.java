package com.smalsus.redhorizonvbr.DeepLinkPostDetailsActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;

public class DeepLinkPostDetailsActivity extends AppCompatActivity
{
    private ImageButton heartsLikes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link_post_details);

        heartsLikes = findViewById(R.id.heartsLikes);
    }
}

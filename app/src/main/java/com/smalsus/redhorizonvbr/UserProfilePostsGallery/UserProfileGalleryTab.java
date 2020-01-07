package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.UserProfileGalleryTabBinding;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.utils.RecycleItemDecoration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserProfileGalleryTab extends Fragment
{

    private UserProfileGalleryTabBinding userProfileGalleryTabBinding;

    public String userId;
    public int screenType;

    public UserProfileGalleryTab(String userId, int screenType) {
        this.userId = userId;
        this.screenType = screenType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        userProfileGalleryTabBinding = DataBindingUtil.inflate(inflater, R.layout.tab_gallery, container, false);
        String token = HRpreference.getInstance(getContext()).getUserInfo().getAuthToken();
        getImageURLs(userId, token);
        userProfileGalleryTabBinding.galleryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        userProfileGalleryTabBinding.galleryRecyclerView.addItemDecoration(new RecycleItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.galler_posts_divider),
                getResources().getInteger(R.integer.posts_images_preview_columns)));
        return userProfileGalleryTabBinding.getRoot();
    }

    public void getImageURLs(String userId,String token)
    {
        EventRequest request = new EventRequest();
        request.requestForGalleryImages(userId, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    Type listType = new TypeToken<GalleryMedia>() {
                    }.getType();
                    GalleryMedia galleryMediaList = new Gson().fromJson(myresponse, listType);
                    getActivity().runOnUiThread(() -> {
                        GalleryRecyclerViewAdapter galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(getContext(), galleryMediaList.getImageUrl());
                        userProfileGalleryTabBinding.galleryRecyclerView.setAdapter(galleryRecyclerViewAdapter);
                    });
                }
            }
        });
    }

}

package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GalleryMedia implements Serializable
{
    @SerializedName("ImageUrl")
    private List<String> ImageUrl;
    @SerializedName("VideoUrl")
    private List<String> VideoUrl;

    public GalleryMedia(List<String> imageUrl, List<String> videoUrl) {
        ImageUrl = imageUrl;
        VideoUrl = videoUrl;
    }

    public List<String> getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        ImageUrl = imageUrl;
    }

    public List<String> getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(List<String> videoUrl) {
        VideoUrl = videoUrl;
    }
}

package com.smalsus.redhorizonvbr;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class GlideUtils {


    public static void loadImage(final Context activity, String imageUrl, final ImageView imageView, final int placeholder, final int errorPlaceHolder) {
        String imageURL = imageUrl != null ? imageUrl.trim() : "";
        Glide.with(activity).load(imageURL).apply(RequestOptions.fitCenterTransform().dontAnimate().placeholder(placeholder).error(errorPlaceHolder)).thumbnail(0.5f)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }

                })
                .into(imageView);
    }

    public static void loadImageWithoutplaceHolder(final Context activity, String imageUrl, final ImageView imageView) {
        String imageURL = imageUrl != null ? imageUrl.trim() : "";
        Glide.with(activity).load(imageURL).apply(RequestOptions.fitCenterTransform().dontAnimate()).thumbnail(0.5f)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }

                })
                .into(imageView);
    }

}
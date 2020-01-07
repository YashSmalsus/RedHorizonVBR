package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.GalleryImageDetailsActivityBinding;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GalleryImageDetailsActivity extends AppCompatActivity {

    private GalleryImageDetailsActivityBinding galleryImageDetailsActivityBinding;
    public String galleryImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryImageDetailsActivityBinding = DataBindingUtil.setContentView(GalleryImageDetailsActivity.this, R.layout.activity_gallery_image_details);
        galleryImageURL = getIntent().getExtras().getString("galleryImageURL");
        Picasso.get().load(galleryImageURL).into(galleryImageDetailsActivityBinding.galleryPostImage);
        galleryImageDetailsActivityBinding.galleryPostsToolbar.setTitle("");
        setSupportActionBar(galleryImageDetailsActivityBinding.galleryPostsToolbar);

        galleryImageDetailsActivityBinding.backToUserProfileScreenActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.gallery_posts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.saveToPhone:
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(galleryImageURL);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                Long reference = downloadManager.enqueue(request);
                Toast.makeText(GalleryImageDetailsActivity.this, "Post Downloaded", Toast.LENGTH_SHORT).show();
                break;
            case R.id.shareToExternal:
                shareImage(galleryImageURL,GalleryImageDetailsActivity.this);
        }
        return true;
    }

     public void shareImage(String url,Context context) {
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                Uri uri=FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",getLocalBitmapFile(bitmap,context));
                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }
     public File getLocalBitmapFile(Bitmap bmp, Context context) {
         File file2 = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            file2=file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file2;
    }

}


package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolder>
{
    private Context context;
    private List<String> list;

    public GalleryRecyclerViewAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.gallery_images_items, parent, false);
        return new GalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position)
    {
        //Picasso.get().load(list.get(position).getImageURL()).into(holder.imageView);
        //holder.imageView.setImageResource(list.get(position).getImageURL());
        Picasso.get().load(list.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GalleryImageDetailsActivity.class);
                        intent.putExtra("galleryImageURL", list.get(position));
                        context.startActivity(intent);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image);
        }
    }
}

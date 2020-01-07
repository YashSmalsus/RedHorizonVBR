package com.smalsus.redhorizonvbr.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.UserLocation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.FollowersListViewHolder>
{

    private Context context;
    private List<EventUser> list;

    public FollowersListAdapter(Context context, List<EventUser> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FollowersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.followers_list_item, parent, false);
        return new FollowersListViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersListViewHolder holder, int position) {
        holder.userFirstName.setText(list.get(position).getfName());
        holder.userLastName.setText(list.get(position).getlName());
        if (list.get(position).getLocationMap() != null) {
            UserLocation userLocation = list.get(position).getLocationMap();
            String userLocationText = (userLocation.getUserCity() != null ? userLocation.getUserCity() : " ") + ((userLocation.getUserCountry() != null ? ", " + userLocation.getUserCountry() : " "));
            holder.userLocation.setText(userLocationText);
        }
        Picasso.get().load(list.get(position).getImageUrl()).into(holder.circularImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FollowersListViewHolder extends RecyclerView.ViewHolder
    {
        TextView userFirstName, userLastName, userLocation;
        CircularImageView circularImageView;
        public FollowersListViewHolder(@NonNull View itemView) {
            super(itemView);
            userFirstName = itemView.findViewById(R.id.followersFollowingUserName);
            userLastName = itemView.findViewById(R.id.followersUserID);
            userLocation = itemView.findViewById(R.id.followersUserLocation);
            circularImageView = itemView.findViewById(R.id.followersUserProfilePicture);
        }
    }
}

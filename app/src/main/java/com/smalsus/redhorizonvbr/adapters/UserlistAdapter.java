package com.smalsus.redhorizonvbr.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.UserLocation;
import com.smalsus.redhorizonvbr.model.Userlist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserlistAdapter extends RecyclerView.Adapter<UserlistAdapter.ViewHolder> {
    private Activity context;
    private List<Userlist> list;
    private ItemClickListener mClickListener;

    public UserlistAdapter(Activity context, List<Userlist> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.suggested_firend_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StringBuilder userName = new StringBuilder().append(list.get(position).getfName()).append(" ").append(list.get(position).getlName());
        holder.username.setText(userName.toString());
        Picasso.get().load(list.get(position).getImageUrl()).resize(50, 50).into(holder.userprofilepic);
        if (list.get(position).getLocationMap() != null) {
            UserLocation userLocation = list.get(position).getLocationMap();
            String userLocationText = (userLocation.getUserCity() != null ? userLocation.getUserCity() : " ") + ((userLocation.getUserCountry() != null ? ", " + userLocation.getUserCountry() : " "));
            holder.userLocation.setText(userLocationText);
        }

        holder.addFriend.setOnClickListener(view -> {
            if (mClickListener != null)
                mClickListener.onItemClick(view, list.get(position).getId(), "thor@gmail.com");
        });
    }

    public void updateList(List<Userlist> listsearch) {
        list = listsearch;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getItem(int id) {
        return list.get(id).getId();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {

        void onItemClick(View view, String Id, String email);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username, userLocation;
        private ImageView userprofilepic;
        private RelativeLayout mainlayout;
        private Button addFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            userLocation = itemView.findViewById(R.id.userLocation);
            userprofilepic = itemView.findViewById(R.id.userprofilepic);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            addFriend = itemView.findViewById(R.id.addFriendBtn);

        }
    }
}

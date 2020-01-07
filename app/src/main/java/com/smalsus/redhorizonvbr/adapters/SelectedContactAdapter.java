package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.SelectedContactsItemsInformation;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.model.Userlist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.SelectedContactViewHolder> {
    private Context context;
    private List<SelectedContactsItemsInformation> list;

    public SelectedContactAdapter(Context context, List<SelectedContactsItemsInformation> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SelectedContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.add_event_contact_added_item_list, parent, false);
        return new SelectedContactViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedContactViewHolder holder, int position) {
        Picasso.get().load(list.get(position).getImageURL()).into(holder.circularImageView);
        holder.contactName.setText(String.format("%s %s", list.get(position).getFirstName(), list.get(position).getLastName()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SelectedContactViewHolder extends RecyclerView.ViewHolder
    {
        CircularImageView circularImageView;
        TextView contactName;
        public SelectedContactViewHolder(@NonNull View itemView) {
            super(itemView);
            circularImageView = itemView.findViewById(R.id.selectedContactUserProfileImage);
            contactName = itemView.findViewById(R.id.selectedContactName);
        }
    }
}

package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.UserLocation;
import com.smalsus.redhorizonvbr.view.activities.AddEventScreen;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Add_Attendeee_Adapter extends BaseSelectableListAdapter<EventUser> implements Serializable{

    private boolean flag = false;

    private Add_Attendeee_Adapter.SelectedItemsCountsChangedListener selectedItemsCountChangedListener;

    public Add_Attendeee_Adapter(Context context, List<EventUser> users) {
        super(context, users);
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {

        final CreategroupAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.userlistitem, null);
            holder = new CreategroupAdapter.ViewHolder();
            holder.opponentIcon =  convertView.findViewById(R.id.userprofilepic);
            holder.opponentName =  convertView.findViewById(R.id.username);
            holder.selectimage=convertView.findViewById(R.id.selectimage);
            holder.opponentLocation=convertView.findViewById(R.id.userLocation);
            /*holder.recyclerView = convertView.findViewById(R.id.selectedContactRecyclerView);*/
            holder.textView = convertView.findViewById(R.id.selectedContactName);
            holder.circularImageView = convertView.findViewById(R.id.selectedContactUserProfileImage);
            holder.cardView = convertView.findViewById(R.id.selectedContactCardViewContainer);
            holder.imageButton = convertView.findViewById(R.id.removeFromSelectedContactsList);
            convertView.setTag(holder);
        } else {
            holder = (CreategroupAdapter.ViewHolder) convertView.getTag();
        }
        final EventUser user = getItem(position);


        if (user != null) {
            StringBuilder userName=new StringBuilder().append(user.getfName()).append(" ").append(user.getlName());
            holder.opponentName.setText(userName.toString());
            Picasso.get().load(user.getImageUrl()).into(holder.opponentIcon);
            if(user.getLocationMap()!=null){
                UserLocation userLocation=user.getLocationMap();
                String userLocationText=(userLocation.getUserCity()!=null?userLocation.getUserCity():" ")+((userLocation.getUserCountry()!=null?", "+userLocation.getUserCountry():" "));
                holder.opponentLocation.setText(userLocationText);
            }


            if (selectedItems.contains(user)) {
                StringBuilder contactName = new StringBuilder().append(user.getfName()).append(" ").append(user.getlName());
                holder.selectimage.setVisibility(View.VISIBLE);
                holder.cardView.setVisibility(View.GONE);
                holder.textView.setText(contactName);
                Picasso.get().load(user.getImageUrl()).into(holder.circularImageView);
            }
            else {
                convertView.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.selectimage.setVisibility(View.GONE);
                holder.cardView.setVisibility(View.GONE);
            }
        }

        convertView.setOnClickListener(v -> {
            Add_Attendeee_Adapter.this.toggleSelection(position);
            selectedItemsCountChangedListener.onCountSelectedItemsChanged(selectedItems.size());
        });

        return convertView;
    }

    public static class ViewHolder {
        ImageView opponentIcon,selectimage;
        TextView opponentName,opponentLocation;
        /*RecyclerView recyclerView;*/
        TextView textView;
        CircularImageView circularImageView;
        ImageButton imageButton;
        CardView cardView;
    }

    public void setSelectedItemsCountsChangedListener(Add_Attendeee_Adapter.SelectedItemsCountsChangedListener selectedItemsCountsChanged) {
        if (selectedItemsCountsChanged != null) {
            this.selectedItemsCountChangedListener = selectedItemsCountsChanged;
        }
    }

    public interface SelectedItemsCountsChangedListener {
        void onCountSelectedItemsChanged(int count);
    }

    public void addValueInSelectedItem(EventUser list){
            selectedItems.add(list);
    }
}

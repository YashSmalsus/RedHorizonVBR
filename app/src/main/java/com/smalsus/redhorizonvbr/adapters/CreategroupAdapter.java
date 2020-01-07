package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.UserLocation;
import com.smalsus.redhorizonvbr.model.Userlist;

import java.util.List;

public class CreategroupAdapter extends BaseSelectableListAdapter<Userlist> {

    private CreategroupAdapter.SelectedItemsCountsChangedListener selectedItemsCountChangedListener;

    public CreategroupAdapter(Context context, List<Userlist> users) {
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


            convertView.setTag(holder);
        } else {
            holder = (CreategroupAdapter.ViewHolder) convertView.getTag();
        }

        final Userlist user = getItem(position);

        if (user != null) {
            StringBuilder userName=new StringBuilder().append(user.getfName()).append(" ").append(user.getlName());
            holder.opponentName.setText(userName.toString());
            GlideUtils.loadImage(context,user.getImageUrl(), holder.opponentIcon, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
            if(user.getLocationMap()!=null){
                UserLocation userLocation=user.getLocationMap();
                String userLocationText=(userLocation.getUserCity()!=null?userLocation.getUserCity():" ")+((userLocation.getUserCountry()!=null?", "+userLocation.getUserCountry():" "));
                holder.opponentLocation.setText(userLocationText);
            }

            if (selectedItems.contains(user)) {
                holder.selectimage.setVisibility(View.VISIBLE);
            } else {
                convertView.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.selectimage.setVisibility(View.GONE);

            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CreategroupAdapter.this.toggleSelection(position);
                selectedItemsCountChangedListener.onCountSelectedItemsChanged(selectedItems.size());
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        ImageView opponentIcon,selectimage;
        TextView opponentName,opponentLocation;
        RecyclerView recyclerView;
        TextView textView;
        CircularImageView circularImageView;
        ImageButton imageButton;
        CardView cardView;
    }

    public void setSelectedItemsCountsChangedListener(CreategroupAdapter.SelectedItemsCountsChangedListener selectedItemsCountsChanged) {
        if (selectedItemsCountsChanged != null) {
            this.selectedItemsCountChangedListener = selectedItemsCountsChanged;
        }
    }

    public interface SelectedItemsCountsChangedListener {
        void onCountSelectedItemsChanged(int count);
    }

}

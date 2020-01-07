package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.interfaces.AddCallParticipantsListner;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.UserLocation;

import java.util.List;

public class AddParticipantsAdapter extends BaseSelectableListAdapter<EventUser> {

    private AddCallParticipantsListner selectedItemsCountChangedListener;

    public AddParticipantsAdapter(Context context, List<EventUser> users) {
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

        final EventUser user = getItem(position);

        if (user != null) {
            StringBuilder userName=new StringBuilder().append(user.getfName()).append(" ").append(user.getlName());
            holder.opponentName.setText(userName.toString());
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

        convertView.setOnClickListener(v -> {
            selectedItemsCountChangedListener.addCallerClicked(user);
        });

        return convertView;
    }


    public void setSelectedItemsCountsChangedListener(AddCallParticipantsListner selectedItemsCountsChanged) {
        if (selectedItemsCountsChanged != null) {
            this.selectedItemsCountChangedListener = selectedItemsCountsChanged;
        }
    }

}

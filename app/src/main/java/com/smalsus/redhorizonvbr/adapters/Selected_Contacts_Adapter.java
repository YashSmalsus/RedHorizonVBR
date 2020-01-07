package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Selected_Contacts_Adapter extends BaseSelectableListAdapter<EventUser>
{

    public Selected_Contacts_Adapter(Context context, List<EventUser> objectsList) {
        super(context, objectsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Selected_Contacts_Adapter.SelectedContactsViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.add_event_contact_added_item_list, null);
            holder = new Selected_Contacts_Adapter.SelectedContactsViewHolder();
            holder.selectdContactProfilePicture =  convertView.findViewById(R.id.userprofilepic);
            holder.selectedContactName =  convertView.findViewById(R.id.username);
            convertView.setTag(holder);
        } else {
            holder = (Selected_Contacts_Adapter.SelectedContactsViewHolder) convertView.getTag();
        }

        final EventUser user = getItem(position);

        if (user != null)
        {
            String name = user.getfName() + " " + user.getlName();
            String userImageURL = user.getImageUrl();
            holder.selectedContactName.setText(name);
            Picasso.get().load(userImageURL).into(holder.selectdContactProfilePicture);
        }
        return null;
    }

    public static class SelectedContactsViewHolder
    {
        TextView selectedContactName;
        CircularImageView selectdContactProfilePicture;
    }
}

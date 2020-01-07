package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.Contact_Model;

import java.util.ArrayList;

public class Contact_Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<Contact_Model> arrayList;
    private OnSendInvitation onSendInvitation;

    public Contact_Adapter(Context context, ArrayList<Contact_Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setSendInvitaionClick(OnSendInvitation onSendInvitation) {
        this.onSendInvitation = onSendInvitation;
    }

    @Override
    public int getCount() {

        return arrayList.size();
    }

    @Override
    public Contact_Model getItem(int position) {

        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Contact_Model model = arrayList.get(position);
        ViewHodler holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_list_item_layout, parent, false);
            holder = new ViewHodler();
            holder.contactImage = convertView
                    .findViewById(R.id.contactImage);
            holder.contactName = convertView
                    .findViewById(R.id.contactName);
            holder.contactEmail = convertView
                    .findViewById(R.id.contactEmail);
            holder.contactNumber = convertView
                    .findViewById(R.id.contactNumber);
            holder.contactOtherDetails = convertView
                    .findViewById(R.id.contactOtherDetails);
            holder.sendInvite = convertView.findViewById(R.id.sendInvite);


            convertView.setTag(holder);
        } else {
            holder = (ViewHodler) convertView.getTag();
        }

        // Set items to all view
        if (!model.getContactName().equals("")
                && model.getContactName() != null) {
            holder.contactName.setText(model.getContactName());
        } else {
            holder.contactName.setText(context.getString(R.string.no_name_hint));
        }
        if (!model.getContactEmail().equals("")
                && model.getContactEmail() != null) {
            holder.contactEmail.setText(model.getContactEmail());
        } else {
            holder.contactEmail.setText(" ");
        }

        if (!model.getContactNumber().equals("")
                && model.getContactNumber() != null) {
            holder.contactNumber.setText(model.getContactNumber());
        } else {
            holder.contactNumber.setText(context.getString(R.string.no_contact_number));
        }

        if (!model.getContactOtherDetails().equals("")
                && model.getContactOtherDetails() != null) {
            holder.contactOtherDetails.setText(model.getContactOtherDetails());
        } else {
            holder.contactOtherDetails.setText(" ");
        }

        Bitmap image = null;
        if (!model.getContactPhoto().equals("")
                && model.getContactPhoto() != null) {
            image = BitmapFactory.decodeFile(model.getContactPhoto());// decode
            if (image != null)
                holder.contactImage.setImageBitmap(image);// Set image if bitmap
                // is not null
            else {
                image = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.defaultuser);// if bitmap is null then set
                holder.contactImage.setImageBitmap(image);
            }
        } else {
            image = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.defaultuser);
            holder.contactImage.setImageBitmap(image);
        }

        holder.sendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onSendInvitation!=null){
                    onSendInvitation.onClickSendInvitation(model.getContactNumber());
                }
            }
        });
        return convertView;
    }

    // View holder to hold views
    private class ViewHodler {
        ImageView contactImage;
        TextView contactName, contactNumber, contactEmail, contactOtherDetails;
        Button sendInvite;
    }

    public interface OnSendInvitation {
        void onClickSendInvitation(String toContact);
    }


}

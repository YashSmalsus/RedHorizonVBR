package com.smalsus.redhorizonvbr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AttendeesSelectorAdaptor extends RecyclerView.Adapter<AttendeesSelectorAdaptor.AttendeesSelectorViewHolder>
{

    private List<EventUser> list;
    private Context context;
    private onSelectAttendeeContactListener onSelectAttendeeContactListener;
    public static boolean isSelected = false;

    public AttendeesSelectorAdaptor(List<EventUser> list, Context context) {
        this.list = list;
        this.context = context;
        this.onSelectAttendeeContactListener = ((com.smalsus.redhorizonvbr.onSelectAttendeeContactListener) context);
    }

    @NonNull
    @Override
    public AttendeesSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View attendeeItem = layoutInflater.inflate(R.layout.attendees_item, parent, false);
        return new AttendeesSelectorViewHolder(attendeeItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeesSelectorViewHolder holder, int position) {
        Picasso.get().load(list.get(position).getImageUrl()).into(holder.circularImageView);
        holder.textView.setText(String.format("%s %s", list.get(position).getfName(), list.get(position).getlName()));

        holder.circularImageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelectedItems(list.get(position).getId());
                        //holder.imageView.setVisibility(View.VISIBLE);
                        //onSelectAttendeeContactListener.onGetSelectedAttendeeUserName(list.get(position));
                    }
                }
        );
    }

    public void setSelectedItems(String userID)
    {
        ArrayList<String> stringArrayList = new ArrayList<>();
        if (!stringArrayList.contains(userID))
        {
            stringArrayList.add(userID);
        }
        else
        {
            stringArrayList.add("Already Exists");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class AttendeesSelectorViewHolder extends RecyclerView.ViewHolder
    {
        private CircularImageView circularImageView;
        private TextView textView;
        private ImageView imageView;
        private View view;
        public AttendeesSelectorViewHolder(@NonNull View itemView) {
            super(itemView);
            circularImageView = itemView.findViewById(R.id.attendeesCircularImageView);
            textView = itemView.findViewById(R.id.attendeesName);
            imageView = itemView.findViewById(R.id.removeAttendee);
        }
    }
}

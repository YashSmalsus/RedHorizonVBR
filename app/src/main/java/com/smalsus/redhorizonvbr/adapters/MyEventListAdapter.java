package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.GetEvent;

import java.util.List;

public class MyEventListAdapter extends RecyclerView.Adapter<MyEventListAdapter.ViewHolder> {
    private String[] mData;
    private LayoutInflater mInflater;
    private List<GetEvent> eventdetails;
    private ItemClickListener mClickListener;

    //    private ItemClickListener mClickListener;
    // data is passed into the constructor
    public MyEventListAdapter(Context context, List<GetEvent> eventList) {
        this.eventdetails = eventList;
        this.mInflater = LayoutInflater.from(context);
//        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public MyEventListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.myeventitem, parent, false);
        return new MyEventListAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull MyEventListAdapter.ViewHolder holder, int position) {
        holder.eventname.setText(eventdetails.get(position).getName());
        holder.member.setText("No of Participate - " + eventdetails.get(position).getEventmember().size());
        String[] parts = eventdetails.get(position).getStartDate().split("T");
        holder.place.setText(eventdetails.get(position).getLocation());
        String part1 = parts[0]; // 004
        String part2 = parts[1];
        holder.date.setText(part1);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view,position,eventdetails);

            }
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return eventdetails.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventname, member, date, place;
        LinearLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            eventname = itemView.findViewById(R.id.eventname);
            member = itemView.findViewById(R.id.members);
            date = itemView.findViewById(R.id.date);
            place = itemView.findViewById(R.id.place);
            layout = itemView.findViewById(R.id.layout);

        }


    }

    //    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData[id];
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, List<GetEvent> eventdetails);
    }

}

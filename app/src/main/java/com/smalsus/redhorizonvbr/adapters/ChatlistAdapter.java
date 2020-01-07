package com.smalsus.redhorizonvbr.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.ChatModelClass;

import java.util.List;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.ViewHolder> {
    private Activity context;
    private List<ChatModelClass> list;


    public ChatlistAdapter(Activity context, List<ChatModelClass> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ChatlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.chatlist_item_view, parent, false);
        return new ChatlistAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatlistAdapter.ViewHolder holder, int position) {
        String userId = HRpreference.getInstance(context.getApplicationContext()).getUserInfo().getId();
        ChatModelClass dataItem = getItem(position);
        if (userId.equals(dataItem.getUserBy().getId())) {
            holder.outGoingTextView.setVisibility(View.VISIBLE);
            holder.outGoingTextView.setText(dataItem.getText());
            holder.incomingTextView.setVisibility(View.GONE);
        } else {
            holder.outGoingTextView.setVisibility(View.GONE);
            holder.incomingTextView.setVisibility(View.VISIBLE);
            holder.incomingTextView.setText(dataItem.getText());
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public ChatModelClass getItem(int position) {
        return list.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView incomingTextView, outGoingTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            incomingTextView = itemView.findViewById(R.id.text_message_incoming);
            outGoingTextView = itemView.findViewById(R.id.textMessageOutGoing);

        }
    }


}
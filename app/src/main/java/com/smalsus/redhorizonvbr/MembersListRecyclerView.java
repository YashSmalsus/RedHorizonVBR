package com.smalsus.redhorizonvbr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.adapters.MemberList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MembersListRecyclerView extends RecyclerView.Adapter<MembersListRecyclerView.MembersListViewHolder>
{

    private Context context;
    private List<MemberList> list;

    public MembersListRecyclerView(Context context, List<MemberList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MembersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.event_members_list_item, parent, false);
        return new MembersListViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersListViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getName());
        Picasso.get().load(list.get(position).getImage()).into(holder.circularImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MembersListViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        CircularImageView circularImageView;
        public MembersListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.event_member_user_name);
            circularImageView = itemView.findViewById(R.id.event_member_image);
        }
    }
}

package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.GroupModel;
import com.smalsus.redhorizonvbr.util.GroupListDiffCallback;
import com.squareup.picasso.Picasso;


import java.util.List;

public class GrouplistAdapter extends RecyclerView.Adapter<GrouplistAdapter.ViewHolder> {
    private Context context;
    private List<GroupModel> list;
    private ItemClickListener mClickListener;

    public GrouplistAdapter(Context context, List<GroupModel> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public GrouplistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.group_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    public void setDatalist(List<GroupModel> newData){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GroupListDiffCallback(this.list, newData));
        diffResult.dispatchUpdatesTo(this);
        list.clear();
        this.list.addAll(newData);
    }

    @Override
    public void onBindViewHolder(final GrouplistAdapter.ViewHolder holder, int position) {
        holder.groupName.setText(list.get(position).getName());
        if(list.get(position).getEventUser()!=null)
        holder.admin_name.setText(String.format("BY : %s", list.get(position).getEventUser().getfName().toUpperCase()));
        holder.groupMemberSize.setText(String.format("%s USER", String.valueOf(list.get(position).getEventmember().size())));
        if (list.get(position).getEventmember().size() >= 2) {
            Picasso.get().load(list.get(position).getEventmember().get(0).getImageUrl()).into(holder.firstUser);
            Picasso.get().load(list.get(position).getEventmember().get(1).getImageUrl()).into(holder.seconduser);
        }
        holder.mainlayout.setOnClickListener(view -> {
            if (mClickListener != null) mClickListener.onItemClick(position,1);
        });
        holder.showMoreInfo.setOnClickListener(view -> {
            if (mClickListener != null) mClickListener.onItemClick(position,2);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupName, admin_name, groupMemberSize;
        private View view;
        private ImageView firstUser, seconduser,showMoreInfo;

        LinearLayout mainlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            groupName = itemView.findViewById(R.id.groupName);
            admin_name = itemView.findViewById(R.id.groupAdmin);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            groupMemberSize = itemView.findViewById(R.id.userItemCount);
            firstUser = itemView.findViewById(R.id.firstUserGroup);
            seconduser = itemView.findViewById(R.id.secondUserGroup);
            showMoreInfo=itemView.findViewById(R.id.showMoreInfo);
        }
    }

    public String getItem(int id) {
        return list.get(id).getName();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    // type 1 for ChatScreen  2 for More Info
    public interface ItemClickListener {
        void onItemClick(int position,int type);
    }
}

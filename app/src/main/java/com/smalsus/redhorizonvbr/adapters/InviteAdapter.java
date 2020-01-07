package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.InviteForm;
import com.smalsus.redhorizonvbr.view.fragments.NotificationFragment;

import java.util.List;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private String[] mData;
    private LayoutInflater mInflater;
    private Context context;
    private List<InviteForm> inviteList;
    private List<InviteForm.Sender> senderList;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public InviteAdapter(Context context, List<InviteForm> inviteList, List<InviteForm.Sender> senderList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.inviteList = inviteList;
        this.senderList = senderList;

    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public InviteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.inviteitem, parent, false);
        return new InviteAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull InviteAdapter.ViewHolder holder, int position) {
        if (senderList.size() > 0) {
            holder.mainlayout_invite.setVisibility(View.VISIBLE);
            StringBuilder userName = new StringBuilder().append(senderList.get(position).getfName()).append(" ").append(senderList.get(position).getlName());
            holder.invitename.setText(userName.toString());
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(view, inviteList.get(position).getId());

                }
            });
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return inviteList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        Button accept, delete;
        TextView invitename;
        LinearLayout mainlayout_invite;

        ViewHolder(View itemView) {
            super(itemView);
            accept = itemView.findViewById(R.id.acceptbtn);
//            delete=itemView.findViewById(R.id.deletebtn);
//            delete.setOnClickListener(this);
            invitename = itemView.findViewById(R.id.invitename);
            mainlayout_invite = itemView.findViewById(R.id.mainlayout_invite);


        }
    }

    public String getItem(int id) {
        return inviteList.get(id).getId();
    }

    public void setClickListener(NotificationFragment itemClickListener) {
        this.mClickListener = (ItemClickListener) itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, String Id);
    }
}

package com.smalsus.redhorizonvbr.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.UserLocation;
import com.smalsus.redhorizonvbr.view.activities.InsallAppList;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private Activity context;
    private List<EventUser> list;
    private ContactItemClickListener mClickListener;
    private int previousPosition;

    public FriendListAdapter(Activity context, List<EventUser> list) {
        this.context = context;
        this.list = list;
    }

    public void updateEmployeeListItems(List<EventUser> list) {
        final ContactListDiffCallBack diffCallback = new ContactListDiffCallBack(this.list, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.list.clear();
        this.list.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }


    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.friendlist_item_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {

        StringBuilder userName = new StringBuilder().append(list.get(position).getfName()).append(" ").append(list.get(position).getlName());
        holder.username.setText(userName.toString());
        holder.userId.setText(list.get(position).getId());
        Picasso.get().load(list.get(position).getImageUrl()).into(holder.userprofilepic);
        if (list.get(position).getLocationMap() != null) {
            UserLocation userLocation = list.get(position).getLocationMap();
            String userLocationText = (userLocation.getUserCity() != null ? userLocation.getUserCity() : " ") + ((userLocation.getUserCountry() != null ? ", " + userLocation.getUserCountry() : " "));
            holder.userLocation.setText(userLocationText);
        }
        if (HRpreference.getInstance(context).getISCallProress()) {
          //  holder.voiceCallBtn.setEnabled(false);
          //  holder.videoCallBtn.setEnabled(false);
        }
        holder.mainlayout.setOnLongClickListener(view -> {
            if (mClickListener != null)
                mClickListener.onItemClick(list.get(position).getId(), list.get(position), 1, 1);
            return false;
        });
        holder.userprofilepic.setOnClickListener(view -> {

            if (mClickListener != null)
                mClickListener.onItemClick(list.get(position).getId(), list.get(position), 2, 2);
        });

        holder.voiceCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)
                    mClickListener.onItemClick(list.get(position).getId(), list.get(position), 1, 2);
            }
        });

        holder.videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)
                    mClickListener.onItemClick(list.get(position).getId(), list.get(position), 1, 1);
            }
        });

        holder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)
                    mClickListener.onItemClick(list.get(position).getId(), list.get(position), 1, 3);
            }
        });

        holder.emailButton.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(View v) {
                        /*Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                        context.startActivity(intent);*/
                        String[] CC = {""};
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:?"));
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_CC, CC);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        if(list.get(position).isExpanded()){
            TransitionManager.beginDelayedTransition(holder.tContainer);
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.relativeLayout.setVisibility(View.VISIBLE);
            //layoutParams.leftMargin = 5;
            //layoutParams.rightMargin = 5;
            //holder.cardView.setElevation(8);
            //holder.cardView.setLayoutParams(layoutParams);
            holder.expandCollapse.setImageResource(R.drawable.contact_buttons_collapse);
        }else{
            TransitionManager.beginDelayedTransition(holder.tContainer);
            holder.linearLayout.setVisibility(View.GONE);
            holder.relativeLayout.setVisibility(View.GONE);
            holder.expandCollapse.setImageResource(R.drawable.contact_buttons_expand);
        }

        holder.mainlayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(previousPosition!=position){
                            list.get(previousPosition).setExpanded(false);
                            notifyItemChanged(previousPosition);
                            previousPosition=position;
                        }

                        if(list.get(position).isExpanded()){
                            list.get(position).setExpanded(false);
                            notifyItemChanged(position);
                        }else {
                            list.get(position).setExpanded(true);
                            notifyItemChanged(position);
                        }
                    }
                }
        );

        holder.expandCollapse.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(previousPosition!=position){
                                list.get(previousPosition).setExpanded(false);
                                notifyItemChanged(previousPosition);
                                previousPosition=position;
                            }

                            if(list.get(position).isExpanded()){
                                list.get(position).setExpanded(false);
                                notifyItemChanged(position);
                            }else {
                                list.get(position).setExpanded(true);
                                notifyItemChanged(position);
                            }
                        }
                    }
            );
        /*SharedPreferences sharedPreferences = context.getSharedPreferences("friendsListCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("numberOfFriends", String.valueOf(list.size()));
        editor.apply();*/
        }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getItem(int id) {
        return list.get(id).getfName();
    }

    // allows clicks events to be caught
    public void setClickListener(FriendListAdapter.ContactItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ContactItemClickListener {
        void onItemClick(String userID, EventUser eventUser, int id, int callType);
    }

    // parent activity will implement this method to respond to click events

    // callType  1 for audio , 2 - video , 3 - chat

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ImageView userprofilepic, voiceCallBtn, videoCallBtn, chatBtn, emailButton;
        ImageView expandCollapse, collapseButton;
        RelativeLayout mainlayout, relativeLayout;
        LinearLayout linearLayout, tContainer;
        private TextView username, userId, userLocation;
        private CardView cardView;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            username = itemView.findViewById(R.id.username);
            userLocation = itemView.findViewById(R.id.userLocation);
            userId = itemView.findViewById(R.id.userID);
            checkbox = itemView.findViewById(R.id.checkbox);
            userprofilepic = itemView.findViewById(R.id.userprofilepic);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            videoCallBtn = itemView.findViewById(R.id.videoCallButton);
            voiceCallBtn = itemView.findViewById(R.id.audioCallButton);
            linearLayout = itemView.findViewById(R.id.actionButtonView);
            chatBtn = itemView.findViewById(R.id.chatButton);
            expandCollapse = itemView.findViewById(R.id.expandButton);
            tContainer = itemView.findViewById(R.id.transitionContainer);
            cardView = itemView.findViewById(R.id.contacts_list_card_view_container);
            relativeLayout = itemView.findViewById(R.id.contacts_container_separator);
            emailButton = itemView.findViewById(R.id.emailButton);
        }
    }


    public class ContactListDiffCallBack extends DiffUtil.Callback {

        private final List<EventUser> mOldEmployeeList;
        private final List<EventUser> mNewEmployeeList;

        public ContactListDiffCallBack(List<EventUser> oldEmployeeList, List<EventUser> newEmployeeList) {
            this.mOldEmployeeList = oldEmployeeList;
            this.mNewEmployeeList = newEmployeeList;
        }

        @Override
        public int getOldListSize() {
            return mOldEmployeeList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewEmployeeList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldEmployeeList.get(oldItemPosition).getId() == mNewEmployeeList.get(
                    newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final EventUser oldEmployee = mOldEmployeeList.get(oldItemPosition);
            final EventUser newEmployee = mNewEmployeeList.get(newItemPosition);

            return oldEmployee.getId().equals(newEmployee.getId());
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }

}
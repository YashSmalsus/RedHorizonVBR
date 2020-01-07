package com.smalsus.redhorizonvbr.UserProfilePostsGallery;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.FollowersListViewHolder> implements Filterable
{
    private Context context;
    private List<EventUser> list;
    private List<String> namesList;
    private List<EventUser> listFull;

    public FollowersListAdapter(Context context, List<EventUser> list, List<String> namesList) {
        this.context = context;
        this.list = list;
        this.namesList = namesList;
        listFull = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public FollowersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.follower_item, parent, false);
        return new FollowersListViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersListViewHolder holder, int position) {
        holder.fullName.setText(String.format("%s %s", list.get(position).getfName(), list.get(position).getlName()));
        holder.userName.setText(namesList.get(position));
        Picasso.get().load(list.get(position).getImageUrl()).into(holder.userProfilePicture);
        holder.unFriendTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String token = HRpreference.getInstance(context.getApplicationContext()).getUserInfo().getAuthToken();
                        EventRequest eventRequest  = new EventRequest();
                        eventRequest.unFriendUser(list.get(position).getId(), token, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("onFailure", "onFailure");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.code() == 200)
                                {
                                    Log.i("Friend unFriended", "friend unfriended");
                                }
                                else
                                {
                                    Log.i("unsuccessful", "unsuccessful");
                                }
                            }
                        });
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<EventUser> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(listFull);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (EventUser item : listFull)
                {
                    if (item.getUserName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    class FollowersListViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName, userName, unFriendTextView;
        CircularImageView userProfilePicture;
        public FollowersListViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.followersFullNameTextView);
            userName = itemView.findViewById(R.id.followerUserNameTextView);
            userProfilePicture = itemView.findViewById(R.id.followerProfilePictureCircularImageView);
            unFriendTextView = itemView.findViewById(R.id.unFriendTextView);
        }
    }
}

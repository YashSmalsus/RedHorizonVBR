package com.smalsus.redhorizonvbr.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.DeepLinkPostDetailsActivity.DeepLinkPostDetailsActivity;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.FeedItemBinding;
import com.smalsus.redhorizonvbr.databinding.NetworkItemBinding;
import com.smalsus.redhorizonvbr.interfaces.NewsFeedItemClick;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
import com.smalsus.redhorizonvbr.view.activities.CreateSocialPostScreen;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class MyFeedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<SocialFeedModel> feedModelList;
    private String userId;

    private NetworkState networkState;
    private NewsFeedItemClick newsFeedItemClick;

    public MyFeedListAdapter(Context context, List<SocialFeedModel> feedModelList, String userId) {
        this.context = context;
        this.feedModelList = feedModelList;
        this.userId = userId;
    }

    public void updateListItem(List<SocialFeedModel> list) {
        final NewsFeedListDiffCallBack diffCallback = new NewsFeedListDiffCallBack(this.feedModelList, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.feedModelList.clear();
        this.feedModelList.addAll(list);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            NetworkItemBinding headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
            return new MyFeedListAdapter.NetworkStateItemViewHolder(headerBinding);

        } else {
            FeedItemBinding itemBinding = FeedItemBinding.inflate(layoutInflater, parent, false);
            return new MyFeedListAdapter.ArticleItemViewHolder(itemBinding);
        }
    }

    public void setNewsFeedItemClickListner(NewsFeedItemClick newsFeedItemClick) {
        this.newsFeedItemClick = newsFeedItemClick;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyFeedListAdapter.ArticleItemViewHolder) {
            ((MyFeedListAdapter.ArticleItemViewHolder) holder).bindTo(getItem(position), position);

        } else {
            ((MyFeedListAdapter.NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }

    private SocialFeedModel getItem(int position) {
        return feedModelList.get(position);
    }


    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return feedModelList.size();
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }


    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {

        private FeedItemBinding binding;

        ArticleItemViewHolder(FeedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(SocialFeedModel article, int position) {
            binding.itemImage.setVisibility(View.VISIBLE);
            binding.itemDesc.setVisibility(View.VISIBLE);
            String author = article.getBy() == null ? context.getString(R.string.author_name) : article.getBy().getfName() + " " + article.getBy().getlName();
            binding.itemTitle.setText(author);
            binding.itemTime.setText(String.format(context.getString(R.string.item_date), CalenderUtils.getDate(article.getCreatedAt()), CalenderUtils.getTime(article.getCreatedAt())));
            binding.itemDesc.setText(article.getText());
            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                Picasso.get().load(article.getImageUrl()).into(binding.itemImage);
            } else {
                binding.itemImage.setVisibility(View.GONE);
            }
            Picasso.get().load(article.getBy().getImageUrl()).into(binding.itemProfileImg);
            if (article.getLikesCount() > 0) {
                binding.likesCountView.setText(String.valueOf(article.getLikesCount()));
                binding.likeCountContainer.setVisibility(View.GONE);
            } else {
                binding.likeCountContainer.setVisibility(View.GONE);
            }
            //likes_count_viewlikes_count_viewlikes_count_viewlikes_count_viewlikes_count_view

            if (article.getCommentCount() > 0) {
                binding.commentsCountView.setText(String.format("%s", String.valueOf(article.getCommentCount())));
                binding.commentsCountView.setVisibility(View.VISIBLE);
            } else
                binding.commentsCountView.setVisibility(View.GONE);


            binding.commentsButton.setOnClickListener(view -> {
                if (newsFeedItemClick != null) {
                    newsFeedItemClick.newsFeedItemClicked(article, 1, position);
                }
            });
            if (article.getLikebyUser() == 0) {
                binding.likeButton.setImageResource(R.drawable.hearts_hollow);
                //binding.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                //binding.likeButton.setTextColor(context.getResources().getColor(R.color.title_txt));
            } else {
                binding.likeButton.setImageResource(R.drawable.hearts_filled);
                //binding.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked, 0, 0, 0);
                //binding.likeButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

            /*binding.likeButtonContainer.setOnClickListener(view -> {
                newsFeedItemClick.newsFeedItemClicked(article, 2, position);
            });*/

            /*UserInfo token = HRpreference.getInstance(context).getUserInfo();
            SharedPreferences sharedPreferences = context.getSharedPreferences("LoggedInUserIdentification", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("userID", "");*/
            UserInfo token = HRpreference.getInstance(context).getUserInfo();
            binding.socialMoreOptions.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            String[] userMoreMyPostOptions = {"Edit Post", "Delete Post"};
                            String[] friendsMorePostOptions = {"Report Post"};
                            if(userId.equals(token.getId()))
                            {
                                builder.setItems(userMoreMyPostOptions, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which)
                                        {
                                            case 0:
                                                String postText = article.getText();
                                                String postImageURL = article.getImageUrl();
                                                Intent intent = new Intent(context, CreateSocialPostScreen.class);
                                                String userId = article.getId();
                                                intent.putExtra("postText", postText);
                                                intent.putExtra("postImageURL", postImageURL);
                                                intent.putExtra("actionToBePerformed", 2);
                                                intent.putExtra("userId", userId);
                                                context.startActivity(intent);
                                                break;
                                            case 1:
                                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                                String confirm = "YES";
                                                String decline = "NO";
                                                SpannableString spannableString = new SpannableString(confirm);
                                                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, confirm.length(), 0);
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                builder1.setTitle("Delete post?");
                                                builder1.setMessage("Are you sure that you want delete this post?");
                                                builder1.setPositiveButton(spannableStringBuilder.append(spannableString), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        SharedPreferences sharedPreferences = context.getSharedPreferences("userPostIDInformation", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("userPostId", article.getId());
                                                        editor.apply();
                                                        newsFeedItemClick.newsFeedItemClicked(article, 3, position);
                                                    }
                                                });
                                                builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                AlertDialog alertDialog = builder1.create();
                                                alertDialog.show();
                                                break;
                                        }
                                    }
                                });
                            }
                            else
                            {
                                builder.setItems(friendsMorePostOptions, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which)
                                        {
                                            case 0:
                                                break;
                                        }
                                    }
                                });
                            }
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
            );

            binding.shareButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, DeepLinkPostDetailsActivity.class);
                            context.startActivity(intent);
                        }
                    }
            );
        }
    }

    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private NetworkItemBinding binding;

        NetworkStateItemViewHolder(NetworkItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.errorMsg.setText(networkState.getMsg());
            } else {
                binding.errorMsg.setVisibility(View.GONE);
            }
        }
    }


    public class NewsFeedListDiffCallBack extends DiffUtil.Callback {

        private final List<SocialFeedModel> mOldEmployeeList;
        private final List<SocialFeedModel> mNewEmployeeList;

        public NewsFeedListDiffCallBack(List<SocialFeedModel> oldEmployeeList, List<SocialFeedModel> newEmployeeList) {
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
            return mOldEmployeeList.get(oldItemPosition).getId().equals(mNewEmployeeList.get(
                    newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final SocialFeedModel oldEmployee = mOldEmployeeList.get(oldItemPosition);
            final SocialFeedModel newEmployee = mNewEmployeeList.get(newItemPosition);

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

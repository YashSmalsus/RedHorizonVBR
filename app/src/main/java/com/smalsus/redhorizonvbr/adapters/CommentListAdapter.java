package com.smalsus.redhorizonvbr.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.CommentItemViewBinding;
import com.smalsus.redhorizonvbr.databinding.NetworkItemBinding;
import com.smalsus.redhorizonvbr.interfaces.NewsFeedItemClick;
import com.smalsus.redhorizonvbr.model.LikesModel;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommentListAdapter extends PagedListAdapter<SocialFeedModel, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private NetworkState networkState;
    private NewsFeedItemClick newsFeedItemClick;

    public CommentListAdapter(Context context) {
        super(SocialFeedModel.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            NetworkItemBinding headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
            return new CommentListAdapter.NetworkStateItemViewHolder(headerBinding);

        } else {
            CommentItemViewBinding itemBinding = CommentItemViewBinding.inflate(layoutInflater, parent, false);
            return new CommentListAdapter.ArticleItemViewHolder(itemBinding);
        }
    }

    public void setNewsFeedItemClickListner(NewsFeedItemClick newsFeedItemClick) {
        this.newsFeedItemClick = newsFeedItemClick;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentListAdapter.ArticleItemViewHolder) {
            ((CommentListAdapter.ArticleItemViewHolder) holder).bindTo(getItem(position), position);
        } else {
            ((CommentListAdapter.NetworkStateItemViewHolder) holder).bindView(networkState);
        }
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

    private void postNewsFeedLikes(String id) {
        String token = HRpreference.getInstance(context.getApplicationContext()).getUserInfo().getAuthToken();
        EventRequest eventRequest = new EventRequest();
        eventRequest.likePost(id, token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    //   mViewModel.updateListData();
                }

            }
        });

    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {

        private CommentItemViewBinding binding;

        ArticleItemViewHolder(CommentItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(SocialFeedModel article, int position) {
            String author = article.getBy() == null ? context.getString(R.string.author_name) : article.getBy().getfName() + " " + article.getBy().getlName();
            binding.itemTitle.setText(author);
            // binding.commentTime.setText(String.format(context.getString(R.string.item_date), CalenderUtils.getDate(article.getCreatedAt()), CalenderUtils.getTime(article.getCreatedAt())));
            Picasso.get().load(article.getBy().getImageUrl()).resize(50, 50).into(binding.itemProfileImg);
            binding.commentText.setText(article.getText());
            if (article.getLikes().size() > 0) {
                binding.commentLikeCountContainer.setVisibility(View.GONE);
                binding.commentLikesCountView.setText(String.valueOf(article.getLikes().size()));
            } else {
                binding.commentLikeCountContainer.setVisibility(View.GONE);
            }
            binding.commentLikeButton.setOnClickListener(view -> {
                postNewsFeedLikes(article.getId());
                if (getCurrentList() != null && getCurrentList().get(position) != null) {
                    ArrayList<LikesModel> likesModelArrayList = getCurrentList().get(position).getLikes();
                    likesModelArrayList.add(new LikesModel());
                    getCurrentList().get(position).setLikes(likesModelArrayList);
                    notifyItemChanged(position);
                }

            });
            UserInfo token = HRpreference.getInstance(context).getUserInfo();
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserID", Context.MODE_PRIVATE);
            String user_id = sharedPreferences.getString("userID", "");
            binding.deleteCommentImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String token_string = HRpreference.getInstance(context).getUserInfo().getAuthToken();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            String[] usersComments = {"Delete Comment"};
                            String[] friendsCommnents = {"Report Comment"};
                            if (user_id.equals(token.getId()))
                            {
                                builder.setItems(usersComments, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which)
                                        {
                                            case 0:
                                                deleteComment(getCurrentList().get(position).getId(), token_string);
                                                break;
                                        }
                                    }
                                });
                            }
                            else
                            {
                                builder.setItems(friendsCommnents, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which)
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
            String timeAgo = (String) DateUtils.getRelativeTimeSpanString(CalenderUtils.getTimeLong(article.getCreatedAt()));
            binding.commentTimeView.setText(timeAgo);
        }

    }

    public void deleteComment(String commentID, String token)
    {
        EventRequest eventRequest = new EventRequest();
        eventRequest.deleteUserPost(commentID, token, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e)
            {
                Toast.makeText(context, "Error deleting comment", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200)
                {

                }
            }
        });
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


}

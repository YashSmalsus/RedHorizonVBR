package com.smalsus.redhorizonvbr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.FeedItemBinding;
import com.smalsus.redhorizonvbr.databinding.NetworkItemBinding;
import com.smalsus.redhorizonvbr.interfaces.NewsFeedItemClick;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
import com.squareup.picasso.Picasso;

public class FeedListAdapter extends PagedListAdapter<SocialFeedModel, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private NetworkState networkState;
    private NewsFeedItemClick newsFeedItemClick;

    public FeedListAdapter(Context context) {
        super(SocialFeedModel.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            NetworkItemBinding headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
            return new NetworkStateItemViewHolder(headerBinding);

        } else {
            FeedItemBinding itemBinding = FeedItemBinding.inflate(layoutInflater, parent, false);
            return new ArticleItemViewHolder(itemBinding);
        }
    }

    public void setNewsFeedItemClickListner(NewsFeedItemClick newsFeedItemClick) {
        this.newsFeedItemClick = newsFeedItemClick;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArticleItemViewHolder) {
            ((ArticleItemViewHolder) holder).bindTo(getItem(position), position);


        } else {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
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


            if (article.getCommentCount() > 0) {
                binding.commentsCountView.setText(String.format("%s Comments", String.valueOf(article.getCommentCount())));
                binding.commentsCountView.setVisibility(View.VISIBLE);
                binding.likeCountContainer.setVisibility(View.GONE);
            } else
                binding.commentsCountView.setVisibility(View.GONE);


            binding.commentsButton.setOnClickListener(view -> {
                if (newsFeedItemClick != null) {
                    newsFeedItemClick.newsFeedItemClicked(article, 1, position);
                }
            });
            if(article.getLikebyUser()==0){
                binding.likeButton.setImageResource(R.drawable.hearts_hollow);
                //binding.likeButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.like, 0, 0, 0);
                //binding.likeButton.setTextColor(context.getResources().getColor(R.color.title_txt));
            }else{
                binding.likeButton.setImageResource(R.drawable.hearts_filled);
                //binding.likeButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.liked, 0, 0, 0);
                //binding.likeButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

            binding.likeButtonContainer.setOnClickListener(view -> {
                newsFeedItemClick.newsFeedItemClicked(article, 2, position);
            });
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
}

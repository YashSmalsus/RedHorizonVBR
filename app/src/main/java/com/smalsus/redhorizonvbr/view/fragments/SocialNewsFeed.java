package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.CommentListAdapter;
import com.smalsus.redhorizonvbr.adapters.FeedListAdapter;
import com.smalsus.redhorizonvbr.databinding.SocialNewsFeedBinding;
import com.smalsus.redhorizonvbr.datasource.factory.CommentListItemViewModelFactory;
import com.smalsus.redhorizonvbr.datasource.factory.SocialNewsFeedViewModelFactory;
import com.smalsus.redhorizonvbr.interfaces.NewsFeedItemClick;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.view.activities.CreateSocialPostScreen;
import com.smalsus.redhorizonvbr.viewmodel.CommentItemViewModel;
import com.smalsus.redhorizonvbr.viewmodel.SocialNewsFeedViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SocialNewsFeed extends Fragment implements NewsFeedItemClick {

    public static boolean isRefresh = false;
    private SocialNewsFeedViewModel mViewModel;
    private SocialNewsFeedBinding socialNewsFeedBinding;
    private FeedListAdapter adapter;
    private CommentItemViewModel commentItemViewModel;

    public static SocialNewsFeed newInstance() {
        return new SocialNewsFeed();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        socialNewsFeedBinding = DataBindingUtil.inflate(inflater, R.layout.social_news_feed_fragment, container, false);

        socialNewsFeedBinding.listFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        socialNewsFeedBinding.listFeed.setNestedScrollingEnabled(false);
        socialNewsFeedBinding.swipeRefreshLayout.setRefreshing(true);
        Picasso.get().load(HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getImageUrl()).into(socialNewsFeedBinding.userProfileImage);

        socialNewsFeedBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            //
            //  mViewModel.updateListData();
            socialNewsFeedBinding.swipeRefreshLayout.setRefreshing(false);
        });

        socialNewsFeedBinding.addPostlayout.setOnClickListener(view -> {
            Intent i2 = new Intent(getContext(), CreateSocialPostScreen.class);
            startActivity(i2);
            // getContext().a(R.anim.slide_in_up,R.anim.slide_out_up);
        });

        adapter = new FeedListAdapter(getContext());
        adapter.setNewsFeedItemClickListner(this);
        socialNewsFeedBinding.listFeed.setAdapter(adapter);
        return socialNewsFeedBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            mViewModel.updateListData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserInfo token = HRpreference.getInstance(getContext()).getUserInfo();
        mViewModel = ViewModelProviders.of(this, new SocialNewsFeedViewModelFactory(token.getAuthToken(),false,token.getId())).get(SocialNewsFeedViewModel.class);
        mViewModel.getArticleLiveData().observe(this, socialNewsFeedViewModels -> {
            adapter.submitList(socialNewsFeedViewModels);
            socialNewsFeedBinding.swipeRefreshLayout.setRefreshing(false);

        });

        mViewModel.getNetworkState().observe(this, networkState -> {
            adapter.setNetworkState(networkState);
        });
        // TODO: Use the ViewModel
    }

    public void onClickMakePost() {

    }


    @Override
    public void newsFeedItemClicked(SocialFeedModel article, int type, int position) {

        // 1 - Comment , 2 -  Likes
        if (type == 1) {
            onShowPopup(article.getId(), article.getLikes().size(), position);
        } else {
            if (article.getLikebyUser() == 0) {
                PagedList<SocialFeedModel> modelPagedList = adapter.getCurrentList();
                if (modelPagedList != null) {
                    int likeCount = modelPagedList.get(position).getLikesCount();
                    modelPagedList.get(position).setLikesCount(likeCount + 1);
                    modelPagedList.get(position).setLikebyUser(1);
                    postNewsFeedLikes(article.getId());
                    adapter.submitList(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            } else {
                PagedList<SocialFeedModel> modelPagedList = adapter.getCurrentList();
                if (modelPagedList != null) {
                    int likeCount = modelPagedList.get(position).getLikesCount();
                    modelPagedList.get(position).setLikesCount(likeCount - 1);
                    modelPagedList.get(position).setLikebyUser(0);
                    postNewsFeedLikes(article.getId());
                    adapter.submitList(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            }


        }

    }


    public void onShowPopup(String id, int likeCount, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = layoutInflater.inflate(R.layout.comment_popup_layout_file, null, false);
        RecyclerView listView = inflatedView.findViewById(R.id.commentsListView);
        LinearLayout likeCountContainer = inflatedView.findViewById(R.id.likeCountContainer);
        TextView likeCountsView = inflatedView.findViewById(R.id.likes_count_view);
        ImageButton closeBtn = inflatedView.findViewById(R.id.closePopupBtn);
        EditText inputCommentBox = inflatedView.findViewById(R.id.commentInputBox);
        ImageButton sendCommentButton = inflatedView.findViewById(R.id.sendCommentBtn);
        if (likeCount > 0)
            likeCountsView.setText(String.format("%d User like this Post ", likeCount));
        else
            likeCountContainer.setVisibility(View.GONE);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        String token = HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getAuthToken();
        CommentListAdapter commentListAdapter = new CommentListAdapter(getContext());
        listView.setAdapter(commentListAdapter);
        commentItemViewModel = ViewModelProviders.of(this, new CommentListItemViewModelFactory(token, id)).get(CommentItemViewModel.class);
        commentItemViewModel.init(token, id);
        commentItemViewModel.getArticleLiveData().observe(this, socialNewsFeedViewModels -> {
            commentListAdapter.submitList(socialNewsFeedViewModels);
        });
        final Point size = new Point();
        display.getSize(size);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 50, true);
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_grey_border_layout));
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.PopupAnimation);
        popWindow.showAtLocation(socialNewsFeedBinding.getRoot(), Gravity.BOTTOM, 0, 100);
        closeBtn.setOnClickListener(view -> popWindow.dismiss());
        popWindow.setOnDismissListener(() -> {
            if (commentListAdapter.getCurrentList() != null) {
                PagedList<SocialFeedModel> modelPagedList = adapter.getCurrentList();
                if (modelPagedList != null && modelPagedList.get(position) != null) {
                    modelPagedList.get(position).setCommentCount(commentListAdapter.getCurrentList().size());
                    adapter.submitList(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            }
            commentItemViewModel = null;
        });
        inputCommentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    sendCommentButton.setVisibility(View.VISIBLE);
                else {
                    sendCommentButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendCommentButton.setOnClickListener(view -> {
            postNewsFeedComment(id, inputCommentBox.getText().toString(), commentListAdapter);
            inputCommentBox.setText("");
        });

    }


    private void postNewsFeedComment(String id, String title, final CommentListAdapter commentListAdapter) {
        UserInfo userInfo = HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo();
        EventRequest eventRequest = new EventRequest();
        eventRequest.postSocialNewsFeedComment(id, title, "", " ", userInfo.getAuthToken(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {

//                  PagedList<SocialFeedModel> modelPagedList=  commentListAdapter.getCurrentList();
//                    SocialFeedModel socialFeedModel=new SocialFeedModel();
//                    socialFeedModel.setBy(userInfo);
//                    socialFeedModel.setText(title);
//                    modelPagedList.add(socialFeedModel);
//                    commentListAdapter.notifyItemInserted(modelPagedList.size());
                    commentItemViewModel.updateListData();


                }

            }
        });

    }


    private void postNewsFeedLikes(String id) {
        String token = HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getAuthToken();
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
}

package com.smalsus.redhorizonvbr.UserProfilePostsGallery;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.CommentListAdapter;
import com.smalsus.redhorizonvbr.adapters.MyFeedListAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityUserProfileScreenBinding;
import com.smalsus.redhorizonvbr.databinding.UserProfilePostsTabBinding;
import com.smalsus.redhorizonvbr.datasource.factory.CommentListItemViewModelFactory;
import com.smalsus.redhorizonvbr.interfaces.NewsFeedItemClick;
import com.smalsus.redhorizonvbr.model.NetworkState;
import com.smalsus.redhorizonvbr.model.SocialFeedModel;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.view.activities.CreateSocialPostScreen;
import com.smalsus.redhorizonvbr.view.activities.LoginScreen;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;
import com.smalsus.redhorizonvbr.viewmodel.CommentItemViewModel;
import com.smalsus.redhorizonvbr.viewmodel.MySocialFeedViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class UserProfilePostsTab extends Fragment implements NewsFeedItemClick

{

    public String userId;
    public int screenType;

    public UserProfilePostsTab(String userId, int screenType) {
        this.userId = userId;
        this.screenType = screenType;
    }

    /*Class Variables*/


    private List<SocialFeedModel> socialFeedModels;

    private MyFeedListAdapter adapter;
    private MySocialFeedViewModel mViewModel;
    private CommentItemViewModel commentItemViewModel;
    private UserProfilePostsTabBinding userProfilePostsTabBinding;

    private TextView friendsCount;

    public static boolean isRefreshSocialNewsFeeds = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Initializing the Data Binding Class*/
        userProfilePostsTabBinding = DataBindingUtil.inflate(inflater,R.layout.tab_posts,container,true);

        /*Initializing important Authentication Details*/
        UserInfo token = HRpreference.getInstance(getContext()).getUserInfo();


        /*Setting Up the Recycler View for Social News Feeds*/
        socialFeedModels = new ArrayList<>();

        adapter = new MyFeedListAdapter(getContext(), socialFeedModels, userId);
        adapter.setNewsFeedItemClickListner(this);
        userProfilePostsTabBinding.profilescreenListFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        userProfilePostsTabBinding.profilescreenListFeed.setAdapter(adapter);
        userProfilePostsTabBinding.profilescreenListFeed.setNestedScrollingEnabled(true);
        userProfilePostsTabBinding.profilescreenListFeed.setHasFixedSize(false);

        mViewModel = ViewModelProviders.of(this).get(MySocialFeedViewModel.class);

        mViewModel.getNewsFeedData(userId, token.getAuthToken()).observe(this, socialNewsFeedViewModels -> {
            adapter.updateListItem(socialNewsFeedViewModels);
            socialFeedModels = socialNewsFeedViewModels;
        });

        mViewModel.getNetworkState().observe(this, networkState -> {
            if (networkState.getStatus().equals(NetworkState.Status.SUCCESS) || networkState.getStatus().equals(NetworkState.Status.FAILED))
            adapter.setNetworkState(networkState);
        });

        if (userId.equals(token.getId()))
        {
            userProfilePostsTabBinding.cratePostFloatingActionButton.show();
        }
        else
        {
            userProfilePostsTabBinding.cratePostFloatingActionButton.hide();
        }

        userProfilePostsTabBinding.cratePostFloatingActionButton.setOnClickListener(view -> {
            Intent i2 = new Intent(getContext(), CreateSocialPostScreen.class);
            i2.putExtra("actionToBePerformed", 1);
            startActivity(i2);
        });


        return userProfilePostsTabBinding.getRoot();
    }

    @Override
    public void newsFeedItemClicked(SocialFeedModel article, int type, int position) {
        if (type == 1) {
            onShowPopup(article.getId(), article.getLikes().size(), position);
        } else if(type == 2) {
            if (article.getLikebyUser() == 0) {
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                if (modelPagedList != null) {
                    int likeCount = modelPagedList.get(position).getLikesCount();
                    modelPagedList.get(position).setLikesCount(likeCount + 1);
                    modelPagedList.get(position).setLikebyUser(1);
                    postNewsFeedLikes(article.getId());
                    adapter.updateListItem(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            } else {
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                if (modelPagedList != null) {
                    int likeCount = modelPagedList.get(position).getLikesCount();
                    modelPagedList.get(position).setLikesCount(likeCount - 1);
                    modelPagedList.get(position).setLikebyUser(0);
                    postNewsFeedLikes(article.getId());
                    adapter.updateListItem(modelPagedList);
                    adapter.notifyItemChanged(position);
                }
            }
        }
            else if (type == 3)
            {
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("userPostIDInformation", MODE_PRIVATE);
                String userToken = HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getAuthToken();
                deleteThePost(sharedPreferences.getString("userPostId", ""), userToken);
                if(modelPagedList.size() > 0)
                {
                    modelPagedList.remove(position);
                    List<SocialFeedModel> modelPagedList2 = modelPagedList;
                    adapter.updateListItem(modelPagedList2);
                    adapter.notifyItemRemoved(position);

                    Toast.makeText(getContext(), "Post Deleted", Toast.LENGTH_LONG).show();
                }
                /*adapter.updateListItem(modelPagedList);
                adapter.notifyItemChanged(position);*/
            }
        }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefreshSocialNewsFeeds)
        {

        }
        isRefreshSocialNewsFeeds = false;
    }

    /*Delete the post method*/
    private void  deleteThePost(String postId, String token)
    {
        EventRequest eventRequest = new EventRequest();
        eventRequest.deleteUserPost(postId, token, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Toast.makeText(getContext(), "Deletion Unsuccessful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Post Deleted", Toast.LENGTH_SHORT).show();
                            /*getActivity().finish();
                            getActivity().startActivity(getActivity().getIntent());*/
                        }
                    });
                }
            }
        });
    }

    private void showApplicationClosedPupup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.confirm_connection_popup_layout, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        TextView confirmMessageView = customView.findViewById(R.id.confirmMessage);
        ImageView profileImage = customView.findViewById(R.id.confirmUserProfile);
        profileImage.setImageResource(R.drawable.redhorizon_icon);
        TextView declineButton = customView.findViewById(R.id.declineButton);
        TextView confirmButton = customView.findViewById(R.id.confirmButton);
        String confirmMessgae = "<font color='red'>" + "Are you sure you want to Exit ?" + "</font>";
        confirmMessageView.setText(Html.fromHtml(confirmMessgae));
        declineButton.setOnClickListener(view -> alertDialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            alertDialog.dismiss();
            Intent logoutIntent = new Intent(getContext(), LoginScreen.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            HRpreference.getInstance(getContext()).clearAllData();
            startActivity(logoutIntent);

        });
        alertDialog.show();
    }

    public void onShowPopup(String id, int likeCount, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = layoutInflater.inflate(R.layout.comment_popup_layout_file, null, false);
        RecyclerView listView = inflatedView.findViewById(R.id.commentsListView);
        LinearLayout likeCountContainer = inflatedView.findViewById(R.id.likeCountContainer);
        TextView likeCountsView = inflatedView.findViewById(R.id.likes_count_view);
        ImageButton closeBtn = inflatedView.findViewById(R.id.closePopupBtn);
        EditText inputCommentBox = inflatedView.findViewById(R.id.commentInputBox);
        ImageButton sendCommentButton = inflatedView.findViewById(R.id.sendCommentBtn);
        if (likeCount > 0) {
            likeCountsView.setText(String.format("%d User like this Post ", likeCount));
        } else
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
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 50, true);
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_grey_border_layout));
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.PopupAnimation);
        popWindow.showAtLocation(userProfilePostsTabBinding.getRoot(), Gravity.BOTTOM, 0, 100);
        closeBtn.setOnClickListener(view -> popWindow.dismiss());
        popWindow.setOnDismissListener(() -> {
            if (commentListAdapter.getCurrentList() != null) {
                List<SocialFeedModel> modelPagedList = socialFeedModels;
                if (modelPagedList != null && modelPagedList.get(position) != null) {
                    modelPagedList.get(position).setCommentCount(commentListAdapter.getCurrentList().size());
                    adapter.updateListItem(modelPagedList);
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

    /*API method to post a new comment*/
    private void postNewsFeedComment(String id, String title, final CommentListAdapter commentListAdapter) {
        UserInfo userInfo = HRpreference.getInstance(getContext()).getUserInfo();
        EventRequest eventRequest = new EventRequest();
        eventRequest.postSocialNewsFeedComment(id, title, "", " ", userInfo.getAuthToken(), new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    commentItemViewModel.updateListData();
                }

            }
        });

    }

    /*API method to post news feeds likes*/
    private void postNewsFeedLikes(String id) {
        String token = HRpreference.getInstance(getContext()).getUserInfo().getAuthToken();
        EventRequest eventRequest = new EventRequest();
        eventRequest.likePost(id, token, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                String error = e.getLocalizedMessage();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    //   mViewModel.updateListData();
                }
            }
        });
    }
}

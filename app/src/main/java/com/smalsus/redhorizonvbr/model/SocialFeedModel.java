package com.smalsus.redhorizonvbr.model;


import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import java.util.ArrayList;

public class SocialFeedModel implements Parcelable{

    private String id;
    private String text;
    private String imageUrl;
    private String videoUrl;
    private ArrayList<String> children;
    private ArrayList<LikesModel> likes;
    private UserInfo by;
    private String createdAt;
    private String modifiedAt;
    private int likebyUser;
    private int likesCount;
    private int commentCount;


    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setChildren(ArrayList<String> children) {
        this.children = children;
    }

    public void setLikes(ArrayList<LikesModel> likes) {
        this.likes = likes;
    }

    public void setBy(UserInfo by) {
        this.by = by;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setLikebyUser(int likebyUser) {
        this.likebyUser = likebyUser;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public ArrayList<String> getChildren() {
        return children;
    }

    public ArrayList<LikesModel> getLikes() {
        return likes;
    }

    public UserInfo getBy() {
        return by;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public int getLikebyUser() {
        return likebyUser;
    }

    public static Creator<SocialFeedModel> getCREATOR() {
        return CREATOR;
    }

    protected SocialFeedModel(Parcel in) {
        id = in.readString();
        text = in.readString();
        imageUrl = in.readString();
        videoUrl = in.readString();
        children = in.createStringArrayList();
        likes = in.createTypedArrayList(LikesModel.CREATOR);
        createdAt = in.readString();
        modifiedAt = in.readString();
        likebyUser = in.readInt();
        commentCount=in.readInt();
        likesCount=in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(imageUrl);
        dest.writeString(videoUrl);
        dest.writeStringList(children);
        dest.writeTypedList(likes);
        dest.writeString(createdAt);
        dest.writeString(modifiedAt);
        dest.writeInt(likebyUser);
        dest.writeInt(likesCount);
        dest.writeInt(commentCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SocialFeedModel> CREATOR = new Creator<SocialFeedModel>() {
        @Override
        public SocialFeedModel createFromParcel(Parcel in) {
            return new SocialFeedModel(in);
        }

        @Override
        public SocialFeedModel[] newArray(int size) {
            return new SocialFeedModel[size];
        }
    };


    public static DiffUtil.ItemCallback<SocialFeedModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<SocialFeedModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull SocialFeedModel oldItem, @NonNull SocialFeedModel newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SocialFeedModel oldItem, @NonNull SocialFeedModel newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        SocialFeedModel article = (SocialFeedModel) obj;
        return article.getId().equals(this.id);
    }
}

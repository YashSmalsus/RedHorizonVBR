package com.smalsus.redhorizonvbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ProductItemModelClass implements Parcelable {
    private String vid;
    private String _id;
    private String ProductName;
    private int type;
    private List<String> imageUrl;
    private List<String> videoUrl;
    @SerializedName("description")
    @Expose
    private Map<String, String> description;
    @SerializedName("specification")
    @Expose
    private Map<String, String> specification;
    private float price;
    private int quantity;
    private String createdAt;
    private int category;
    private int subCategory;

    public ProductItemModelClass() {
    }

    public ProductItemModelClass(String vid, String _id, String productName, int type, List<String> imageUrl, List<String> videoUrl, Map<String, String> description, Map<String, String> specification, float price, int quantity, String createdAt, int category, int subCategory) {
        this.vid = vid;
        this._id = _id;
        ProductName = productName;
        this.type = type;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.description = description;
        this.specification = specification;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.category = category;
        this.subCategory = subCategory;
    }

    protected ProductItemModelClass(Parcel in) {
        vid = in.readString();
        _id = in.readString();
        ProductName = in.readString();
        type = in.readInt();
        imageUrl = in.createStringArrayList();
        videoUrl = in.createStringArrayList();
        price = in.readFloat();
        quantity = in.readInt();
        createdAt = in.readString();
        category = in.readInt();
        subCategory = in.readInt();
    }

    public static final Creator<ProductItemModelClass> CREATOR = new Creator<ProductItemModelClass>() {
        @Override
        public ProductItemModelClass createFromParcel(Parcel in) {
            return new ProductItemModelClass(in);
        }

        @Override
        public ProductItemModelClass[] newArray(int size) {
            return new ProductItemModelClass[size];
        }
    };

    public String getVid() {
        return vid;
    }

    public String get_id() {
        return _id;
    }

    public String getProductName() {
        return ProductName;
    }

    public int getType() {
        return type;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public List<String> getVideoUrl() {
        return videoUrl;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public Map<String, String> getSpecification() {
        return specification;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getCategory() {
        return category;
    }

    public int getSubCategory() {
        return subCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(vid);
        parcel.writeString(_id);
        parcel.writeString(ProductName);
        parcel.writeInt(type);
        parcel.writeStringList(imageUrl);
        parcel.writeStringList(videoUrl);
        parcel.writeFloat(price);
        parcel.writeInt(quantity);
        parcel.writeString(createdAt);
        parcel.writeInt(category);
        parcel.writeInt(subCategory);
    }

    public static DiffUtil.ItemCallback<ProductItemModelClass> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProductItemModelClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductItemModelClass oldItem, @NonNull ProductItemModelClass newItem) {
            return oldItem.get_id().equals(newItem.get_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductItemModelClass oldItem, @NonNull ProductItemModelClass newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        ProductItemModelClass article = (ProductItemModelClass) obj;
        return article.get_id().equals(this._id);
    }
}

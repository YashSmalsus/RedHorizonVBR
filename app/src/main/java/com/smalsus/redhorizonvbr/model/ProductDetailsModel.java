package com.smalsus.redhorizonvbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ProductDetailsModel implements Parcelable {

    public static final Creator<ProductDetailsModel> CREATOR = new Creator<ProductDetailsModel>() {
        @Override
        public ProductDetailsModel createFromParcel(Parcel in) {
            return new ProductDetailsModel(in);
        }

        @Override
        public ProductDetailsModel[] newArray(int size) {
            return new ProductDetailsModel[size];
        }
    };
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
    private List<ProductItemModelClass> relatedItem;

    public ProductDetailsModel(String vid, String _id, String productName, int type, List<String> imageUrl, List<String> videoUrl, Map<String, String> description, Map<String, String> specification, float price, int quantity, String createdAt, int category, int subCategory, List<ProductItemModelClass> relatedItem) {
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
        this.relatedItem = relatedItem;
    }

    protected ProductDetailsModel(Parcel in) {
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
        relatedItem = in.createTypedArrayList(ProductItemModelClass.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vid);
        dest.writeString(_id);
        dest.writeString(ProductName);
        dest.writeInt(type);
        dest.writeStringList(imageUrl);
        dest.writeStringList(videoUrl);
        dest.writeFloat(price);
        dest.writeInt(quantity);
        dest.writeString(createdAt);
        dest.writeInt(category);
        dest.writeInt(subCategory);
        dest.writeTypedList(relatedItem);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public List<ProductItemModelClass> getRelatedItem() {
        return relatedItem;
    }


}

package com.smalsus.redhorizonvbr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MyCartProductItemModel implements Parcelable {

    String uId ;
    String id;
    List<ProductItemModelClass> product;

    public String getuId() {
        return uId;
    }

    public String getId() {
        return id;
    }

    public List<ProductItemModelClass> getProduct() {
        return product;
    }

    public static Creator<MyCartProductItemModel> getCREATOR() {
        return CREATOR;
    }

    protected MyCartProductItemModel(Parcel in) {
        uId = in.readString();
        id = in.readString();
        product = in.createTypedArrayList(ProductItemModelClass.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uId);
        dest.writeString(id);
        dest.writeTypedList(product);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyCartProductItemModel> CREATOR = new Creator<MyCartProductItemModel>() {
        @Override
        public MyCartProductItemModel createFromParcel(Parcel in) {
            return new MyCartProductItemModel(in);
        }

        @Override
        public MyCartProductItemModel[] newArray(int size) {
            return new MyCartProductItemModel[size];
        }
    };
}

package com.smalsus.redhorizonvbr.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.AddressItemBinding;
import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;

import java.util.List;

public class AddressChoiceAdapter extends BaseSelectableListAdapter<UserDeliveryAddress> {
    private Context mContext;
    private List<UserDeliveryAddress> userDeliveryAddressList;
    private EditAddressItemListner addressItemListner;
    public AddressChoiceAdapter(Context context, List<UserDeliveryAddress> users) {
        super(context, users);
        this.mContext = context;
        this.userDeliveryAddressList = users;
    }
    public void setEditItemClickedListner(EditAddressItemListner addressItemListner){
        this.addressItemListner=addressItemListner;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final UserDeliveryAddress user = getItem(position);
        @SuppressLint("ViewHolder")
        AddressItemBinding addressItemLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.address_item_layout, parent, false);
        if (selectedItems.contains(user)) {
            addressItemLayoutBinding.selectedCheckBox.setChecked(true);
        } else {
            addressItemLayoutBinding.selectedCheckBox.setChecked(false);
        }
        String name = user.getName();
        String address = user.getBuildingName() + " , " + user.getArea();
        String locality = user.getCity() + " , " + user.getState() + " " + user.getPin_code();
        String phoneNumber = user.getPhone_number();
        addressItemLayoutBinding.userName.setText(name);
        addressItemLayoutBinding.userHome.setText(address);
        addressItemLayoutBinding.stateCity.setText(locality);
        addressItemLayoutBinding.number.setText(phoneNumber);
        addressItemLayoutBinding.editButton.setOnClickListener(view -> {
            if(addressItemListner!=null){
                addressItemListner.editAddressItemClicked(user);
            }
        });

        addressItemLayoutBinding.getRoot().setOnClickListener(view -> {
            AddressChoiceAdapter.this.toggleRemovePrevious(user);
        });
        addressItemLayoutBinding.selectedCheckBox.setOnCheckedChangeListener((compoundButton, b) -> AddressChoiceAdapter.this.toggleRemovePrevious(user));

        return addressItemLayoutBinding.getRoot();
    }
    public void addSelectedAddress(UserDeliveryAddress user){
        AddressChoiceAdapter.this.toggleRemovePrevious(user);
    }

    public interface  EditAddressItemListner{
        void editAddressItemClicked(UserDeliveryAddress item);
    }
}
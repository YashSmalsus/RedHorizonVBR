package com.smalsus.redhorizonvbr.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.databinding.ActivityAddAddressScreenBinding;
import com.smalsus.redhorizonvbr.db.UserAddressDatabase;
import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;

import java.util.List;

public class AddAddressScreen extends AppCompatActivity {
    private ActivityAddAddressScreenBinding addAddressScreenBinding;
    private boolean isUpdate = false;
    private UserDeliveryAddress userDeliveryAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addAddressScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_address_screen);
        addAddressScreenBinding.addAddress.setOnClickListener(view -> addAdresssData());
        addAddressScreenBinding.addAddressBackButton.setOnClickListener(view -> finish());
        Intent intent = getIntent();
        if (intent.getBooleanExtra("Is_Update_address", false)) {
            isUpdate = true;
            userDeliveryAddress= (UserDeliveryAddress) intent.getSerializableExtra("UPDATE_UserDeliveryAddress_DATA");
            setaddressData(userDeliveryAddress);
        }

    }

    private void getAddressListData() {
        UserAddressDatabase userAddressDatabase = UserAddressDatabase.getDatabase(this);
        List<UserDeliveryAddress> userDeliveryAddressList = userAddressDatabase.userAddressDatabase().getAllWords();
        int size = userDeliveryAddressList.size();
    }

    private void addAdresssData() {
        String name = addAddressScreenBinding.nameInput.getText().toString();
        String home = addAddressScreenBinding.inputHouseNo.getText().toString();
        String colony = addAddressScreenBinding.colonyInput.getText().toString();
        String city = addAddressScreenBinding.cityInput.getText().toString();
        String state = addAddressScreenBinding.stateInput.getText().toString();
        String pinCode = addAddressScreenBinding.inputPinCode.getText().toString();
        String phoneNumber = addAddressScreenBinding.phoneNumberInput.getText().toString();
        UserDeliveryAddress userDeliveryAddress = new UserDeliveryAddress(name, home, colony, city, state, pinCode, phoneNumber);
        new AgentAsyncTask(this).execute(userDeliveryAddress);
    }
    private void updateAddress(){
        String name = addAddressScreenBinding.nameInput.getText().toString();
        String home = addAddressScreenBinding.inputHouseNo.getText().toString();
        String colony = addAddressScreenBinding.colonyInput.getText().toString();
        String city = addAddressScreenBinding.cityInput.getText().toString();
        String state = addAddressScreenBinding.stateInput.getText().toString();
        String pinCode = addAddressScreenBinding.inputPinCode.getText().toString();
        String phoneNumber = addAddressScreenBinding.phoneNumberInput.getText().toString();
    }

    private void setaddressData(UserDeliveryAddress userDeliveryAddress) {
        if (userDeliveryAddress != null) {
            addAddressScreenBinding.nameInput.setText(userDeliveryAddress.getName());
            addAddressScreenBinding.inputHouseNo.setText(userDeliveryAddress.getBuildingName());
            addAddressScreenBinding.colonyInput.setText(userDeliveryAddress.getArea());
            addAddressScreenBinding.cityInput.setText(userDeliveryAddress.getCity());
            addAddressScreenBinding.stateInput.setText(userDeliveryAddress.getCity());
            addAddressScreenBinding.inputPinCode.setText(userDeliveryAddress.getPin_code());
            addAddressScreenBinding.phoneNumberInput.setText(userDeliveryAddress.getPhone_number());
        }

    }

    private class AgentAsyncTask extends AsyncTask<UserDeliveryAddress, Void, Boolean> {
        private Activity activity;


        public AgentAsyncTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(UserDeliveryAddress... params) {
            UserAddressDatabase userAddressDatabase = UserAddressDatabase.getDatabase(activity);
            if (isUpdate) {
                userAddressDatabase.userAddressDatabase().updateUserDeliveryAddress(params[0]);
            } else {
                userAddressDatabase.userAddressDatabase().insert(params[0]);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean value) {
            this.activity.finish();
        }
    }
}

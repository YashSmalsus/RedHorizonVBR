package com.smalsus.redhorizonvbr.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.AddressChoiceAdapter;
import com.smalsus.redhorizonvbr.databinding.SelectAddressScreenBinding;
import com.smalsus.redhorizonvbr.db.UserAddressDatabase;
import com.smalsus.redhorizonvbr.db.entity.UserDeliveryAddress;

import java.util.List;

public class SelectAddressScreen extends AppCompatActivity implements AddressChoiceAdapter.EditAddressItemListner {

    SelectAddressScreenBinding selectAddressScreenBinding;
    private AddressChoiceAdapter addressChoiceAdapter;
    private UserDeliveryAddress userDeliveryAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectAddressScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_address_screen);
        Intent intent=getIntent();
        userDeliveryAddress= (UserDeliveryAddress) intent.getSerializableExtra(OrderSummaryScreen.USER_DELIVERY_ADDRESS);
        selectAddressScreenBinding.addNewAddress.setOnClickListener(view -> {
            Intent intent2 = new Intent(SelectAddressScreen.this, AddAddressScreen.class);
            intent2.putExtra("Is_Update_address",false);
            startActivity(intent2);
        });

        selectAddressScreenBinding.addDiliveryAddressBtn.setOnClickListener(view -> {
            if (addressChoiceAdapter != null) {
                List<UserDeliveryAddress> userDeliveryAddresses = (List<UserDeliveryAddress>) addressChoiceAdapter.getSelectedItems();
                if (userDeliveryAddresses != null && userDeliveryAddresses.size() > 0) {
                    HRpreference.getInstance(getApplicationContext()).saveUserAddress(userDeliveryAddresses.get(0));
                }
                finish();
            }
        });

        selectAddressScreenBinding.selectAddressbackButton.setOnClickListener(view -> {
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new AgentAsyncTask(this).execute("");
    }

    @Override
    public void editAddressItemClicked(UserDeliveryAddress item) {
        Intent intent2 = new Intent(SelectAddressScreen.this, AddAddressScreen.class);
        intent2.putExtra("Is_Update_address",true);
        intent2.putExtra("UPDATE_UserDeliveryAddress_DATA",item);
        startActivity(intent2);
    }

    private class AgentAsyncTask extends AsyncTask<String, Void, List<UserDeliveryAddress>> {
        private Activity activity;


        public AgentAsyncTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected List<UserDeliveryAddress> doInBackground(String... params) {
            UserAddressDatabase userAddressDatabase = UserAddressDatabase.getDatabase(activity);
            return userAddressDatabase.userAddressDatabase().getAllWords();
        }

        @Override
        protected void onPostExecute(List<UserDeliveryAddress> userDeliveryAddressList) {
            addressChoiceAdapter = new AddressChoiceAdapter(this.activity, userDeliveryAddressList);
            selectAddressScreenBinding.addressChoiceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            selectAddressScreenBinding.addressChoiceList.setAdapter(addressChoiceAdapter);
            if(userDeliveryAddress!=null)
            addressChoiceAdapter.addSelectedAddress(userDeliveryAddress);
        }
    }
}

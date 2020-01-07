package com.smalsus.redhorizonvbr.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ContactListViewModel extends ViewModel {
    private MutableLiveData<List<EventUser>> users;

    public LiveData<List<EventUser>> getUsers(String id, String token) {
        if (users == null) {
            users = new MutableLiveData<>();
            getFriendList(id, token);
        }
        return users;
    }

    private void getFriendList(String id, String token) {
        EventRequest request = new EventRequest();
        request.requestForFriendList(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        Type listType = new TypeToken<List<FriendList>>() {
                        }.getType();
                        List<FriendList> yourList = new Gson().fromJson(myresponse, listType);
                        users.postValue(yourList.get(0).getAssociateList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }
}
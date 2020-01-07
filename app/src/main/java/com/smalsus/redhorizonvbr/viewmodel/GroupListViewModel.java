package com.smalsus.redhorizonvbr.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.model.GroupModel;
import com.smalsus.redhorizonvbr.networkrequest.HrApiRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupListViewModel extends ViewModel {
    private MutableLiveData<List<GroupModel>> groupList;

    public LiveData<List<GroupModel>> getGroup(String id, String token) {
        if (groupList == null) {
            groupList = new MutableLiveData<>();
            getGroupList(id, token);
        }
        return groupList;
    }

    public void getGroupList(String id, String token) {
        HrApiRequest request = new HrApiRequest();
        request.getGroupList(id, token, new Callback() {
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
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<GroupModel>>() {
                        }.getType();
                        List<GroupModel> groupModelList = gson.fromJson(myresponse, type);
                        Collections.reverse(groupModelList);
                        groupList.postValue(groupModelList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}

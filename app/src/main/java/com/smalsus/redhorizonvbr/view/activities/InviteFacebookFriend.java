package com.smalsus.redhorizonvbr.view.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.smalsus.redhorizonvbr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InviteFacebookFriend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_facebook_friend);
        getFBFriendList();
    }

    private void getFBFriendList(){
        AccessToken token = AccessToken.getCurrentAccessToken();
        new GraphRequest(token,"2356445981277759/friends",null, HttpMethod.GET, graphResponse -> {
            try {
                JSONArray jsonArrayFriends = graphResponse.getJSONArray();
                JSONObject friendlistObject = jsonArrayFriends.getJSONObject(0);
                String friendListID = friendlistObject.getString("id");
                myNewGraphReq(friendListID);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).executeAsync();
        Bundle param = new Bundle();
        param.putString("fields", "friendlist");
    }

    private void myNewGraphReq(String friendlistId) {
        final String graphPath = "/"+friendlistId+"/members/";
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = new GraphRequest(token, graphPath, null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONObject object = graphResponse.getJSONObject();
                try {
                    JSONArray arrayOfUsersInFriendList= object.getJSONArray("data");
                    /* Do something with the user list */
                    /* ex: get first user in list, "name" */
                    JSONObject user = arrayOfUsersInFriendList.getJSONObject(0);
                    String usersName = user.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle param = new Bundle();
        param.putString("fields", "name");
        request.setParameters(param);
        request.executeAsync();
    }
}

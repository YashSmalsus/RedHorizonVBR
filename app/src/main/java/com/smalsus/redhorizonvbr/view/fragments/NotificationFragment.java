package com.smalsus.redhorizonvbr.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.InviteAdapter;
import com.smalsus.redhorizonvbr.model.InviteForm;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.networkrequest.HrApiRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationFragment extends Fragment implements InviteAdapter.ItemClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView invitelist;
    private InviteAdapter inviteAdapter;
    private ProgressDialog dialog;
    private List<InviteForm> InviteList;
    private List<InviteForm.Sender>senderList;
    private OnFragmentInteractionListener mListener;
    private LinearLayout textViewNoNotification;
    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_notification, container, false);
        InviteList = new ArrayList<>();
        senderList= new ArrayList<>();
        dialog = new ProgressDialog(getContext());
        invitelist = view.findViewById(R.id.invitelist);
        textViewNoNotification=view.findViewById(R.id.textViewNoNotification);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        invitelist.setLayoutManager(mLayoutManager);
        GetInviteList(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getAuthToken());
       return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View view, String Id) {
        acceptInvite(Id,HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken());

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void GetInviteList(String id, String token) {
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.getFriendInvite(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                dialog.dismiss();
                if(senderList.size()>0)
                    senderList.clear();
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {

                        JSONArray jsonArray = new JSONArray(myresponse);
                        for(int a=0;a<jsonArray.length();a++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(a);
                            InviteForm inviteForm = new InviteForm();
                            inviteForm.setId(jsonObject.getString("id"));
                            inviteForm.setInviteSent(jsonObject.getBoolean("isInviteSent"));
                            inviteForm.setStatus(jsonObject.getString("status"));
                            JSONObject jsonObject1=jsonObject.getJSONObject("sender");
                            InviteForm.Sender sender =new InviteForm.Sender();
                            sender.setUserName(jsonObject1.getString("userName"));
                            sender.setfName(jsonObject1.getString("fName"));
                            sender.setlName(jsonObject1.getString("lName"));
                            InviteList.add(inviteForm);
                            senderList.add(sender);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
               if(getActivity()!=null){
                   getActivity().runOnUiThread(() -> {
                       if(InviteList.size()>0)
                           textViewNoNotification.setVisibility(View.GONE);
                       else
                           textViewNoNotification.setVisibility(View.VISIBLE);
                       inviteAdapter = new InviteAdapter(getContext().getApplicationContext(), InviteList,senderList);
                       invitelist.setAdapter(inviteAdapter);
                       inviteAdapter.setClickListener(NotificationFragment.this);
                       inviteAdapter.notifyDataSetChanged();

                   });
               }
            }
        });
    }
    private void acceptInvite(String id, String token) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforAcceptInvite(id, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dialog.dismiss();
                int statusCode = response.code();
                if (statusCode == 200) {
                  if(getActivity()!=null){
                      getActivity().runOnUiThread(() -> GetInviteList(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken()));
                  }


                }
            }
        });
    }
}

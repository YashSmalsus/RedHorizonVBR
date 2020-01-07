package com.smalsus.redhorizonvbr.view.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.GlideUtils;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.Add_Attendeee_Adapter;
import com.smalsus.redhorizonvbr.model.EventInfo;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.utils.CollectionsUtils;
import com.smalsus.redhorizonvbr.utils.Consts;
import com.smalsus.redhorizonvbr.utils.FragmentExecuotr;
import com.smalsus.redhorizonvbr.utils.PermissionsChecker;
import com.smalsus.redhorizonvbr.utils.UtitlityClass;
import com.smalsus.redhorizonvbr.view.activities.PermissionsActivity;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VBR_Screen_Fragment extends Fragment implements View.OnClickListener {
    private static final String SHARED_PREF_ARG = "shared_pref_arugument";
    private PermissionsChecker checker;
    private ImageView admin_image;
    private TextView admin_name;
    private Add_Attendeee_Adapter add_attendeee_adapter;
    private List<EventUser> friendlist;
    private ProgressDialog dialog;
    private FloatingActionButton addCaller;
    private Button callButton;

    private OnFragmentInteractionListener mListener;

    public VBR_Screen_Fragment() {

    }

    public static VBR_Screen_Fragment newInstance() {
        VBR_Screen_Fragment fragment = new VBR_Screen_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // sharedPrefsHelper = (SharedPrefsHelper) getArguments().getSerializable(SHARED_PREF_ARG);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vbr__screen_, container, false);
        findViews(view);
        addCaller.setOnClickListener(this);
        friendlist = new ArrayList<>();
        getFriendList(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken());
        add_attendeee_adapter = new Add_Attendeee_Adapter(getContext(), friendlist);
        add_attendeee_adapter.setSelectedItemsCountsChangedListener(new Add_Attendeee_Adapter.SelectedItemsCountsChangedListener() {
            @Override
            public void onCountSelectedItemsChanged(int count) {
                if (count > 0) {
                    callButton.setEnabled(true);
                    callButton.setBackgroundResource(R.drawable.call_button_active);
                    callButton.setTextColor(getResources().getColor(R.color.white));
                } else {
                    callButton.setEnabled(false);
                    callButton.setBackgroundResource(R.drawable.call_button_inactive);
                    callButton.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        admin_name.setText(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getfName());
        GlideUtils.loadImage(getActivity(), HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getImageUrl(), admin_image, R.drawable.defaultuser, R.drawable.defaultuser);
        Vbr_Fragment tab2 = new Vbr_Fragment();
        FragmentExecuotr.addFragmentWithBackStack(getChildFragmentManager(), R.id.screen_VBR_container, tab2, tab2.getClass().getSimpleName());
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            // mListener.onFragmentInteraction(uri);
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

    private void findViews(View view) {
        checker = new PermissionsChecker(getContext());
        admin_image = view.findViewById(R.id.admin_image);
        admin_name = view.findViewById(R.id.admin_name);
        addCaller = view.findViewById(R.id.floating_action_button);
        dialog = new ProgressDialog(getContext());
    }

    private void getFriendList(String id, String token) {
        EventRequest request = new EventRequest();
        dialog.setMessage("Loading");
        dialog.show();
        request.requestForFriendList(id, token, new Callback() {
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
                    String myresponse = response.body().string();
                    try {
                        Type listType = new TypeToken<List<FriendList>>() {
                        }.getType();
                        List<FriendList> yourList = new Gson().fromJson(myresponse, listType);
                        for (EventUser eventUser : yourList.get(0).getAssociateList())
                            friendlist.add(eventUser);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> add_attendeee_adapter.notifyDataSetChanged());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void showpopup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.calling_popup_layout, null);
        alertDialog.setView(convertView);
        ListView lv = convertView.findViewById(R.id.userlist);
        callButton = convertView.findViewById(R.id.popupCallButton);
        Button popupCancelButton = convertView.findViewById(R.id.popupCancelButton);
        AlertDialog dialog = alertDialog.create();
        popupCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_attendeee_adapter.clearSelection();
                dialog.dismiss();
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collection<EventUser> userCollection = add_attendeee_adapter.getSelectedItems();
                ArrayList<String> videolIDist = CollectionsUtils.getSelectedUserVideoId(add_attendeee_adapter.getSelectedItems());
                List<EventUser> userList = new ArrayList<EventUser>(userCollection);
                if (userList.size() == 0) {
                    Toast.makeText(getContext(), "At least 1 contact must be selected", Toast.LENGTH_SHORT).show();
                } else {
                    if (videolIDist.size() > 0) {

                        if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                            startPermissionsActivity(true);
                        }

                    }

                }
            }
        });
        lv.setAdapter(add_attendeee_adapter);
        UtitlityClass.setListViewHeightBasedOnChildren(lv);
        dialog.show();
    }


    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(getActivity(), checkOnlyAudio, Consts.PERMISSIONS);
    }


    private void Start_Call_dashboard(List<EventInfo> groupList, int position) {
        String videoIDv;
        ArrayList<String> videolIDist = new ArrayList<>();
        for (int i = 0; i < groupList.get(position).getMembers().size(); i++) {
            videoIDv = groupList.get(position).getMembers().get(i).getVideoId();
            videolIDist.add(videoIDv);
        }
        if (videolIDist.size() > 0) {
            // startCall(false, videolIDist);
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view == addCaller)
            showpopup();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int screntType, boolean isVIDEO, boolean isVBR, ArrayList<String> userIDs, List<EventUser> eventmember);
    }
}

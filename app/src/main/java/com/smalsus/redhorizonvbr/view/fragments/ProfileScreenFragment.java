package com.smalsus.redhorizonvbr.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.APIConstance.HRConstant;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.ExpandableListAdapter;
import com.smalsus.redhorizonvbr.interfaces.CustomButtonListener;
import com.smalsus.redhorizonvbr.model.EventHeader;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.services.GPSTracker;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
import com.smalsus.redhorizonvbr.view.activities.EventDetailScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileScreenFragment extends Fragment implements View.OnClickListener, CustomButtonListener {

    public static boolean isProfileListUpdate = false;
    public static boolean isProfileDataUpdate = false;
    private OnFragmentInteractionListener mListener;
    private TextView noEventToday;
    private RelativeLayout pastBtnContainer, currentItemContainer, upcommingItemContainer;
    private TextView upcomingItemCount, pastItemCount, currentItemCount;
    private View upcomingTabDevider, currenttavDevider, pastCurrentDevider;
    private ExpandableListView listview;
    //  private TextView name_user;
    // private ImageView profile_user, coverImage;
    private String imageUrl;
    private List<EventHeader> _listDataHeader;
    private HashMap<EventHeader, GetEvent> _listDataChild;
    private List<EventHeader> pastEventHeader;
    private List<EventHeader> upcomingEventHeader;
    private HashMap<EventHeader, GetEvent> pastEventChildList;
    private HashMap<EventHeader, GetEvent> upcomingEventChildList;
    //  private ImageView settingbtn;
    private ProgressDialog dialog;
    //  private ImageButton background_change_btn;
    private ExpandableListAdapter expandableListAdapter;

    public ProfileScreenFragment() {
        // Required empty public constructor
    }

    public static ProfileScreenFragment newInstance() {
        ProfileScreenFragment fragment = new ProfileScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);
        GPSTracker gps = new GPSTracker(getContext(), getActivity());
        _listDataHeader = new ArrayList<>();
        _listDataChild = new HashMap<>();
        pastEventHeader = new ArrayList<>();
        pastEventChildList = new HashMap<>();
        upcomingEventHeader = new ArrayList<>();
        upcomingEventChildList = new HashMap<>();
//        settingbtn = view.findViewById(R.id.settingbtn);
//        settingbtn.setOnClickListener(this);
//        background_change_btn = view.findViewById(R.id.background_btn);
//        coverImage = view.findViewById(R.id.coverImage);
        noEventToday = view.findViewById(R.id.noEventToday);
        dialog = new ProgressDialog(getContext());
//        profile_user = view.findViewById(R.id.profile_user);
//        eventcounter = view.findViewById(R.id.eventcounter);
//        name_user = view.findViewById(R.id.name_user);
        // TextView todaydateview = view.findViewById(R.id.todaydateview);
//        background_change_btn.setOnClickListener(this);
        listview = view.findViewById(R.id.listview);
        pastBtnContainer = view.findViewById(R.id.pastBtnContainer);
        pastBtnContainer.setOnClickListener(this);
        currentItemContainer = view.findViewById(R.id.currentItemContainer);
        currentItemContainer.setOnClickListener(this);
        upcommingItemContainer = view.findViewById(R.id.upcommingItemContainer);
        upcommingItemContainer.setOnClickListener(this);
        upcomingItemCount = view.findViewById(R.id.upcomingItemCount);
        currentItemCount = view.findViewById(R.id.currentEventItemCount);
        pastItemCount = view.findViewById(R.id.pastItemCount);
        upcomingTabDevider = view.findViewById(R.id.upcomingTabDevider);
        currenttavDevider = view.findViewById(R.id.currentTabDevider);
        pastCurrentDevider = view.findViewById(R.id.pastTabDevider);

        //  todaydateview.setText(CalenderUtils.getCurrentDateTime());
        if (getActivity() != null) {
            GetUserevent(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), CalenderUtils.getyear(), CalenderUtils.getmonth(), CalenderUtils.getdate(), HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken());
            getUserDetails(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken());
        }
        expandableListAdapter = new ExpandableListAdapter(getContext(), _listDataHeader, _listDataChild);
        listview.setAdapter(expandableListAdapter);
        expandableListAdapter.setCustomButtonListner(this);
        if (gps.canGetLocation()) {
            JSONObject location = new JSONObject();
            try {
                location.put(HRConstant.USER_LONGTITUDE, gps.getLongitude());
                location.put(HRConstant.USER_LATITUDE, gps.getLatitude());
                location.put(HRConstant.USER_CITY_LOCATION, gps.getLocality(getContext()));
                location.put(HRConstant.USER_COUNTRY_LOCATION, gps.getCountryName(getContext()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateUserLocation(location);
        }
        //  setUserDataLayout();

        return view;
    }

//    private void setUserDataLayout() {
//        if (getActivity() != null) {
//           // UserInfo userInfo = HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo();
//            //  name_user.setText(userInfo.getfName());
//            //  GlideUtils.loadImage(getContext(), userInfo.getImageUrl(), profile_user, R.drawable.defaultuser, R.drawable.defaultuser);
//            //  GlideUtils.loadImage(getContext(), userInfo.getCoverURL(), coverImage, R.drawable.coverbg, R.drawable.coverbg);
//        }
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            // mListener.onFragmentInteraction(uri);
        }
    }


    private void getUserDetails(String token) {
        EventRequest request = new EventRequest();
        request.getUserDetails(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String myResponse = response.body().string();
                final int statusCode = response.code();
                if (statusCode == 200) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Gson gson = new GsonBuilder().create();
                            UserInfo form = gson.fromJson(myResponse, UserInfo.class);
                            imageUrl = form.getImageUrl();
                            HRpreference.getInstance(getActivity().getApplicationContext()).saveUserInfo(form);
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        //   getActivity().runOnUiThread(() -> GlideUtils.loadImage(getActivity(), imageUrl, profile_user, R.drawable.defaultuser, R.drawable.defaultuser));
                    }
                }
            }
        });
    }

    private void GetUserevent(String id, String year, String month, String day, String token) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.getEventinfobyDay(id, year, month, day, token, new Callback() {
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
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<GetEvent>>() {
                        }.getType();
                        List<GetEvent> eventDetailList = gson.fromJson(myresponse, type);
                        updateEventList(eventDetailList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void updateEventList(List<GetEvent> eventDetailList) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                _listDataHeader.clear();
                _listDataChild.clear();
                pastEventHeader.clear();
                pastEventChildList.clear();
                upcomingEventHeader.clear();
                upcomingEventChildList.clear();
                for (GetEvent event : eventDetailList) {
                    if (CalenderUtils.isTimeBetweenDate(event.getStartDate(), event.getEndDate()) == 1) {
                        EventHeader eventHeader = new EventHeader(event.getEventID(), event.getName(), event.getStartDate(), event.getEventUser());
                        _listDataHeader.add(eventHeader);
                        _listDataChild.put(eventHeader, event);
                    } else if (CalenderUtils.minuteAgo(event.getStartDate()) == 2) {
                        EventHeader eventHeader = new EventHeader(event.getEventID(), event.getName(), event.getStartDate(), event.getEventUser());
                        upcomingEventHeader.add(eventHeader);
                        upcomingEventChildList.put(eventHeader, event);
                    } else {
                        EventHeader eventHeader = new EventHeader(event.getEventID(), event.getName(), event.getStartDate(), event.getEventUser());
                        pastEventHeader.add(eventHeader);
                        pastEventChildList.put(eventHeader, event);
                    }
                }
                if (eventDetailList.size() > 0) {
                    expandableListAdapter.notifyDataSetChanged();
                    currentItemCount.setText(String.valueOf(_listDataHeader.size()));
                    pastItemCount.setText(String.valueOf(pastEventHeader.size()));
                    upcomingItemCount.setText(String.valueOf(upcomingEventHeader.size()));
                    noEventToday.setVisibility(View.GONE);
                } else {
                    noEventToday.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
    public void onResume() {
        super.onResume();
        if (isProfileListUpdate) {
            if (getActivity() != null)
                GetUserevent(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), CalenderUtils.getyear(), CalenderUtils.getmonth(), CalenderUtils.getdate(), HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken());
            isProfileListUpdate = false;
        }
        if (isProfileDataUpdate) {
            // setUserDataLayout();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == pastBtnContainer) {
            setAdapter(2);
        } else if (view == currentItemContainer) {
            setAdapter(1);
        } else if (view == upcommingItemContainer) {
            setAdapter(3);
        }
    }

    private void updateUserLocation(JSONObject location) {
        if (getActivity() != null) {
            EventRequest request = new EventRequest();
            request.requestforUpdateLocation(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), location, HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }

    }

    private void setAdapter(int type) {
        if (type == 2) {
            expandableListAdapter = new ExpandableListAdapter(getContext(), pastEventHeader, pastEventChildList);
            listview.setAdapter(expandableListAdapter);
            expandableListAdapter.setCustomButtonListner(ProfileScreenFragment.this);
            currenttavDevider.setVisibility(View.GONE);
            upcomingTabDevider.setVisibility(View.GONE);
            pastCurrentDevider.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            expandableListAdapter = new ExpandableListAdapter(getContext(), _listDataHeader, _listDataChild);
            listview.setAdapter(expandableListAdapter);
            expandableListAdapter.setCustomButtonListner(ProfileScreenFragment.this);
            currenttavDevider.setVisibility(View.VISIBLE);
            upcomingTabDevider.setVisibility(View.GONE);
            pastCurrentDevider.setVisibility(View.GONE);
        } else if (type == 3) {
            expandableListAdapter = new ExpandableListAdapter(getContext(), upcomingEventHeader, upcomingEventChildList);
            listview.setAdapter(expandableListAdapter);
            expandableListAdapter.setCustomButtonListner(ProfileScreenFragment.this);
            currenttavDevider.setVisibility(View.GONE);
            upcomingTabDevider.setVisibility(View.VISIBLE);
            pastCurrentDevider.setVisibility(View.GONE);
        }

    }

    @Override
    public void onButtonClickListner(ArrayList<String> videolIDist, int id, GetEvent details) {
        if (id == 2) {
            Intent intent = new Intent(getContext(), EventDetailScreen.class);
            intent.putExtra("EVENT_DETAILS", details);
            intent.putExtra("screen", 1);
            startActivity(intent);
        } else if (id == 3) {
            mListener.onFragmentInteraction(3, false, true, videolIDist, details.getEventmember());
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int screntType, boolean isVIDEO, boolean isVBR, ArrayList<String> userIDs, List<EventUser> eventmember);
    }
}

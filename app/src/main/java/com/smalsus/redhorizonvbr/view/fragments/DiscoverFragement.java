package com.smalsus.redhorizonvbr.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.UserTierModel;
import com.smalsus.redhorizonvbr.networkrequest.CallApiRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.smalsus.redhorizonvbr.view.activities.LoginScreen.MY_PERMISSIONS_REQUEST_LOCATION;


public class DiscoverFragement extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private OnFragmentInteractionListener mListener;
    private List<UserTierModel> userTierModelList;

    public DiscoverFragement() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiscoverFragement newInstance() {

        return new DiscoverFragement();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String userId = marker.getSnippet();
        UserTierModel userTierModel = findUserByID(userId);
        if (userTierModel != null) {
            EventUser eventUser = new EventUser(userTierModel.getId(), userTierModel.getUserName(), userTierModel.getfName(), userTierModel.getlName(), "", userTierModel.getImageUrl(), "", userTierModel.getLocation());
            showPopUp(eventUser, userId);
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_fragement, container, false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.discovermap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 16.0f));
                            createMyMarker(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), "My Location", " ", 2);
                            mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                    .radius(1000)
                                    .strokeColor(getResources().getColor(R.color.black_transparent_50))
                                    .fillColor(getResources().getColor(R.color.grey_transparent_10)));
                            getTierFrienlist(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken());
                            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

                            // Set listeners for marker events.  See the bottom of this class for their behavior.
                            mMap.setOnMarkerClickListener(DiscoverFragement.this);
                            mMap.setOnInfoWindowClickListener(DiscoverFragement.this);
                        } else {
                            //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 14));
                            // mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .anchor(0.5f, 0.5f)
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.rh_location_pin)));
                }
            });
        }
    }

    private void createMyMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .anchor(0.5f, 0.5f)
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_and_flags)));
                }
            });
        }
    }

    private void getTierFrienlist(String authToken) {
        CallApiRequest callApiRequest = new CallApiRequest();
        callApiRequest.getTierFriendList(authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<UserTierModel>>() {
                        }.getType();
                        List<UserTierModel> eventList = gson.fromJson(myresponse, type);
                        userTierModelList = eventList;
                        addMarkers(eventList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void addMarkers(List<UserTierModel> userTierModelList) {
        for (UserTierModel userTierModel : userTierModelList) {
            if (userTierModel.getLocation() != null) {
                try {
                    Double latitude = userTierModel.getLocation().getLat();
                    Double longtitude = userTierModel.getLocation().getLongi();
                    createMarker(latitude, longtitude, userTierModel.getfName() + " " + userTierModel.getlName(), userTierModel.getId(), 1);

                } catch (NumberFormatException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }

    }

    private UserTierModel findUserByID(String id) {
        for (UserTierModel userTierModel : userTierModelList) {
            if (userTierModel.getId().equals(id))
                return userTierModel;
        }
        return null;
    }

    private void showPopUp(final EventUser eventUser, final String userId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custome_call_chat_action_dialog, null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();
        // reference the textview of custom_popup_dialog
        LinearLayout audioCallButton = customView.findViewById(R.id.audio_call_item_btn);
        LinearLayout vedioCallButton = customView.findViewById(R.id.video_call_btn);
        LinearLayout chatButton = customView.findViewById(R.id.chat_call_btn);


        audioCallButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.contactListFragIntraction(false, false, userId, eventUser, 2);
            }
            alertDialog.dismiss();
        });
        vedioCallButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.contactListFragIntraction(false, false, userId, eventUser, 1);
            }
            alertDialog.dismiss();
        });

        chatButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.contactListFragIntraction(false, false, userId, eventUser, 3);
            }
            alertDialog.dismiss();
        });


        alertDialog.show();

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void contactListFragIntraction(boolean isVIDEO, boolean isVBR, String userIDs, EventUser eventmember, int chatType);
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;


        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.costum_window_popup, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }


        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = view.findViewById(R.id.title);
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = view.findViewById(R.id.snippet);

//            if (snippet != null && snippet.length() > 12) {
//                SpannableString snippetText = new SpannableString(snippet);
//                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
//                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
//                snippetUi.setText(snippetText);
//            } else {
//                snippetUi.setText("");
//            }
        }
    }
}

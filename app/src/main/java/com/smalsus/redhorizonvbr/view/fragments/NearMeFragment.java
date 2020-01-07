package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.WearableListAdapter;
import com.smalsus.redhorizonvbr.model.WerableStorePlaces;
import com.smalsus.redhorizonvbr.viewmodel.LocationViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.smalsus.redhorizonvbr.view.activities.LoginScreen.MY_PERMISSIONS_REQUEST_LOCATION;


public class NearMeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mMap;
    private OnFragmentInteractionListener mListener;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private WearableListAdapter nearMeListAdapter;
    private ProgressBar spinner, nearMeProgressBar;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private LocationViewModel model;
    private LinearLayout restaurantBtn, atmBtn, shoppingBtn, bankButton;
    private TextView nearYouTitle;


    public NearMeFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static NearMeFragment newInstance(String param1, String param2) {
        NearMeFragment fragment = new NearMeFragment();
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
        View view = inflater.inflate(R.layout.fragment_near_me, container, false);
        RecyclerView nearMePlacesList = view.findViewById(R.id.nearByMeList);
        restaurantBtn = view.findViewById(R.id.restaurantButton);
        restaurantBtn.setOnClickListener(this);
        atmBtn = view.findViewById(R.id.atmButton);
        atmBtn.setOnClickListener(this);
        shoppingBtn = view.findViewById(R.id.shoppingMalButton);
        shoppingBtn.setOnClickListener(this);
        bankButton = view.findViewById(R.id.bankButton);
        bankButton.setOnClickListener(this);
        spinner = view.findViewById(R.id.progressBar1);
        nearMeProgressBar = view.findViewById(R.id.nearMeProgressBar);
        nearYouTitle=view.findViewById(R.id.nearYouTitle);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        nearMePlacesList.setLayoutManager(mLayoutManager);
        nearMeListAdapter = new WearableListAdapter(getContext(), new ArrayList<>());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.nearMeMap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }
        model = ViewModelProviders.of(this).get(LocationViewModel.class);

        model.getNetworkState().observe(this, networkState -> nearMeListAdapter.setNetworkState(networkState));

        nearMePlacesList.setAdapter(nearMeListAdapter);
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

    private void showAllmarkerOnMap(List<WerableStorePlaces> heroList) {

        for (WerableStorePlaces werableStorePlaces : heroList) {
            createMarker(Double.parseDouble(werableStorePlaces.geometry.locationA.lat), Double.parseDouble(werableStorePlaces.geometry.locationA.lat), werableStorePlaces.name, werableStorePlaces.vicinity, 2);
        }


    }

    @Override
    public void onPause() {
        super.onPause();

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
                                            mLastKnownLocation.getLongitude()), 14.0f));
                            createMyMarker(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), "My Location", " ", 2);
                            mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                    .radius(1000)
                                    .strokeColor(getResources().getColor(R.color.black_transparent_50))
                                    .fillColor(getResources().getColor(R.color.grey_transparent_10)));
                            model.getArticleLiveData(String.valueOf(mLastKnownLocation.getLatitude()), String.valueOf(mLastKnownLocation.getLongitude()), null).observe(NearMeFragment.this, heroList -> {
                                nearMeListAdapter.updateListItem(heroList);
                                showAllmarkerOnMap(heroList);
                                spinner.setVisibility(View.GONE);
                                nearMeProgressBar.setVisibility(View.GONE);
                            });
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

        if (mMap != null) {
          mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

    }

    private void createMyMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_and_flags)));
        }



    }

    @Override
    public void onClick(View view) {
        if (view == restaurantBtn) {
            if (mLastKnownLocation != null){
                nearMeProgressBar.setVisibility(View.VISIBLE);
                nearYouTitle.setText("Restaurant Near You");
                model.getNearMeData(String.valueOf(mLastKnownLocation.getLatitude()), String.valueOf(mLastKnownLocation.getLongitude()), "restaurant");
            }

        }else if(view==atmBtn) {
            if (mLastKnownLocation != null) {
                nearYouTitle.setText("ATM Near You");
                nearMeProgressBar.setVisibility(View.VISIBLE);
                model.getNearMeData(String.valueOf(mLastKnownLocation.getLatitude()), String.valueOf(mLastKnownLocation.getLongitude()), "atm");
            }
        }else if(view==bankButton){
            if (mLastKnownLocation != null){
                nearMeProgressBar.setVisibility(View.VISIBLE);
                nearYouTitle.setText("Bank Near  You");
                model.getNearMeData(String.valueOf(mLastKnownLocation.getLatitude()), String.valueOf(mLastKnownLocation.getLongitude()), "bank");
            }
        }
        else if(view==shoppingBtn){
            if (mLastKnownLocation != null){
                nearMeProgressBar.setVisibility(View.VISIBLE);
                nearYouTitle.setText("Shopping Mall  Near You");
                model.getNearMeData(String.valueOf(mLastKnownLocation.getLatitude()), String.valueOf(mLastKnownLocation.getLongitude()), "shopping_mall");
            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

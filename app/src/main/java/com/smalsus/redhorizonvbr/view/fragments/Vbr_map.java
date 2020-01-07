package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.model.UserLocation;
import com.smalsus.redhorizonvbr.services.GPSTracker;
import com.smalsus.redhorizonvbr.utils.Consts;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Vbr_map extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng sydney1;
    private GPSTracker gpsTracker;
    private Double adminL = 0.0d;
    private Double adminLong = 0.0d;
    private OnFragmentInteractionListener mListener;
    private List<EventUser> eventUsers;
    int screen;
    private ImageButton vbrFragmentChanger;

    public Vbr_map() {
        // Required empty public constructor
    }

    public static Vbr_map newInstance(List<EventUser> eventUserList) {
        Vbr_map fragment = new Vbr_map();
        Bundle args = new Bundle();
        args.putSerializable(Consts.EXTRA_USERLIST_DATA, (Serializable) eventUserList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParcebleExtra();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vbr_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        vbrFragmentChanger = view.findViewById(R.id.vbrFragmentChanger);
        vbrFragmentChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    Vbr_map vbr_map = new Vbr_map();
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(vbr_map.getClass().getSimpleName());
                    if (fragment != null)
                        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void setMyData(List<GetEvent> list) {

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
        if (HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getLocation().getLat() != null) {
            adminL = HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getLocation().getLat();
            adminLong = HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getLocation().getLongi();
            sydney1 = new LatLng(adminL, adminLong);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney1, 14.0f));
            mMap.addMarker(new MarkerOptions().position(sydney1).draggable(false).visible(true).title(HRpreference.getInstance(getContext().getApplicationContext()).getUserInfo().getfName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.locationpin)));
        }
        if (eventUsers != null && eventUsers.size() > 0) {
            for (int a = 0; a < eventUsers.size(); a++) {
                UserLocation userLocation = eventUsers.get(a).getLocationMap();
                if (userLocation != null && userLocation.getLat() != null && userLocation.getLongi() != null) {
                    Double lati = eventUsers.get(a).getLocationMap().getLat();
                    Double longi = eventUsers.get(a).getLocationMap().getLongi();
                    LatLng sydney2 = new LatLng(Double.valueOf(lati), Double.valueOf(longi));
                    this.showCurvedPolyline(sydney1, sydney2, 0.5);
                    mMap.addMarker(new MarkerOptions().position(sydney2).draggable(false).visible(true).title(eventUsers.get(a).getfName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.avatar32)));
                }
            }
        }
    }

    private void showCurvedPolyline(LatLng p1, LatLng p2, double k) {

        try {
            double d = SphericalUtil.computeDistanceBetween(p1, p2);
            double h = SphericalUtil.computeHeading(p1, p2);
            LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);
            double x = (1 - k * k) * d * 0.5 / (2 * k);
            double r = (1 + k * k) * d * 0.5 / (2 * k);
            LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);
            PolylineOptions options = new PolylineOptions();
            List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(0));
            double h1 = SphericalUtil.computeHeading(c, p1);
            double h2 = SphericalUtil.computeHeading(c, p2);
            int numpoints = 100;
            double step = (h2 - h1) / numpoints;
            for (int i = 0; i < numpoints; i++) {
                LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
                options.add(pi);
            }
            mMap.addPolyline(options.width(5).color(Color.RED).geodesic(false).pattern(pattern));
        } catch (NullPointerException e) {
            System.out.print("NullPointerException Caught");
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getParcebleExtra() {
        if (getArguments() != null) {
            eventUsers = (List<EventUser>) getArguments().getSerializable(Consts.EXTRA_USERLIST_DATA);
        }
    }


}

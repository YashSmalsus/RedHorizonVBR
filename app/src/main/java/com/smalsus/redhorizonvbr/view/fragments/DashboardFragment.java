package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.DashboardItemAdapter;
import com.smalsus.redhorizonvbr.model.DashBoardItem;
import com.smalsus.redhorizonvbr.utils.RecycleItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    public static String[] dashboardUrl = {"http://18.222.29.55:1212/generic/dashboard",
            "http://18.222.29.55:1212/energy/dashboard",
            "http://18.222.29.55:1212/finance/dashboard",
            "http://18.222.29.55:1212/teleCommunication/dashboard",
            "http://18.222.29.55:1212/healthcare/dashboard",
            "http://18.222.29.55:1212/material/dashboard",
            "http://18.222.29.55:1212/utility/dashboard",
            "http://18.222.29.55:1212/informationTechnology/dashboard",
            "http://18.222.29.55:1212/webservices/dashboard",
            "http://18.222.29.55:1212/merchandise/dashboard",
            "http://18.222.29.55:1212/realestate/dashboard",
            "http://18.222.29.55:1212/iOT/dashboard"
    };
    private String[] web = {
            "MAIN",
            "Energy",
            "Financials",
            "Telecommunications",
            "Healthcare",
            "Materials",
            "Utilities",
            "Information Technology",
            "Web Services",
            "Merchanise",
            "Real Estate",
            "Internet Of Things"

    };
    private int[] imageId = {
            R.drawable.main_dashboard_ic,
            R.drawable.energy_dashboard_ic,
            R.drawable.finance_dashboard_ic,
            R.drawable.telecommunication_dashboard_ic,
            R.drawable.healthcare_dashboard_ic,
            R.drawable.material_dashboard_ic,
            R.drawable.utility_dashboard_ic,
            R.drawable.information_technology_dashboard_ic,
            R.drawable.wennservice_dashboard_ic,
            R.drawable.staples_dashboard_ic,
            R.drawable.real_state_dashboard_ic,
            R.drawable.iot_dashboard_ic

    };
    private OnFragmentInteractionListener mListener;
    private RecyclerView dashBoardGridList;
    private List<DashBoardItem> dashBoardItemList;
    private DashboardItemAdapter dashboardItemAdapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashBoardItemList = new ArrayList<>();
        dashBoardGridList = view.findViewById(R.id.dashboardlist);
        dashBoardGridList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dashboardItemAdapter = new DashboardItemAdapter(getContext(), dashBoardItemList);
        dashBoardGridList.setAdapter(dashboardItemAdapter);
        dashBoardGridList.addItemDecoration(new RecycleItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.fab_margin),
                getResources().getInteger(R.integer.photo_list_preview_columns)));
        addDashItemData();
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

    private void addDashItemData() {
        for (int i = 0; i < web.length; i++) {
            DashBoardItem dashBoardItem = new DashBoardItem(web[i], imageId[i], dashboardUrl[i]);
            dashBoardItemList.add(dashBoardItem);
        }
        dashboardItemAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


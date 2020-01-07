package com.smalsus.redhorizonvbr.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gtomato.android.ui.transformer.FlatMerryGoRoundTransformer;
import com.gtomato.android.ui.widget.CarouselView;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.UserProfilePostsGallery.UserProfilePostsTab;
import com.smalsus.redhorizonvbr.model.UserInfo;
import com.smalsus.redhorizonvbr.view.activities.UserProfileScreen;
import com.squareup.picasso.Picasso;

import java.util.Random;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button eventTabBtn, dashBoardTabBtn, searchTabBtn;


    private OnFragmentInteractionListener mListener;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        CarouselView carouselView = view.findViewById(R.id.carousel);
        carouselView.setTransformer(new FlatMerryGoRoundTransformer());
        carouselView.setAdapter(new RandomPageAdapter(getContext()));
        carouselView.setGravity(Gravity.TOP);
        carouselView.setInfinite(true);
        eventTabBtn = view.findViewById(R.id.eventTab);
        dashBoardTabBtn = view.findViewById(R.id.dashboardTab);
        searchTabBtn = view.findViewById(R.id.searchTab);
        eventTabBtn.setOnClickListener(this);
        dashBoardTabBtn.setOnClickListener(this);
        searchTabBtn.setOnClickListener(this);
        UserInfo userInfo = HRpreference.getInstance(getContext()).getUserInfo();
        carouselView.setOnItemClickListener((adapter, view1, i, i1) -> {
            if (i == 0) {
                //Intent intent = new Intent(getContext(), UserProfileScreen.class);
                Intent intent = new Intent(getContext(), UserProfilePostsTab.class);
                intent.putExtra("userId", userInfo.getId());
                intent.putExtra("ScreenType", 1);
                Intent intent1 = new Intent(getContext(), UserProfileScreen.class);
                intent1.putExtra(UserProfileScreen.USER_DETAILS_ID, userInfo.getId());
                intent1.putExtra("ScreenType",1);
                startActivity(intent1);
                getActivity().finish();
            }

        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DashboardFragment dashboardFragment = new DashboardFragment();
        loadFragment(dashboardFragment);
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.homeFrameLayoutContainer, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        if (view == eventTabBtn) {
            eventTabBtn.setBackgroundResource(R.drawable.tab_btn_active_bg);
            eventTabBtn.setTextColor(getResources().getColor(R.color.home_tab_selected_bg_color));
            dashBoardTabBtn.setTextColor(getResources().getColor(R.color.white));
            searchTabBtn.setTextColor(getResources().getColor(R.color.white));
            dashBoardTabBtn.setBackgroundResource(R.drawable.tab_btn_deactive);
            searchTabBtn.setBackgroundResource(R.drawable.tab_btn_deactive);
            ProfileScreenFragment profileScreenFragment = new ProfileScreenFragment();
            loadFragment(profileScreenFragment);
        } else if (view == dashBoardTabBtn) {
            eventTabBtn.setBackgroundResource(R.drawable.tab_btn_deactive);
            dashBoardTabBtn.setBackgroundResource(R.drawable.tab_btn_active_bg);
            searchTabBtn.setBackgroundResource(R.drawable.tab_btn_deactive);
            dashBoardTabBtn.setTextColor(getResources().getColor(R.color.home_tab_selected_bg_color));
            eventTabBtn.setTextColor(getResources().getColor(R.color.white));
            searchTabBtn.setTextColor(getResources().getColor(R.color.white));
            DashboardFragment dashboardFragment = new DashboardFragment();
            loadFragment(dashboardFragment);
        } else if (view == searchTabBtn) {
            eventTabBtn.setBackgroundResource(R.drawable.tab_btn_deactive);
            dashBoardTabBtn.setBackgroundResource(R.drawable.tab_btn_deactive);
            searchTabBtn.setBackgroundResource(R.drawable.tab_btn_active_bg);
            dashBoardTabBtn.setTextColor(getResources().getColor(R.color.white));
            eventTabBtn.setTextColor(getResources().getColor(R.color.white));
            searchTabBtn.setTextColor(getResources().getColor(R.color.home_tab_selected_bg_color));
            SearchFragment dashboardFragment = new SearchFragment();
            loadFragment(dashboardFragment);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public static class ViewHolder extends CarouselView.ViewHolder {
        ImageView imageView;
        TextView itemTextView;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.item_image);
            this.itemTextView = itemView.findViewById(R.id.item_text);
            this.itemView = itemView;
        }
    }

    public static class RandomPageAdapter extends CarouselView.Adapter<ViewHolder> {

        int[] slidingImagesBg = {R.drawable.sliding_blue, R.drawable.sliding_green, R.drawable.sliding_orange, R.drawable.sliding_pink, R.drawable.sliding_red, R.drawable.sliding_yellow};
        private Context mContext;

        RandomPageAdapter(Context context) {
            super();
            this.mContext = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // View view = RandomPageFragment.createView(context, width, height, "0");

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View listItem = layoutInflater.inflate(R.layout.crousel_item, parent, false);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                UserInfo userInfo = HRpreference.getInstance(mContext).getUserInfo();
                Picasso.get().load(userInfo.getImageUrl()).into(holder.imageView);
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(mContext, UserProfileScreen.class);
                    mContext.startActivity(intent);

                });

            }
            holder.itemView.setBackgroundResource(slidingImagesBg[new Random().nextInt(slidingImagesBg.length)]);
        }

        @Override
        public int getItemCount() {
            return 10;
        }

    }


}

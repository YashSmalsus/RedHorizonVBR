package com.smalsus.redhorizonvbr.view.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.GridAdapter;
import com.smalsus.redhorizonvbr.adapters.PopupEventAdapter;
import com.smalsus.redhorizonvbr.interfaces.CustomButtonListener;
import com.smalsus.redhorizonvbr.interfaces.OnSwipeTouchListener;
import com.smalsus.redhorizonvbr.model.CalenderEventForm;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.view.activities.AddEventScreen;
import com.smalsus.redhorizonvbr.view.activities.EventDetailScreen;
import com.smalsus.redhorizonvbr.view.activities.MultiPartyActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EventDashboardFragment extends Fragment implements View.OnClickListener, GridAdapter.GridItemClicked, CustomButtonListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static boolean isListRefresh = false;
    private GridView gridView;
    private Calendar calInstance = Calendar.getInstance(Locale.ENGLISH);
    private Calendar calendarObject = Calendar.getInstance(Locale.ENGLISH);
    private TextView display_current_date, noEventForTodayTextView, createEvent;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private ProgressDialog dialog;
    private List<GetEvent> eventDetail;
    private List<GetEvent> todayEventList;
    private Date date;
    private int currentDate;
    private ArrayList<CalenderEventForm> daysInThisMonth;
    private ArrayList<CalenderEventForm> daysInLastMonth;
    private ArrayList<CalenderEventForm> daysInNextMonth;
    private ListView listEvent;
    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private int isMonthSelected = 0, isYearselected = 0;
    private OnFragmentInteractionListener mListener;
    private PopupEventAdapter popupEventAdapter;

    private AppBarLayout appBarLayout;
    private TextView screenHeadingText;


    //private TextView screenHeadingText;

    public EventDashboardFragment() {
        // Required empty public constructor
    }

    public static EventDashboardFragment newInstance(String param1, String param2) {
        EventDashboardFragment fragment = new EventDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static String getCurrentDateTime() {
        String dateValue = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateValue = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dateValue;
    }

   @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appBarLayout.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                        if (i == 0)
                        {
                            screenHeadingText.setText("ITINERARY");
                        }
                        else
                        {

                        }
                    }
                }
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_dashboard, container, false);
        Spinner year_spinner = view.findViewById(R.id.year_spinner);
        gridView = view.findViewById(R.id.calendar_grid);

        appBarLayout = view.findViewById(R.id.itineraryAppBarLayout);
        screenHeadingText = view.findViewById(R.id.itineraryScreebHeading);

        ImageButton addEvent = view.findViewById(R.id.addEvent);
        addEvent.setOnClickListener(this);
        dialog = new ProgressDialog(getContext());
        eventDetail = new ArrayList<>();
        todayEventList = new ArrayList<>();
        listEvent = view.findViewById(R.id.listevent);
        createEvent = view.findViewById(R.id.createEvent);
        createEvent.setOnClickListener(this);
        noEventForTodayTextView = view.findViewById(R.id.noEventForTodayTextView);
        Spinner month_spinner = view.findViewById(R.id.month_spinner);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        try {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        } catch (NullPointerException e) {
            Log.d("Exception", e.getLocalizedMessage());
        }

        int height = displayMetrics.heightPixels;
        gridView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * (.10))));
        ArrayAdapter<String> month = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, months);
        month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month_spinner.setAdapter(month);
        month_spinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        ArrayList<String> years = new ArrayList<>();
        Calendar previousYearInstace = Calendar.getInstance();
        previousYearInstace.add(Calendar.YEAR, -2);
        Calendar nextYearInstace = Calendar.getInstance();
        nextYearInstace.add(Calendar.YEAR, 5);
        for (int i = previousYearInstace.get(Calendar.YEAR); i <= nextYearInstace.get(Calendar.YEAR); i++)
        {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        yearadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(yearadapter);
        int spinnerPosition = yearadapter.getPosition(Integer.toString(calendarObject.get(Calendar.YEAR)));
        year_spinner.setSelection(spinnerPosition);
        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++isMonthSelected > 1) {
                    calInstance.set(Calendar.MONTH, i + 1);
                    date = calInstance.getTime();
                    setUpCalendarAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++isYearselected > 1) {
                    String year_value = adapterView.getSelectedItem().toString();
                    calInstance.set(Calendar.YEAR, Integer.parseInt(year_value));
                    date = calInstance.getTime();
                    setUpCalendarAdapter();
                }

            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        ImageView previous_month = view.findViewById(R.id.previous_month);
        ImageView nextmonth = view.findViewById(R.id.next_month);
        display_current_date = view.findViewById(R.id.display_current_date);
        previous_month.setOnClickListener(this);
        nextmonth.setOnClickListener(this);
        date = calInstance.getTime();
        setUpCalendarAdapter();
        gridView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                calInstance.add(Calendar.MONTH, -1);
                date = calInstance.getTime();
                setUpCalendarAdapter();
            }

            public void onSwipeLeft() {
                calInstance.add(Calendar.MONTH, 1);
                date = calInstance.getTime();
                setUpCalendarAdapter();
            }
        });

        popupEventAdapter = new PopupEventAdapter(getContext(), todayEventList);
        popupEventAdapter.setCustomButtonListner(this);
        listEvent.setAdapter(popupEventAdapter);
        return view;
    }

    private void getUserEventByDate(String id, String year, String month, String day, String token) {
        EventRequest request = new EventRequest();
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
                if (todayEventList != null)
                    todayEventList.clear();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<GetEvent>>() {
                        }.getType();
                        List<GetEvent> eventList = gson.fromJson(myresponse, type);
                        todayEventList.addAll(eventList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        popupEventAdapter.notifyDataSetChanged();
                        if (popupEventAdapter.getCount() > 0)
                            noEventForTodayTextView.setVisibility(View.GONE);
                        else
                            noEventForTodayTextView.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
    }

    private void setUpCalendarAdapter() {

        List<Date> dayValueInCells = new ArrayList<>();
        Calendar mCal = (Calendar) calInstance.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        Date date4 = mCal.getTime();
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        int MAX_CALENDAR_COLUMN = 42;
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        GetUserevent(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken(), getCurrentDateTime());
        String sDate = formatter.format(calInstance.getTime());
        int sDay = calInstance.get(Calendar.DAY_OF_MONTH);
        display_current_date.setText(sDate);
    }

    private void GetUserevent(String id, String token, String date) {
        EventRequest request = new EventRequest();
        if (eventDetail.size() > 0)
            eventDetail.clear();
        dialog.setMessage("please wait");
        dialog.show();
        request.getEventinfo(id, token, date, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200) {
                    String myresponse = response.body().string();
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<GetEvent>>() {
                        }.getType();
                        eventDetail = gson.fromJson(myresponse, type);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> getDaysOfMonth());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                }


            }

        });
    }

    private void getDaysOfMonth() {
        getUserEventByDate(HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getId(), String.valueOf(calInstance.get(Calendar.YEAR)), String.valueOf(calInstance.get(Calendar.MONTH)), String.valueOf(calInstance.get(Calendar.DAY_OF_MONTH)), HRpreference.getInstance(getActivity().getApplicationContext()).getUserInfo().getAuthToken());
        this.daysInLastMonth = new ArrayList<>();
        this.daysInNextMonth = new ArrayList<>();
        this.daysInThisMonth = new ArrayList<>();
        String currentMonth = this.months[this.calendarObject.get(Calendar.MONTH)];
        String currentYear = String.valueOf(this.calendarObject.get(Calendar.YEAR));

        if (this.date.getMonth() == new Date().getMonth()) {
            this.currentDate = new Date().getDate();
        } else {
            this.currentDate = 999;
        }

        int firstDayThisMonth = new Date(this.date.getYear(), this.date.getMonth(), 1).getDay();
        int prevNumOfDays = new Date(this.date.getYear(), this.date.getMonth(), 0).getDate();

        for (int i = prevNumOfDays - (firstDayThisMonth - 1); i <= prevNumOfDays; i++) {
            Date dateToCompare = new Date(this.date.getYear(), this.date.getMonth() - 1, i);
            String thisPrevDay = this.getWeekDay(dateToCompare);
            this.checkingForRecord(2, dateToCompare, thisPrevDay, i);
        }
        int thisNumOfDays = new Date(this.date.getYear(), this.date.getMonth() + 1, 0).getDate();
        for (int j = 0; j < thisNumOfDays; j++) {
            Date dateToCompare = new Date(this.date.getYear(), this.date.getMonth(), j + 1);
            String thisDay = this.getWeekDay(dateToCompare);
            this.checkingForRecord(1, dateToCompare, thisDay, j + 1);
        }


        int lastDayThisMonth = new Date(this.date.getYear(), this.date.getMonth() + 1, 0).getDay();
        for (int k = 0; k < (6 - lastDayThisMonth); k++) {
            Date dateToCompare = new Date(this.date.getYear(), this.date.getMonth() + 1, k + 1);
            String thisLastDay = this.getWeekDay(dateToCompare);
            this.checkingForRecord(3, dateToCompare, thisLastDay, k + 1);
        }
        int totalDays = this.daysInLastMonth.size() + this.daysInThisMonth.size() + this.daysInNextMonth.size();
        if (totalDays < 36) {
            for (int l = (7 - lastDayThisMonth); l < ((7 - lastDayThisMonth) + 7); l++) {
                Date dateToCompare = new Date(this.date.getYear(), this.date.getMonth() + 1, l);
                String thisLastMonthDay = this.getWeekDay(dateToCompare);
                this.checkingForRecord(3, dateToCompare, thisLastMonthDay, l);
            }
        }
        for (CalenderEventForm calenderEventForm : this.daysInThisMonth)
            this.daysInLastMonth.add(calenderEventForm);
//        for (CalenderEventForm calenderEventForm : this.daysInNextMonth)
//            this.daysInLastMonth.add(calenderEventForm);


        GridAdapter gridAdapter = new GridAdapter(getContext(), this.daysInLastMonth, currentDate, currentMonth, currentYear);
        gridAdapter.setGridImtemClicked(this);
        gridView.setAdapter(gridAdapter);

    }

    private void checkingForRecord(int checkMonth, Date dateToCompare, String dateToDisplay, int DaytoDisplay) {
        boolean isFoundDate = false;
        List<EventUser> members;
        int selectedEventID;
        String varTitle;
        ArrayList<GetEvent> onDateEventData;
        onDateEventData = new ArrayList<>();
        for (GetEvent itemRecord : this.eventDetail) {
            String d = itemRecord.getStartDate().substring(0, 10);
            Date dateFirst = null;
            try {
                dateFirst = new SimpleDateFormat("yyyy-MM-dd").parse(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int dateFirstDay = dateFirst.getDate();
            int dateFirstMonth = dateFirst.getMonth();
            int dateFirstYear = dateFirst.getYear();
            int dateOtherDay = dateToCompare.getDate();
            int dateOtherMonth = dateToCompare.getMonth();
            int dateOtherYear = dateToCompare.getYear();

            if ((dateFirstDay == dateOtherDay) && (dateFirstMonth == dateOtherMonth) && (dateFirstYear == dateOtherYear)) {
                isFoundDate = true;
//                varTitle = itemRecord.getName();
//                members = itemRecord.getEventmember();
//                String location = itemRecord.getLocation();
//                String startdatev = itemRecord.getStartDate();
//                String endDate = itemRecord.getEndDate();
//                String desc = itemRecord.getDesc();
//                String reminder = itemRecord.getReminder();
//                String createdAt = itemRecord.getCreatedAt();
//                String fname = itemRecord.getEventUser().getfName();
//                selectedEventID = 0;
//                EventInfo abc = new EventInfo(createdAt, fname, endDate, desc, reminder, selectedEventID, varTitle, members, location, startdatev);
                onDateEventData.add(itemRecord);
            }
        }
        // int isBiggerThenCurrent = this.checkForCurrentDate(dateToCompare);
        if (isFoundDate && checkMonth == 1) {
            if (DaytoDisplay == currentDate) {
                this.daysInThisMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 1));
            } else {
                this.daysInThisMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 0));
            }

        } else if (isFoundDate && checkMonth == 2) {
            this.daysInLastMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 0));
        } else if (isFoundDate && checkMonth == 3) {
            this.daysInNextMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 0));
        } else if (!isFoundDate && checkMonth == 1) {
            if (DaytoDisplay == currentDate) {
                this.daysInThisMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 1));
            } else {
                this.daysInThisMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 0));
            }
        } else if (!isFoundDate && checkMonth == 2) {
            this.daysInLastMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 0));
        } else if (!isFoundDate && checkMonth == 3) {
            this.daysInNextMonth.add(new CalenderEventForm(dateToDisplay, DaytoDisplay, onDateEventData, 0));
        }
    }

    private String getWeekDay(Date date) {
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return days[date.getDay()];
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

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
    public void onResume() {
        super.onResume();
        if (isListRefresh) {
            setUpCalendarAdapter();
            isListRefresh = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous_month: {
                calInstance.add(Calendar.MONTH, -1);
                this.date = calInstance.getTime();
                setUpCalendarAdapter();
                
                break;
            }
            case R.id.next_month: {
                calInstance.add(Calendar.MONTH, 1);
                this.date = calInstance.getTime();
                setUpCalendarAdapter();
                break;
            }
            case R.id.addEvent: {
                Intent intent = new Intent(getContext(), AddEventScreen.class);
                intent.putExtra("AddEventType", 1);
                startActivity(intent);
                break;
            }
            case R.id.createEvent: {
                Intent intent = new Intent(getContext(), AddEventScreen.class);
                intent.putExtra("AddEventType", 1);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onGridItemClicked(ArrayList<GetEvent> eventList, View view, int position) {
        if (eventList.size() != 0) {
            if (!eventList.get(0).getName().isEmpty()) {
                todayEventList.clear();
                todayEventList.addAll(eventList);
                popupEventAdapter.notifyDataSetChanged();
            } else {
                if (eventList.size() > 0)
                    eventList.clear();
                todayEventList.clear();
                todayEventList.addAll(eventList);
                popupEventAdapter.notifyDataSetChanged();
            }
        } else {
            todayEventList.clear();
            todayEventList.addAll(eventList);
            popupEventAdapter.notifyDataSetChanged();
        }
    }


//    private void showConfirmMessage(ArrayList<String> videolIDist, List<EventUser> eventmember) {
//        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(getContext());
//        builder1.setMessage("Do you  want to call");
//        builder1.setCancelable(true);
//        builder1.setPositiveButton(
//                "Confirm",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        mListener.onFragmentInteraction(2, false, true, videolIDist, eventmember);
//                    }
//                });
//        builder1.setNegativeButton(
//                "Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
//        alert11.show();
//    }

    @Override
    public void onButtonClickListner(ArrayList<String> videolIDist, int id, GetEvent details) {
        if (id == 2) {
            Intent intent = new Intent(getContext(), EventDetailScreen.class);
            intent.putExtra("EVENT_DETAILS", details);
            intent.putExtra("screen", 1);
            startActivity(intent);
        } else if (id == 3) {
            mListener.onVBRFragmentInteraction(details.getChatRoomName(), details.getEventUser().getId(),details.getEventID());
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onVBRFragmentInteraction(String roomId, String userId,String eventId);
    }
}

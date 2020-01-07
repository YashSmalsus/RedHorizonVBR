package com.smalsus.redhorizonvbr.view.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smalsus.redhorizonvbr.AttendeesListActivity;
import com.smalsus.redhorizonvbr.HRpreference;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.RedHorizonValidator;
import com.smalsus.redhorizonvbr.SelectedContactsItemsInformation;
import com.smalsus.redhorizonvbr.adapters.Add_Attendeee_Adapter;
import com.smalsus.redhorizonvbr.adapters.SelectedContactAdapter;
import com.smalsus.redhorizonvbr.adapters.Selected_Contacts_Adapter;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.FriendList;
import com.smalsus.redhorizonvbr.model.GetEvent;
import com.smalsus.redhorizonvbr.model.Userlist;
import com.smalsus.redhorizonvbr.networkrequest.EventRequest;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;
//import com.smalsus.redhorizonvbr.view.HashTagEditText;
import com.smalsus.redhorizonvbr.view.HashTagEditText;
import com.smalsus.redhorizonvbr.view.fragments.EventDashboardFragment;
import com.smalsus.redhorizonvbr.view.fragments.ProfileScreenFragment;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddEventScreen extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearLayout;
    private Spinner room, reminder, available, location, selectEventColor;
    private EditText subject, description, subtopicTV, topicTV;
    private TextView addattendee, attendeename, start_date, end_date, screenTitle;
    private HashTagEditText hashTagEditText;
    private ProgressDialog dialog;
    private JSONArray jsonArray;
    private ImageButton backbtn;
    private Button submit;
    private List<EventUser> friendlist;
    private Add_Attendeee_Adapter add_attendeee_adapter;
    private Selected_Contacts_Adapter selected_contacts_adapter;
    private Calendar myCalendarStartTime;
    private Calendar myCalendarEndTime;
    private DatePickerDialog.OnDateSetListener date;
    private SimpleDateFormat sdf;
    private int label = 0;
    private String startdatevalue, endDatevalue, location_meeeting;
    private List<EventUser> userlist;
    private Integer type = 0;
    private Integer availablevalue = 0;
    private int reminderValue = 0;
    private boolean isUpdate;
    private int startTimemHour, startTimeMinute;
    private int endTimehours, endTimeminute;
    private RelativeLayout start_date_layout, end_date_layout;
    private GetEvent eventsDetails;
    private int addEventType;
    private ArrayAdapter<String> stringArrayAdapter;

    public static List<String> user_name_list;

    private RecyclerView recyclerView;
    private SelectedContactAdapter selectedContactAdapter;

    private LinearLayout selectedContactsLinearLayout;

    public String attendeesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_screen);
        findView();
        addEventType = getIntent().getIntExtra("AddEventType", 0);
        if (getIntent().getBooleanExtra("ISUPADTE_EVENT", false)) {
            screenTitle.setText(getString(R.string.update_event));
            isUpdate = true;
            setUpdateData();
        } else {
            screenTitle.setText(getString(R.string.add_event_title));
            isUpdate = false;
            userlist = new ArrayList<>();
        }
        getFriendList(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken()); //response function to fetch friend list
        add_attendeee_adapter = new Add_Attendeee_Adapter(this, friendlist);
        add_attendeee_adapter.setSelectedItemsCountsChangedListener(new Add_Attendeee_Adapter.SelectedItemsCountsChangedListener() {
            @Override
            public void onCountSelectedItemsChanged(int count) {

            }
        });


        //        GetUser(HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken());
        ArrayAdapter<String> locationadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.location));
        locationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(locationadapter);
        if (isUpdate)
            location.setSelection(locationadapter.getPosition(eventsDetails.getLocation()));
        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                location_meeeting = location.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type = 1;
            }
        });

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.colors));
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectEventColor.setAdapter(colorAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eventtype));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        room.setAdapter(adapter);
        room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type = 1;
            }
        });


        ArrayAdapter<String> reminderadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.reminder));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminder.setAdapter(reminderadapter);

        reminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String value = reminder.getSelectedItem().toString();
                String[] v = value.split(" ");
                reminderValue = Integer.parseInt(v[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> availableadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.available));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        available.setAdapter(availableadapter);
        available.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                availablevalue = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                availablevalue = 1;

            }
        });

        date = (view, year, month, dayOfMonth) -> {
            if (label == 0) {
                myCalendarStartTime.set(Calendar.YEAR, year);
                myCalendarStartTime.set(Calendar.MONTH, month);
                myCalendarStartTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (isBeforeDay(myCalendarStartTime, Calendar.getInstance())) {
                    showAlert("Start Date should be current or after current Date");
                } else {
                    String myFormat = "yyyy-MM-dd"; //In which you need put here
                    sdf = new SimpleDateFormat(myFormat, Locale.US);
                    startdatevalue = sdf.format(myCalendarStartTime.getTime());
                    timepicker();
                }
            } else if (label == 1) {
                myCalendarEndTime.set(Calendar.YEAR, year);
                myCalendarEndTime.set(Calendar.MONTH, month);
                myCalendarEndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (isBeforeDay(myCalendarEndTime, myCalendarStartTime)) {
                    showAlert("End Date should be  after Start Date");
                } else {
                    String myFormat = "yyyy-MM-dd"; //In which you need put here
                    sdf = new SimpleDateFormat(myFormat, Locale.US);
                    endDatevalue = sdf.format(myCalendarEndTime.getTime());
                    timeend();
                }
            }

        };

    }
    private void findView() {
        linearLayout = findViewById(R.id.selectEventColorContainer);
        screenTitle = findViewById(R.id.screenTitle);
        friendlist = new ArrayList<>();
        start_date_layout = findViewById(R.id.startlayout);
        end_date_layout = findViewById(R.id.endlayout);
        start_date_layout.setOnClickListener(this);
        end_date_layout.setOnClickListener(this);
        start_date = findViewById(R.id.startdate_v);
        end_date = findViewById(R.id.enddate_v);
        myCalendarStartTime = Calendar.getInstance();
        myCalendarEndTime = Calendar.getInstance();
        dialog = new ProgressDialog(this);
        room = findViewById(R.id.room);
        reminder = findViewById(R.id.reminder);
        available = findViewById(R.id.availability);
        subject = findViewById(R.id.input_subject);
        location = findViewById(R.id.loation);
        selectEventColor = findViewById(R.id.selectEventColor);
        description = findViewById(R.id.descption);
        backbtn = findViewById(R.id.addEvent_backbtn);
        addattendee = findViewById(R.id.addcontact);
        attendeename = findViewById(R.id.attendeename);
        subtopicTV = findViewById(R.id.subtopicTV);
        topicTV = findViewById(R.id.topicTV);
        backbtn.setOnClickListener(this);
        addattendee.setOnClickListener(this);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        hashTagEditText = findViewById(R.id.eventHashtags);
    }


    private void setUpdateData() {
        Intent intent = getIntent();
        eventsDetails = (GetEvent) intent.getSerializableExtra("EVENT_DETAILS");
        userlist = eventsDetails.getEventmember();
        subject.setText(eventsDetails.getName());
        description.setText(eventsDetails.getDesc());
        startdatevalue = eventsDetails.getStartDate();
        endDatevalue = eventsDetails.getEndDate();
        topicTV.setText(eventsDetails.getTopic());
        subtopicTV.setText(eventsDetails.getSubTopic());
        if (eventsDetails.getStartDate().length() > 16 && eventsDetails.getEndDate().length() > 16) {
            start_date.setText(CalenderUtils.convertTimeDateFormat(startdatevalue));
            end_date.setText(CalenderUtils.convertTimeDateFormat(endDatevalue));
        }

        jsonArray = new JSONArray();
        StringBuilder selectedUsers = new StringBuilder();
        for (int i = 0; i < userlist.size(); i++) {
            jsonArray.put(userlist.get(i).getId());
            if (i < userlist.size() - 1) {
                selectedUsers.append(userlist.get(i).getfName()).append(",");
            } else {
                selectedUsers.append(userlist.get(i).getfName()).append(" ");
            }
        }
        jsonArray.put(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId());
        attendeename.setVisibility(View.VISIBLE);
        attendeename.setText(selectedUsers.toString());
    }


    private void  showpopup() {
        /*Intent intent = new Intent(AddEventScreen.this, AttendeesListActivity.class);
        startActivity(intent);
        attendeesList = getIntent().getExtras().getString("attendeesList");*/
        /*StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < attendeesList.length() - 2; i++)
        {
            stringBuilder.append(attendeesList.charAt(i));
        }*/
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddEventScreen.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom, null);
        List<String> userNameList = new ArrayList<String>();
        alertDialog.setView(convertView);
        alertDialog.setTitle("Contacts");
        ListView lv = convertView.findViewById(R.id.userlist);
        lv.setAdapter(add_attendeee_adapter);
        FloatingActionButton addgroup = convertView.findViewById(R.id.addgroup);

        selectedContactsLinearLayout = findViewById(R.id.selected_contacts_container);

        addgroup.setOnClickListener(this);
        alertDialog.setPositiveButton("Add Ateendee", (dialog, which) -> {
            Collection<EventUser> userCollection = new HashSet<>(add_attendeee_adapter.getSelectedItems());
            List<EventUser> userList = new ArrayList<>(userCollection);
            jsonArray = new JSONArray();


            StringBuilder name = new StringBuilder();
            for (int i = 0; i < userList.size(); i++) {
                jsonArray.put(userList.get(i).getId());
                if (i < userList.size() - 1) {
                    name.append(userList.get(i).getfName()).append(" , ");
                } else {
                    name.append(userList.get(i).getfName()).append(" ");
                }
            }

            jsonArray.put(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId());
            if (userList.size() == 0) {
                Toast.makeText(getApplicationContext(), "At least 1 contact must be selected", Toast.LENGTH_SHORT).show();
                attendeename.setVisibility(View.GONE);

            } else {
                attendeename.setVisibility(View.VISIBLE);
                attendeename.setText(name.toString());
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }


    @Override
    public void onClick(View v) {
        if (v == addattendee) {
            showpopup();
        } else if (v == backbtn) {
            finish();
        } else if (v == submit) {
            if ((validateInputData())) {
                if (isUpdate) {
                    updateEvent(eventsDetails.getEventID(), jsonArray, subject.getText().toString(),topicTV.getText().toString(),subtopicTV.getText().toString(), type, startdatevalue, endDatevalue, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken(), location_meeeting, availablevalue, reminderValue, hashTagEditText.getValues(), false, false, description.getText().toString(), true);
                } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < hashTagEditText.getValues().size(); i++)
                        {
                            stringBuilder.append(hashTagEditText.getValues().get(i));
                        }
                        postEvent(HRpreference.getInstance(getApplicationContext()).getUserInfo().getId(), jsonArray,topicTV.getText().toString(),subtopicTV.getText().toString(), UUID.randomUUID().toString(), subject.getText().toString(), type, startdatevalue, endDatevalue, HRpreference.getInstance(getApplicationContext()).getUserInfo().getAuthToken(), location_meeeting, availablevalue, reminderValue, hashTagEditText.getValues(),false, false, description.getText().toString(), true);
                }
            }
        } else if (v == start_date_layout) {
            label = 0;
            new DatePickerDialog(AddEventScreen.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendarStartTime
                    .get(Calendar.YEAR), myCalendarStartTime.get(Calendar.MONTH),
                    myCalendarStartTime.get(Calendar.DAY_OF_MONTH)).show();

        } else if (v == end_date_layout) {
            label = 1;
            new DatePickerDialog(AddEventScreen.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendarEndTime
                    .get(Calendar.YEAR), myCalendarEndTime.get(Calendar.MONTH),
                    myCalendarEndTime.get(Calendar.DAY_OF_MONTH)).show();

        }
    }

    private void getFriendList(String id, String token) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
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
                        friendlist.addAll(yourList.get(0).getAssociateList());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                runOnUiThread(() -> {
                    //  add_attendeee_adapter = new Add_Attendeee_Adapter(getBaseContext(), friendlist,userlist);
                    add_attendeee_adapter.notifyDataSetChanged();
                    if (isUpdate) {
                        setSelectedValue();
                    }

                });
            }
        });
    }

    private void setSelectedValue() {
        for (EventUser eventUser1 : userlist) {
            for (EventUser eventUser2 : friendlist) {
                if (eventUser2.getId().equalsIgnoreCase(eventUser1.getId())) {
                    add_attendeee_adapter.addValueInSelectedItem(eventUser2);
                    break;
                }
            }
        }
        add_attendeee_adapter.notifyDataSetChanged();
    }


    private void postEvent(String id, JSONArray members,String topic,String subTopic,String roomName, String eventname, Integer type, String startdateevent, String enddateevent, String token, String postlocation, Integer availability, Integer postreminder, List<String> hashTag, Boolean isAllDay, Boolean isPrivate, String desc, Boolean isActive) {


        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforPostevent(id, members,topic,subTopic,roomName, eventname, type, startdateevent, enddateevent, token, postlocation,availability, postreminder, hashTagEditText.getValues(), isAllDay, isPrivate, desc, isActive, new Callback() {
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

                    runOnUiThread(() -> {
                        Toast.makeText(AddEventScreen.this, "Successfully Event Create", Toast.LENGTH_SHORT).show();
                        if (addEventType == 1) {
                            EventDashboardFragment.isListRefresh = true;
                        } else if (addEventType == 2) {
                            ProfileScreenFragment.isProfileListUpdate = true;
                        }
                        finish();
                    });


                } else {
                    runOnUiThread(() -> Toast.makeText(AddEventScreen.this, "Please Try Again", Toast.LENGTH_SHORT).show());

                }
            }
        });
    }

    private void updateEvent(String id, JSONArray members, String eventname,String topic,String subtopic, Integer type, String startdateevent, String enddateevent, String token, String postlocation, Integer availability, Integer postreminder, List<String> hashTag, Boolean isAllDay, Boolean isPrivate, String desc, Boolean isActive) {
        EventRequest request = new EventRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestForUpdateEvent(id, members, eventname,topic,subtopic, type, startdateevent, enddateevent, token, postlocation, availability, postreminder, hashTagEditText.getValues(), isAllDay, isPrivate, desc, isActive, new Callback() {
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
                    runOnUiThread(() -> {
                        finish();
                    });


                } else {
                    runOnUiThread(() -> Toast.makeText(AddEventScreen.this, "Please Try Again", Toast.LENGTH_SHORT).show());

                }
            }
        });
    }

    private void timepicker() {
        final Calendar c = Calendar.getInstance();
        startTimemHour = c.get(Calendar.HOUR_OF_DAY);
        startTimeMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                (view, hourOfDay, minute) -> {
                    startTimemHour = hourOfDay;
                    startTimeMinute = minute;
                    start_date.setText((new StringBuilder().append(startdatevalue).append(" ").append(hourOfDay).append(":").append(minute)).toString());
                    startdatevalue = startdatevalue + " " + hourOfDay + ":" + minute;
                    startTimemHour = hourOfDay;
                    startTimeMinute = minute;
                }, startTimemHour, startTimeMinute, false);
        timePickerDialog.show();
    }

    private void timeend() {
        final Calendar c = Calendar.getInstance();
        endTimehours = c.get(Calendar.HOUR_OF_DAY);
        endTimeminute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT,
                (view, hourOfDay, minute) -> {
                    endTimehours = hourOfDay;
                    endTimeminute = minute;
                    if (isBeforeTime(myCalendarStartTime, myCalendarEndTime, startTimemHour, startTimeMinute, endTimehours, endTimeminute)) {
                        showAlert("End time should after Start time");
                    } else {
                        end_date.setText((new StringBuilder().append(endDatevalue).append(" ").append(hourOfDay).append(":").append(minute)).toString());
                        endDatevalue = endDatevalue + " " + hourOfDay + ":" + minute;
                    }

                }, endTimehours, endTimeminute, false);
        timePickerDialog.show();
    }


    public boolean validateInputData() {

        String subjectv = subject.getText().toString().trim();
        String startdatev = startdatevalue;
        String enddatev = endDatevalue;
        String desc = description.getText().toString().trim();
        String loctionv = location_meeeting;
        if (jsonArray != null && jsonArray.length() > 0) {
            if (!RedHorizonValidator.isEmpty(subjectv)) {
                if (!RedHorizonValidator.isEmpty(loctionv)) {
                    if (!RedHorizonValidator.isEmpty(startdatev)) {
                        if (!RedHorizonValidator.isEmpty(enddatev)) {
                            if (!RedHorizonValidator.isEmpty(desc)) {

                            } else {
                                Toast.makeText(AddEventScreen.this, "Please Enter Description", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                        } else {
                            Toast.makeText(AddEventScreen.this, "Please Enter end date", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                    } else {
                        Toast.makeText(AddEventScreen.this, "Please Enter start date", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(AddEventScreen.this, "Please Enter your location", Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(AddEventScreen.this, "Please Enter your subject", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(AddEventScreen.this, "Please Add Attendee", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    private boolean isBeforeDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false;
        return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isBeforeTime(Calendar cal1, Calendar cal2, int startTimeHours, int startTimeMin, int endTimeHours, int endTimeMin) {
        boolean status=false;
        if (isSameDay(cal1, cal2)) {
            if(startTimeHours==endTimeHours && startTimeMin > endTimeMin)
                status=true;
            else if(startTimeHours>endTimeHours){
                status=true;
            }
            return status;
        }
        return false;
    }
}

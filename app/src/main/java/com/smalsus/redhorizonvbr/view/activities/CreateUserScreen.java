package com.smalsus.redhorizonvbr.view.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.RedHorizonValidator;
import com.smalsus.redhorizonvbr.networkrequest.HrApiRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateUserScreen extends AppCompatActivity implements View.OnClickListener {
    private TextView link_login,input_dob;
    private EditText firstname, lastname, email, username, password, phoneno;
    private Button btn_signup;
    private RadioGroup gender;
    private int gendername = 1;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private ProgressDialog dialog;
    private RelativeLayout maleRadioButtonContainer, femaleRadioButtonContainer;
    private RadioButton radioButtonmale,radioButtonFemale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_screen);
        dialog = new ProgressDialog(this);
        link_login = findViewById(R.id.link_login);
        firstname = findViewById(R.id.input_name);
        myCalendar = Calendar.getInstance();
        radioButtonFemale=findViewById(R.id.female);
        radioButtonmale=findViewById(R.id.male);
        myCalendar.add(Calendar.YEAR, -15);
        lastname = findViewById(R.id.input_Lastname);
        email = findViewById(R.id.input_email);
        username = findViewById(R.id.input_username);
        phoneno = findViewById(R.id.input_phone);
        password = findViewById(R.id.input_password);
        btn_signup = findViewById(R.id.btn_signup);
        gender = findViewById(R.id.gender);
        btn_signup.setOnClickListener(this);
        link_login.setOnClickListener(this);
        input_dob = findViewById(R.id.input_dob);
        input_dob.setOnClickListener(this);
        maleRadioButtonContainer = findViewById(R.id.maleRadioButtonContainer);
        femaleRadioButtonContainer = findViewById(R.id.femaleRadioButtonContainer);
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        gendername=1;
                        maleRadioButtonContainer.setBackgroundColor(getResources().getColor(R.color.selected_radio_btn_color));
                        maleRadioButtonContainer.setBackgroundColor(getResources().getColor(R.color.grey_transparent_10));
                        /*radioButtonmale.setBackgroundColor(getResources().getColor(R.color.selected_radio_btn_color));
                        radioButtonFemale.setBackgroundColor(getResources().getColor(R.color.grey_transparent_10));*/
                        break;
                    case R.id.female:
                        gendername = 2;
                        femaleRadioButtonContainer.setBackgroundColor(getResources().getColor(R.color.selected_radio_btn_color));
                        femaleRadioButtonContainer.setBackgroundColor(getResources().getColor(R.color.grey_transparent_10));
                        /*radioButtonmale.setBackgroundColor(getResources().getColor(R.color.grey_transparent_10));
                        radioButtonFemale.setBackgroundColor(getResources().getColor(R.color.selected_radio_btn_color));*/
                        break;
                }
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        input_dob.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean validateInputData() {
        String fname = firstname.getText().toString().trim();
        String lname = lastname.getText().toString().trim();
        String Uemail = email.getText().toString().trim();
        String UserName = username.getText().toString().trim();
        String paswd = password.getText().toString().trim();
        String phoneNo = phoneno.getText().toString().trim();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
        String UDob = sdf.format(myCalendar.getTime());
        int gender = gendername;
        if (!RedHorizonValidator.isEmpty(fname)) {
            if (!RedHorizonValidator.isEmpty(lname)) {
                if (RedHorizonValidator.isValidEmail(Uemail)) {
                    if (!RedHorizonValidator.isEmpty(UserName)) {
                        if (!RedHorizonValidator.isEmpty(paswd)) {
                            if (!RedHorizonValidator.isEmpty(phoneNo)) {
                                if (!RedHorizonValidator.isEmpty(UDob)) {
                                    if (!RedHorizonValidator.isEmpty(String.valueOf(gender))) {

                                    } else {
                                        showDialogWithOkButton("Error", "Please Select your Gender");
                                        return false;
                                    }
                                } else {
                                    showDialogWithOkButton("Error", "Please Enter your DOB");
                                    return false;
                                }
                            } else {
                                showDialogWithOkButton("Error", "Please Enter your Phone No");
                                return false;
                            }
                        } else {
                            showDialogWithOkButton("Error", "Please Enter your Password");
                            return false;
                        }
                    } else {
                        showDialogWithOkButton("Error", "Please Enter your Username");
                        return false;
                    }
                } else {
                    showDialogWithOkButton("Error", "Please Enter your Valid Email ID");
                    return false;
                }
            } else {
                showDialogWithOkButton("Error", "Please Enter your Lastname");
                return false;
            }
        } else {
            showDialogWithOkButton("Error", "Please Enter your Firstname");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == link_login) {
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
        } else if (v == btn_signup) {
            if (validateInputData()) {
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
                createUSer(phoneno.getText().toString(), firstname.getText().toString(), lastname.getText().toString(), gendername, sdf.format(myCalendar.getTime()), email.getText().toString(), username.getText().toString(), password.getText().toString());
            }
        } else if (v == input_dob) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateUserScreen.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        }
    }

    private void createUSer(String Phoneno, String Fname, String Lname, int gender, String Dob, String Emailid, String Username, String Pasword) {
        HrApiRequest request = new HrApiRequest();
        dialog.setMessage("please wait");
        dialog.show();
        request.requestforCreateUSer(Phoneno, Fname, Lname, gender, Dob, Emailid, Username, Pasword,false, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                Toast.makeText(CreateUserScreen.this,  e.getLocalizedMessage(), Toast
                        .LENGTH_SHORT).show();
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                dialog.dismiss();
                if (statusCode == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(CreateUserScreen.this, "Successfully User Created", Toast
                                .LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateUserScreen.this, LoginScreen.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CreateUserScreen.this, "User Already Exist ", Toast
                            .LENGTH_LONG).show());
                }
            }
        });
    }

    public void showDialogWithOkButton(String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(getString(R.string.ok_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
    /*public void makePasswordVisible(View view)
    {
        ImageView show = findViewById(R.id.visibilityImage);
        if(view.getId() == R.id.visibilityImage)
        {
            if(password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
            {
                show.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else
            {
                show.setImageResource(R.drawable.ic_visibility_on_black_24dp);
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }*/
}

package com.bruno.gpmap.manage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bruno.gpmap.GPSTracker;
import com.bruno.gpmap.R;
import com.firebase.client.Firebase;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mFullName;
    private View mLoginFormView;
    private Spinner spGender;
    private int posGender = 0;
    private Spinner spHeight;
    private int posHeight = 0;
    private GPSTracker gps;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Inicializa o GPS
        gps = new GPSTracker(Register.this);

        // check if GPS enabled
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mFullName = (EditText) findViewById(R.id.name);

        spGender = (Spinner)findViewById(R.id.gender);
        spGender.setSelection(posGender);

        spHeight = (Spinner)findViewById(R.id.height);
        spHeight.setSelection(posHeight);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.cad_register_form);
    }

    private void attemptRegister() {
        String email = mEmailView.getText().toString();
        String name = mFullName.getText().toString();
        String password = mPasswordView.getText().toString();
        String gender = spGender.getSelectedItem().toString();
        String height = spHeight.getSelectedItem().toString();

        //Verificação do Genero
        if(spGender.getSelectedItemPosition() <=0)
            Toast.makeText(Register.this, getString(R.string.select_gender), Toast.LENGTH_SHORT).show();

        //Verificação do Altura
        if(spHeight.getSelectedItemPosition() <=0)
            Toast.makeText(Register.this, getString(R.string.select_height), Toast.LENGTH_SHORT).show();


//        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> main, View view, int position,
//                                       long Id) {
//                if (position <= 0) {
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        try {
            createRegister(email,name, password, gender, height);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void createRegister(String email, String name, String password, String gender, String height){
        final Firebase ref = new Firebase("https://gpmap.firebaseio.com/users");
        ref.child("email").setValue(email);
        ref.setValue(email);
        ref.child(email).child("name").setValue(name);
        ref.child(email).child("password").setValue(password);
        ref.child(email).child("gender").setValue(gender);
        ref.child(email).child("height").setValue(height);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog, this, year, month, day);
//            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }
}

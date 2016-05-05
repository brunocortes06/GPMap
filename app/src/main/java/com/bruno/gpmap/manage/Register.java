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

import com.bruno.gpmap.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Calendar;
import java.util.Map;

public class Register extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mFullName;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mFullName = (EditText) findViewById(R.id.name);

        Spinner sp = (Spinner)findViewById(R.id.gender);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    // get spinner value
                }else{
                    // show toast select gender
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        String password = mPasswordView.getText().toString();

        try {
            createRegister(email);
//            Firebase ref = new Firebase("https://gpmap.firebaseio.com");
//            ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
//                @Override
//                public void onSuccess(Map<String, Object> result) {
//                    Toast.makeText(Register.this,"Successfully created user account with uid: " + result.get("uid"), Toast.LENGTH_LONG).show();
//                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
//                }
//                @Override
//                public void onError(FirebaseError firebaseError) {
//                    switch (firebaseError.getCode()) {
//                        case FirebaseError.USER_DOES_NOT_EXIST:
//                            // handle a non existing user
//                            Toast.makeText(Register.this,"Usuário inválido", Toast.LENGTH_LONG).show();
//                            break;
//                        case FirebaseError.INVALID_PASSWORD:
//                            // handle an invalid password
//                            System.out.println("Senha inválida");
//                            break;
//                        default:
//                            // handle other errors
//                            System.out.println("Erro no login");
//                            break;
//                    }
//                    System.out.println("ERRO");
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }


}
    private void createRegister(String email){
        final Firebase ref = new Firebase("https://gpmap.firebaseio.com");
        ref.child("users").setValue(email);
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

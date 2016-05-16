package com.bruno.gpmap.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bruno.gpmap.R;
import com.bruno.gpmap.util.GPSTracker;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{

    private Firebase firebase;

    EditText emailEditText;

    EditText passwordEditText;

    private EditText mFullName;

    private Spinner spGender;

    private Spinner spAge;

    private int posGender = 0;

    private int posAge = 0;

    private double lat;

    private double lng;

    private Firebase.ValueResultHandler<Map<String, Object>> registerCallback
            = new Firebase.ValueResultHandler<Map<String, Object>>()
    {

        @Override
        public void onSuccess (Map<String, Object> stringObjectMap)
        {
            finishRegister(stringObjectMap.get("uid").toString());
        }

        @Override
        public void onError (FirebaseError firebaseError)
        {

            showError();
        }
    };

    private void showError ()
    {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    private void finishRegister (String uid)
    {
        Firebase ref = new Firebase("https://gpmap.firebaseio.com/users");
        ref.child(uid).child("name").setValue(mFullName.getText().toString());
        ref.child(uid).child("gender").setValue(spGender.getSelectedItem().toString());
        ref.child(uid).child("age").setValue(spAge.getSelectedItem().toString());

        updateFirebaseLocation(uid, lat, lng);
        finish();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        GPSTracker gps = new GPSTracker(this);
        // check if GPS enabled
        if(gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        }

        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);

        mFullName = (EditText) findViewById(R.id.name);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://gpmap.firebaseio.com/");

        spGender = (Spinner)findViewById(R.id.spinnerGender);
        spGender.setSelection(posGender);

        spAge = (Spinner)findViewById(R.id.spinnerAge);
        spAge.setSelection(posAge);

    }

    public void register (View view)
    {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String name = mFullName.getText().toString();

        if(name.equals(null) || name.equals("")) {
            Toast.makeText(this, "Preencha o Nome", Toast.LENGTH_SHORT).show();
            return;
        }
        if(spGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o Genero", Toast.LENGTH_SHORT).show();
            return;
        }
        if(spAge.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione a idade", Toast.LENGTH_SHORT).show();
            return;
        }
        firebase.createUser(email, password, registerCallback);

        //            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//            mEmailView.startAnimation(shake);
//            focusView = mEmailView;
    }

    public static void start (Context context)
    {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    private void updateFirebaseLocation (String uid, double latitude, double longitude)
    {
        GeoFire geoFire = new GeoFire(firebase.child("locations"));
        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        geoFire.setLocation(uid, geoLocation);
    }
}

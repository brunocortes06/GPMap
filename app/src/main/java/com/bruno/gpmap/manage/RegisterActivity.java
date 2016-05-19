package com.bruno.gpmap.manage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bruno.gpmap.R;
import com.bruno.gpmap.util.GPSTracker;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText emailEditText;

    EditText passwordEditText;

    private EditText mFullName;

    private Spinner spGender;

    private Spinner spAge;

    private int posGender = 0;

    private int posAge = 0;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private double lat;

    private double lng;

    private String TAG = "Register";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

//    private Firebase.ValueResultHandler<Map<String, Object>> registerCallback
//            = new Firebase.ValueResultHandler<Map<String, Object>>()
//    {
//
//        @Override
//        public void onSuccess (Map<String, Object> stringObjectMap)
//        {
//            finishRegister(stringObjectMap.get("uid").toString());
//        }
//
//        @Override
//        public void onError (FirebaseError firebaseError)
//        {
//
//            showError();
//        }
//    };

    private void showError() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    private void finishRegister(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://gpmap.firebaseio.com/users");
        ref.child(uid).child("name").setValue(mFullName.getText().toString());
        ref.child(uid).child("gender").setValue(spGender.getSelectedItem().toString());
        ref.child(uid).child("age").setValue(spAge.getSelectedItem().toString());
        updateFirebaseLocation(uid, lat, lng);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        GPSTracker gps = new GPSTracker(this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        }

        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);

        mFullName = (EditText) findViewById(R.id.name);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        spGender = (Spinner) findViewById(R.id.spinnerGender);
        spGender.setSelection(posGender);

        spAge = (Spinner) findViewById(R.id.spinnerAge);
        spAge.setSelection(posAge);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void register(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String name = mFullName.getText().toString();

        if (name.equals(null) || name.equals("")) {
            Toast.makeText(this, "Preencha o Nome", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o Genero", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spAge.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione a idade", Toast.LENGTH_SHORT).show();
            return;
        }
        createUser(email, password);

        //            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//            mEmailView.startAnimation(shake);
//            focusView = mEmailView;
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        finishRegister(task.getResult().getUser().getUid());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    private void updateFirebaseLocation(String uid, double latitude, double longitude) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        GeoFire geoFire = new GeoFire(rootRef.child("locations"));
        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        geoFire.setLocation(uid, geoLocation);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mAuth.addAuthStateListener(mAuthListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bruno.gpmap.manage/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.bruno.gpmap.manage/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }
}

package com.bruno.gpmap.manage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bruno.gpmap.R;
//import com.bruno.gpmap.map.MapsActivity;
import com.bruno.gpmap.map.MapsActivity;
import com.bruno.gpmap.util.GPSTracker;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference firebase;

    private FirebaseAuth mAuth;

    private EditText emailEditText;

    private EditText passwordEditText;

    private View mLoginFormView;

    private View mProgressView;

    private Toolbar mToolbar;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private ImageView rocketImage;
    private boolean ret = false;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "Login";
    private CallbackManager mCallbackManager;
    private String age;
    private String email;
    private String name;
    private String gender;
    private boolean loginFace = false;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void showError(){
        showProgress(false);
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    private void goMapActivity(String uid) {
        finish();
        MapsActivity.start(this, uid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseAuth.getInstance().signOut();
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bruno.gpmap",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    if(loginFace)
                        registerUserData(user.getUid());

                    setLocation(user.getUid());

                    // User is signed in
                    //login automatico
                    goMapActivity(user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        AccessToken.getCurrentAccessToken();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_birthday");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code
                                try {
                                    String birthday = object.getString("birthday"); // 01/31/1980 format

                                    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                    Date date = (Date)formatter.parse(birthday);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);
                                    Calendar today = Calendar.getInstance();
                                    int ageInt = today.get(Calendar.YEAR) - cal.get((Calendar.YEAR));
                                    age = String.valueOf(ageInt);
                                    email = object.getString("email");
                                    name = object.getString("first_name");
                                    gender = object.getString("gender");
                                    if(gender.equals("male"))
                                        gender = "Masculino";

                                    if(gender.equals("female"))
                                        gender = "Feminino";

                                    loginFace = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);

        firebase = FirebaseDatabase.getInstance().getReference();

        rocketImage = (ImageView) findViewById(R.id.idBotao);
        rocketImage.setBackgroundResource(R.drawable.anim_but);

        rocketImage.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(mToolbar);
    }

    private void setLocation(String uid) {
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("locations"));
        GPSTracker gps = new GPSTracker(this);
        // check if GPS enabled
        double lat = 0;
        double lng = 0;
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        }
        GeoLocation geoLocation = new GeoLocation(lat, lng);
        geoFire.setLocation(uid, geoLocation);
    }

    private void registerUserData(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://gpmap.firebaseio.com/users");
        ref.child(uid).child("name").setValue(name);
        ref.child(uid).child("gender").setValue(gender);
        ref.child(uid).child("age").setValue(age);
        ref.child(uid).child("tel").setValue("xx xxxxx-xxxx");
    }


    public void login() {
        try {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if(!task.isSuccessful()){
                                Log.w(TAG, "signInWithEmail", task.getException());
                                Toast.makeText(LoginActivity.this, "Login inválido, tente novamente!.",
                                        Toast.LENGTH_LONG).show();
                                showProgress(false);
                                return;
                            }
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            goMapActivity(task.getResult().getUser().getUid());
                            if (!task.isSuccessful()) {
                                showProgress(false);
                                Log.w(TAG, "signInWithEmail", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void register(View view) {
        RegisterActivity.start(this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        AnimationDrawable rocketAnimation = (AnimationDrawable) rocketImage.getBackground();
        rocketAnimation.start();

        if( emailEditText.getText().toString().equals(null) || emailEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Digite o e-mail!", Toast.LENGTH_LONG).show();
            return;
        }
        if( passwordEditText.getText().toString().equals(null) || passwordEditText.getText().toString().equals("")){
            Toast.makeText(this, "Digite a senha!", Toast.LENGTH_LONG).show();
            return;
        }

        checkIfAnimationDone(rocketAnimation);
    }

    private boolean checkIfAnimationDone(AnimationDrawable anim){
        final AnimationDrawable a = anim;
        int timeBetweenChecks = 300;
        Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() - 1)){
                    checkIfAnimationDone(a);
                } else{
                    showProgress(true);
                    login();
                }
            }
        }, timeBetweenChecks);
        return ret;
    };

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.cad, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        if (item.getItemId() == R.id.reg)
        {
            Intent complete_reg = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(complete_reg);
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
//                        goMapActivity((credential.getProvider()));
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

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
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{

    private Firebase firebase;

    EditText emailEditText;

    EditText passwordEditText;

    private EditText mFullName;

    private Spinner spGender;

    private int posGender = 0;

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
        finish();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);

        mFullName = (EditText) findViewById(R.id.name);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://gpmap.firebaseio.com/");

        spGender = (Spinner)findViewById(R.id.spinnerGender);
        spGender.setSelection(posGender);
    }

    public void register (View view)
    {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(mFullName.equals(null))
            Toast.makeText(this, "Preencha o Nome", Toast.LENGTH_SHORT).show();

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
}

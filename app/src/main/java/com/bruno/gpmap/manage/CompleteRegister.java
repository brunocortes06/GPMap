package com.bruno.gpmap.manage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bruno.gpmap.R;
import com.bruno.gpmap.model.User;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class CompleteRegister extends AppCompatActivity {

    private Map<Marker, User> otherUsersData;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);
        otherUsersData = new HashMap<>();

        uid = getIntent().getExtras().getString("uid");
        System.out.println(CompleteRegister.class+uid);
//        otherUsersData = (Map<Marker, User>)getIntent().getSerializableExtra("User");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

//    public static void start (Context context, User user)
//    {
//        Intent intent = new Intent(context, CompleteRegister.class);
//        intent.putExtra("User", user);
//        context.startActivity(intent);
//    }
}

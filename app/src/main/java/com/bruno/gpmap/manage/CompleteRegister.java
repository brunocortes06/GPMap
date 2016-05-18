package com.bruno.gpmap.manage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bruno.gpmap.R;
import com.bruno.gpmap.model.User;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class CompleteRegister extends AppCompatActivity {

    private Map<Marker, User> otherUsersData;

    private String uid;

    private Spinner spinnerHair;

    private Spinner spinnerSkin;

    private int posHair = 0;

    private int posSkin = 0;

    private EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);
        otherUsersData = new HashMap<>();

        uid = getIntent().getExtras().getString("uid");
        System.out.println(CompleteRegister.class+uid);
//        otherUsersData = (Map<Marker, User>)getIntent().getSerializableExtra("User");

        spinnerHair = (Spinner)findViewById(R.id.spinnerHair);
        spinnerHair.setSelection(posHair);

        spinnerSkin = (Spinner)findViewById(R.id.spinnerSkin);
        spinnerSkin.setSelection(posSkin);

        desc = (EditText)findViewById(R.id.editTextDesc);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void send(View view){

        if(desc.equals(null) || desc.equals("")) {
            Toast.makeText(this, "Preencha a descrição", Toast.LENGTH_SHORT).show();
            return;
        }
        if(spinnerHair.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione a cor do cabelo", Toast.LENGTH_SHORT).show();
            return;
        }
        if(spinnerSkin.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o tom de pele", Toast.LENGTH_SHORT).show();
            return;
        }
        addInfo();
    }

    private void addInfo(){
        Firebase ref = new Firebase("https://gpmap.firebaseio.com/users");
        ref.child(uid).child("hair").setValue(spinnerHair.getSelectedItem().toString());
        ref.child(uid).child("skin").setValue(spinnerSkin.getSelectedItem().toString());
        ref.child(uid).child("description").setValue(desc.getText().toString());

        finish();
    }

//    public static void start (Context context, User user)
//    {
//        Intent intent = new Intent(context, CompleteRegister.class);
//        intent.putExtra("User", user);
//        context.startActivity(intent);
//    }
}

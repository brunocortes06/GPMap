package com.bruno.gpmap.manage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bruno.gpmap.R;
import com.bruno.gpmap.model.User;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private EditText tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);
        otherUsersData = new HashMap<>();

        uid = getIntent().getExtras().getString("uid");
        System.out.println(CompleteRegister.class+uid);

        spinnerHair = (Spinner)findViewById(R.id.spinnerHair);
        spinnerHair.setSelection(posHair);

        spinnerSkin = (Spinner)findViewById(R.id.spinnerSkin);
        spinnerSkin.setSelection(posSkin);

        desc = (EditText)findViewById(R.id.editTextDesc);

        tel = (EditText)findViewById(R.id.editTextTel);
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
        if(tel.equals(null) || tel.equals("")) {
            Toast.makeText(this, "Preencha o telefone", Toast.LENGTH_SHORT).show();
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(uid).child("hair").setValue(spinnerHair.getSelectedItem().toString());
        ref.child(uid).child("skin").setValue(spinnerSkin.getSelectedItem().toString());
        ref.child(uid).child("description").setValue(desc.getText().toString());
        ref.child(uid).child("tel").setValue(tel.getText().toString());

        finish();
    }

}

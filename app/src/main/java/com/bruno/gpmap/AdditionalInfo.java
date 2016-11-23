package com.bruno.gpmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bruno.gpmap.map.MapsActivity;
import com.bruno.gpmap.model.User;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Bruno on 16/05/2016.
 */
public class AdditionalInfo extends AppCompatActivity {

    private String key;
    private User user;
    private TextView mFullName;
    private TextView mAge;
    private TextView mHair;
    private TextView mGender;
    private TextView mSkin;
    private TextView mtel;
    private TextView mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additionalinfo);
        key = getIntent().getExtras().getString("key");
        getUserData(key);
        mFullName = (TextView) findViewById(R.id.textName);
        mAge = (TextView) findViewById(R.id.textAge);
        mHair = (TextView) findViewById(R.id.textHair);
        mGender = (TextView) findViewById(R.id.textGender);
        mSkin = (TextView) findViewById(R.id.textSkin);
        mtel = (TextView) findViewById(R.id.textTel);
        mDesc = (TextView) findViewById(R.id.textDesc);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getUserData(String key) {
        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUser.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = (snapshot.getValue(User.class));
                if(user.getName() != null) {
                    mFullName.setText("Nome: " + user.getName());
                }else{
                    mFullName.setText("Nome: -");
                }
                if(user.getAge() != null) {
                    mAge.setText("Idade: " + user.getAge());
                }else{
                    mAge.setText("Idade: -");
                }
                if(user.getHair() != null) {
                    mHair.setText("Cabelo: " + user.getHair());
                }else{
                    mHair.setText("Cabelo: -");
                }
                if(user.getGender() != null) {
                    mGender.setText("Gênero: " + user.getGender());
                }else{
                    mGender.setText("Gênero: -");
                }
                if(user.getSkin() != null) {
                    mSkin.setText("Cor da pele: " + user.getSkin());
                }else{
                    mSkin.setText("Cor da pele: -");
                }
                if(user.getDescription() != null) {
                    mDesc.setText("Descrição: " + user.getDescription());
                }else{
                    mDesc.setText("Descrição: -");
                }
                if(user.getTel() != null) {
                    mtel.setText("Telefone: " + user.getTel());
                }else{
                    mtel.setText("Telefone: -");
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        MapsActivity.start(this, key);
    }
}

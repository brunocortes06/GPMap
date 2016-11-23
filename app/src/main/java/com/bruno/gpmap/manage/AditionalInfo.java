package com.bruno.gpmap.manage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.bruno.gpmap.R;
import com.bruno.gpmap.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AditionalInfo extends AppCompatActivity {

    private EditText age;
    private EditText desc;
    private EditText gender;
    private EditText hair;
    private EditText skin;
    private EditText tel;
    private String uid;
    private String uidSelected;
    private static final String TAG = AditionalInfo.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        uid = getIntent().getExtras().getString("uid");
        uidSelected = getIntent().getExtras().getString("uidSelected");

        desc = (EditText)findViewById(R.id.editTextDescAD);
        tel = (EditText)findViewById(R.id.editTextTel);
        age = (EditText)findViewById(R.id.editTextAge);
        skin = (EditText)findViewById(R.id.editTextSkin);
        gender = (EditText)findViewById(R.id.editTextGender);
        hair = (EditText)findViewById(R.id.editTextHair);

        populate();
    }

    private void populate() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                desc.setText(user.getDescription());
                tel.setText(user.getTel().toString());
                age.setText(user.getAge());
                skin.setText(user.getSkin());
                gender.setText(user.getGender());
                hair.setText(user.getHair());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.child(uidSelected).addValueEventListener(dataListener);
    }
}

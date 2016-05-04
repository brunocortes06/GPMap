package com.bruno.gpmap.manage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Bruno on 03/05/2016.
 */
public class CreateUser {

    public void createUser(String mEmail, String mPassword){

        try {
            Firebase ref = new Firebase("https://gpmap.firebaseio.com");
            ref.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    System.out.println("ERRO");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

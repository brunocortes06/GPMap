package com.bruno.gpmap.manage;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bruno on 03/05/2016.
 */
public class LoginUser {

//    public void loginUser(String email, String password) {
//        try {
//            final Firebase ref = new Firebase("https://gpmap.firebaseio.com");
//            ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
////            ref.authWithPassword("bobtony@firebase.com", "correcthorsebatterystaple", new Firebase.AuthResultHandler() {
//                @Override
//                public void onAuthenticated(AuthData authData) {
//                    // Authentication just completed successfully :)
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("provider", authData.getProvider());
//                    if(authData.getProviderData().containsKey("displayName")) {
//                        map.put("displayName", authData.getProviderData().get("displayName").toString());
//                    }
//                    ref.child("users").child(authData.getUid()).setValue(map);
//                }
//                @Override
//                public void onAuthenticationError(FirebaseError firebaseError) {
//                    System.out.println("Erro no login");
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}

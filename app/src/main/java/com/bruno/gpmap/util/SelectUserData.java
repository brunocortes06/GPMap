//package com.bruno.gpmap.util;
//
//import com.bruno.gpmap.model.User;
//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.firebase.client.ValueEventListener;
//
///**
// * Created by Bruno on 16/05/2016.
// */
//public class SelectUserData {
//
//    public User userData = new User();
//
//    public User getUserData(String uid) {
//
//        Firebase ref = new Firebase("https://gpmap.firebaseio.com/users");
//        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    userData = userSnapshot.getValue(User.class);
//                }
////                System.out.println(snapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        });
//        return userData;
//    }
//}
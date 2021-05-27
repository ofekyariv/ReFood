package com.example.finalapp;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFireBase_RTDB {


    private static final String ADMIN_PATH = "Admin";
    private static final String USERS = "Users";
    private static final String EMAIL_PATH = "email";
    private static String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    public interface CallBack_DataIsReady {
        void getResult(Boolean data);
    }


    public static void checkIfAdmin(final CallBack_DataIsReady callBack_dataIsReady) {
        FirebaseDatabase.getInstance().getReference().child(ADMIN_PATH).child(EMAIL_PATH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshotAdminEmail) {

                        FirebaseDatabase.getInstance().getReference().child(USERS).child(currentUserID).child(EMAIL_PATH).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotUserEmail) {
                                        if (dataSnapshotAdminEmail.getValue().toString().equals(dataSnapshotUserEmail.getValue().toString())) {
                                            callBack_dataIsReady.getResult(true);
                                        } else {
                                            callBack_dataIsReady.getResult(false);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}

package com.example.vetandaid;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Start extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //checking if there is a user logged
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        if (Objects.equals(dataSnapshot.getKey(), "accType")) {
                            if (Objects.requireNonNull(dataSnapshot.getValue()).toString().trim().equals("client")) {
                                //starting the app without having to log in again
                                Intent i = new Intent(Start.this, ClientProfile.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } else if (Objects.requireNonNull(dataSnapshot.getValue()).toString().trim().equals("vet")) {
                                //starting the app without having to log in again
                                Intent i = new Intent(Start.this, VetProfile.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}

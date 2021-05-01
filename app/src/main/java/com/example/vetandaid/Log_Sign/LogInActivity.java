package com.example.vetandaid.Log_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vetandaid.ClientProfile;
import com.example.vetandaid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private EditText Email, Password;
    private Button Log;
    private ProgressBar Bar;
    private ViewGroup Cont;
    private FirebaseAuth fAuth;
    private FirebaseUser firebaseUser;

    private String accType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Log = findViewById(R.id.log_in);
        Bar = findViewById(R.id.progressBar);
        Cont = findViewById(R.id.container);

        fAuth = FirebaseAuth.getInstance();

        accType = " ";

        Log.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(Cont);
            Log.setVisibility(View.GONE);
            Bar.setVisibility(View.VISIBLE);

            validate(Email.getText().toString(), Password.getText().toString());
        });
    }

    private void validate(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(email)) {
                Email.setError("E-mail field is empty!");
                Email.requestFocus();
            }
            if (TextUtils.isEmpty(password)) {
                Password.setError("Password field is empty!");
                Password.requestFocus();
            }
            endLoading();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.setError("Invalid e-mail!");
            Email.requestFocus();
            endLoading();
            return;
        }
        if (password.length() < 6) {
            Password.setError("Password must be at least 6 characters long!");
            Password.requestFocus();
            endLoading();
            return;
        }

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //getting the account type from the database
                firebaseUser = fAuth.getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            if (firebaseUser.getUid().equals(dataSnapshot.getKey())) {
                                accType = Objects.requireNonNull(dataSnapshot.child("accType").getValue()).toString();

                                switch (accType) {
                                    case "client":
                                        startActivity(new Intent(LogInActivity.this, ClientProfile.class));
                                        endLoading();
                                        break;
                                    case "vet":
                                        //startActivity(new Intent(LogInActivity.this, VetProfile.class));
                                        endLoading();
                                        break;
                                    case "other":
                                        //startActivity(new Intent(LogInActivity.this, VetProfile.class));
                                        endLoading();
                                        break;
                                    default:
                                        Toast.makeText(LogInActivity.this, "Failed to login! Try again!", Toast.LENGTH_SHORT).show();
                                        endLoading();
                                        break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            } else {
                Toast.makeText(LogInActivity.this, "Failed to login! Try again!", Toast.LENGTH_SHORT).show();
                endLoading();
            }
        });
    }

    private void endLoading() {
        TransitionManager.beginDelayedTransition(Cont);
        Bar.setVisibility(View.GONE);
        Log.setVisibility(View.VISIBLE);
    }
}
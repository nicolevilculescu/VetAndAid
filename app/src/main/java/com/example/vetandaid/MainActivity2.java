package com.example.vetandaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.vetandaid.Log_Sign.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {

    TextView userEmail;
    Button logout, testing;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        userEmail = findViewById(R.id.profileEmai);
        logout = findViewById(R.id.button);
        testing = findViewById(R.id.button2);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        userEmail.setText(firebaseUser.getEmail());

        logout.setOnClickListener(v -> {
            //signing out
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        testing.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity2.this, ClientProfile.class);
            startActivity(intent);
        });
    }
}
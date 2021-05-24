package com.example.vetandaid.RegistrationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vetandaid.AddPet;
import com.example.vetandaid.R;
import com.example.vetandaid.model.MedicalHistory;
import com.example.vetandaid.model.Pet;
import com.example.vetandaid.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ClientFragment extends Fragment {

    private EditText Fname, Lname, Phone, Email, Password1, Password2;
    private ProgressBar Bar;
    private ViewGroup Cont;
    private Button Sign;
    private FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_client, container, false);

        Fname = v.findViewById(R.id.fname1);
        Lname = v.findViewById(R.id.lname1);
        Phone = v.findViewById(R.id.phone1);
        Email = v.findViewById(R.id.email1);
        Password1 = v.findViewById(R.id.password1);
        Password2 = v.findViewById(R.id.repassword1);
        Bar = v.findViewById(R.id.progressBar1);
        Cont = v.findViewById(R.id.container1);

        Sign = v.findViewById(R.id.sign_up1);

        fAuth = FirebaseAuth.getInstance();

        Sign.setOnClickListener(v1 -> {
            TransitionManager.beginDelayedTransition(Cont);
            Sign.setVisibility(View.GONE);
            Bar.setVisibility(View.VISIBLE);
            validate(Fname.getText().toString().trim(), Lname.getText().toString().trim(), Phone.getText().toString().trim(),
                    Email.getText().toString().trim(), Password1.getText().toString().trim(), Password2.getText().toString().trim());
       });

        return v;
    }

    private void validate(String fname, String lname, String phone, String email, String password1, String password2) {
        //Checking to see if the fields are empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
            if (TextUtils.isEmpty(email)) {
                Email.setError("E-mail field is empty!");
                Email.requestFocus();
            }
            if (TextUtils.isEmpty(fname)) {
                Fname.setError("First name field is empty!");
                Fname.requestFocus();
            }
            if (TextUtils.isEmpty(lname)) {
                Lname.setError("Last name field is empty!");
                Lname.requestFocus();
            }
            if (TextUtils.isEmpty(phone)) {
                Phone.setError("Phone number field is empty!");
                Phone.requestFocus();
            }
            if (TextUtils.isEmpty(password1)) {
                Password1.setError("Password field is empty!");
                Password1.requestFocus();
            }
            if (TextUtils.isEmpty(password2)) {
                Password2.setError("Repeat password field is empty!");
                Password2.requestFocus();
            }
            endLoading();
        }
        //Checking if the email address does match with the pattern
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Invalid e-mail!");
            Email.requestFocus();
            endLoading();
        }
        //Checking if the phone number does match with the pattern
        else if (!Patterns.PHONE.matcher(phone).matches()) {
            Phone.setError("Invalid phone number!");
            Phone.requestFocus();
            endLoading();
        }
        //Checking if the password has at least 6 characters
        else if (password1.length() < 6) {
            Password1.setError("Password must be at least 6 characters long!");
            Password1.requestFocus();
            endLoading();
        }
        //Checking if the 2 passwords match
        else if (!password1.equals(password2)) {
            Password2.setError("This password doesn't match with the first password!");
            Password1.requestFocus();
            Password2.requestFocus();
            endLoading();
        }
        else {
            //crating the new user in the Authentication DB of Firebase
            fAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Password1.getText().toString().trim()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User(Email.getText().toString().trim(), Password1.getText().toString().trim(),
                            Fname.getText().toString().trim(), Lname.getText().toString().trim(), Phone.getText().toString().trim(), "client");

                    //adding the user to the Realtime Database of Firebase
                    FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance()
                            .getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
                            endLoading();
                            startActivity(new Intent(getActivity(), AddPet.class));
                        } else {
                            Toast.makeText(getActivity(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                            endLoading();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                    endLoading();
                }
            });
        }
    }

    private void endLoading() {
        TransitionManager.beginDelayedTransition(Cont);
        Bar.setVisibility(View.GONE);
        Sign.setVisibility(View.VISIBLE);
    }
}

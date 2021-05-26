package com.example.vetandaid.RegistrationFragments;

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

import com.example.vetandaid.R;
import com.example.vetandaid.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class OtherFragment extends Fragment {

    private EditText Fname, Lname, Phone, Email, Password1, Password2;
    private ProgressBar Bar;
    private ViewGroup Cont;
    private Button Sign;
    private FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_other, container, false);

        Fname = v.findViewById(R.id.fname3);
        Lname = v.findViewById(R.id.lname3);
        Phone = v.findViewById(R.id.phone3);
        Email = v.findViewById(R.id.email3);
        Password1 = v.findViewById(R.id.password3);
        Password2 = v.findViewById(R.id.repassword3);
        Bar = v.findViewById(R.id.progressBar4);
        Cont = v.findViewById(R.id.container3);

        Sign = v.findViewById(R.id.sign_up3);

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
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Invalid e-mail!");
            Email.requestFocus();
            endLoading();
        }
        else if (!Patterns.PHONE.matcher(phone).matches()) {
            Phone.setError("Invalid phone number!");
            Phone.requestFocus();
            endLoading();
        }
        else if (password1.length() < 6) {
            Password1.setError("Password must be at least 6 characters long!");
            Password1.requestFocus();
            endLoading();
        }
        else if (!password1.equals(password2)) {
            Password2.setError("This password doesn't match with the first password!");
            Password1.requestFocus();
            Password2.requestFocus();
            endLoading();
        }
        else {
            fAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Password1.getText().toString().trim()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User(Email.getText().toString().trim(), Password1.getText().toString().trim(),
                            Fname.getText().toString().trim(), Lname.getText().toString().trim(), Phone.getText().toString().trim(), "other");

                    FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance()
                            .getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
                            endLoading();
                            //startActivity(new Intent(getActivity(), MainActivity2.class));
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

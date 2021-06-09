package com.example.vetandaid.RegistrationFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vetandaid.Log_Sign.VetFirstSign;
import com.example.vetandaid.R;
import com.example.vetandaid.model.Vet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VetFragment extends Fragment {

    private EditText Clinic, Registration, Fname, Lname, Phone, Address, Email, Password1, Password2;
    private ProgressBar Bar1, Bar2;
    private ViewGroup Cont;
    private Button Sign;
    private TextView Data1, Data2;
    private ImageView info1, info2;
    private FirebaseAuth fAuth;

    public static final String PREFS_VET_ID = "VetIdPrefsFile";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vet, container, false);

        Cont = v.findViewById(R.id.container2);
        Data1 = v.findViewById(R.id.data);
        Data2 = v.findViewById(R.id.data2);
        Email = v.findViewById(R.id.email2);
        Password1 = v.findViewById(R.id.password2);
        Password2 = v.findViewById(R.id.repassword2);
        Sign = v.findViewById(R.id.sign_up2);

        Clinic = v.findViewById(R.id.clinic);
        Registration = v.findViewById(R.id.registration);
        Fname = v.findViewById(R.id.fname2);
        Lname = v.findViewById(R.id.lname2);
        Phone = v.findViewById(R.id.phone2);
        Address = v.findViewById(R.id.address);

        Bar1 = v.findViewById(R.id.progressBar2);
        Bar2 = v.findViewById(R.id.progressBar3);

        fAuth = FirebaseAuth.getInstance();

        info1 = v.findViewById(R.id.infoIconRegNb);



        info1.setOnClickListener(v1 -> Toast.makeText(getActivity(), "Trade Register registration number of the type: J40/8118/2002", Toast.LENGTH_LONG)
                .show());

        info2 = v.findViewById(R.id.infoIcon2);

        info2.setOnClickListener(v1 -> Toast.makeText(getActivity(), "Address must look like: Street Name Number, City, Country", Toast.LENGTH_LONG).show());

        FloatingActionButton next = v.findViewById(R.id.next);

        next.setOnClickListener(v1 -> {
            if (validate1(Clinic.getText().toString().trim(), Registration.getText().toString().trim(), Fname.getText().toString().trim(),
                    Lname.getText().toString().trim(), Phone.getText().toString().trim(), Address.getText().toString().trim())) {
                TransitionManager.beginDelayedTransition(Cont);
                Data1.setVisibility(View.GONE);
                Clinic.setVisibility(View.GONE);
                Registration.setVisibility(View.GONE);
                Fname.setVisibility(View.GONE);
                Lname.setVisibility(View.GONE);
                Phone.setVisibility(View.GONE);
                Address.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                info1.setVisibility(View.GONE);
                info2.setVisibility(View.GONE);

                Data2.setVisibility(View.VISIBLE);
                Email.setVisibility(View.VISIBLE);
                Password1.setVisibility(View.VISIBLE);
                Password2.setVisibility(View.VISIBLE);
                Sign.setVisibility(View.VISIBLE);
            }
        });

        Sign.setOnClickListener(v1 -> {
            TransitionManager.beginDelayedTransition(Cont);
            Sign.setVisibility(View.GONE);
            Bar1.setVisibility(View.VISIBLE);
            validate(Clinic.getText().toString().trim(), Registration.getText().toString().trim(), Fname.getText().toString().trim(),
                    Lname.getText().toString().trim(), Phone.getText().toString().trim(), Address.getText().toString().trim(),
                    Email.getText().toString().trim(), Password1.getText().toString().trim(), Password2.getText().toString().trim());
        });

        return v;
    }

    private boolean validate1(String clinic, String registration, String fname, String lname, String phone, String address) {
        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(clinic) ||
                TextUtils.isEmpty(registration) || TextUtils.isEmpty(address)) {
            
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
            if (TextUtils.isEmpty(clinic)) {
                Clinic.setError("Clinic/Shop name field is empty!");
                Clinic.requestFocus();
            }
            if (TextUtils.isEmpty(registration)) {
                Registration.setError("Registration number field is empty!");
                Registration.requestFocus();
            }
            if (TextUtils.isEmpty(address)) {
                Address.setError("Address field is empty!");
                Address.requestFocus();
            }
            endLoading(Bar2);
            return false;
        }
        else if (!Patterns.PHONE.matcher(phone).matches()) {
            Phone.setError("Invalid phone number!");
            Phone.requestFocus();
            endLoading(Bar2);
            return false;
        }
        else if (!registration.matches("J[0-9]{2}/[0-9]{3,4}/[0-9]{4}")) {
            Registration.setError("Invalid registration number!");
            Registration.requestFocus();
            endLoading(Bar2);
            return false;
        }
        return true;
    }
    
    private void validate(String clinic, String registration, String fname, String lname, String phone, String address,
                          String email, String password1, String password2) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {

            if (TextUtils.isEmpty(email)) {
                Email.setError("E-mail field is empty!");
                Email.requestFocus();
            }
            if (TextUtils.isEmpty(password1)) {
                Password1.setError("Password field is empty!");
                Password1.requestFocus();
            }
            if (TextUtils.isEmpty(password2)) {
                Password2.setError("Repeat password field is empty!");
                Password2.requestFocus();
            }
            endLoading(Bar1, Sign);
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Invalid e-mail!");
            Email.requestFocus();
            endLoading(Bar1, Sign);
        }
        else if (password1.length() < 6) {
            Password1.setError("Password must be at least 6 characters long!");
            Password1.requestFocus();
            endLoading(Bar1, Sign);
        }
        else if (!password1.equals(password2)) {
            Password2.setError("This password doesn't match with the first password!");
            Password1.requestFocus();
            Password2.requestFocus();
            endLoading(Bar1, Sign);
        }
        else {
            fAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Password1.getText().toString().trim()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Vet vet = new Vet(email, password1, fname, lname, phone, address, "vet", registration, clinic);

                    FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance()
                            .getCurrentUser()).getUid()).setValue(vet).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
                            endLoading(Bar1, Sign);

                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            SharedPreferences settings = requireActivity().getSharedPreferences(PREFS_VET_ID, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("id", id);
                            editor.apply();

                            startActivity(new Intent(getActivity(), VetFirstSign.class));
                        } else {
                            Toast.makeText(getActivity(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                            endLoading(Bar1, Sign);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                    endLoading(Bar1, Sign);
                }
            });
        }
    }

    private void endLoading(@NotNull ProgressBar Bar) {
        TransitionManager.beginDelayedTransition(Cont);
        Bar.setVisibility(View.GONE);
    }

    private void endLoading(@NotNull ProgressBar Bar, Button Sign) {
        TransitionManager.beginDelayedTransition(Cont);
        Bar.setVisibility(View.GONE);
        Sign.setVisibility(View.VISIBLE);
    }
}

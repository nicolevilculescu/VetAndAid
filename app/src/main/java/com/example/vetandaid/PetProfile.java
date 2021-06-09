package com.example.vetandaid;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.RecyclerViews.Breeds;
import com.example.vetandaid.RecyclerViews.MedicalAdapter;
import com.example.vetandaid.model.MedicalHistory;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetProfile extends AppCompatActivity implements MedicalAdapter.RecyclerViewClickListener{

    public static final String PREFS_MEDICAL_ID = "MedicalIdPrefsFile";

    private DatabaseReference reference;
    private StorageReference storageReference;

    private TextView name, age, breed, editPic;
    private CircleImageView img;
    private EditText editName, editAge;
    private Button delete;

    private Uri imageUri;

    private TabHost tabHost;
    private MedicalAdapter adapter;

    private FloatingActionButton edit, done, breedButton;

    String id1, id2, birthdate, species, accType;

    public static final String EXTRA_CATEGORY = "extraCategory";
    public static final String EXTRA_INFO = "extraInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_PET_ID, Context.MODE_PRIVATE);
        id1 = settings.getString("id", "default");

        settings = getSharedPreferences(Constants.PREFS_ACC_TYPE, Context.MODE_PRIVATE);
        accType = settings.getString("accType", "default");

        name = findViewById(R.id.nameTextView);
        age = findViewById(R.id.ageTextView);
        breed = findViewById(R.id.breedTextView);

        edit = findViewById(R.id.editInfo);
        delete = findViewById(R.id.deletePet);
        done = findViewById(R.id.doneButton);

        if (accType.equals("client")) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }

        img = findViewById(R.id.profile_image);

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        storageReference = FirebaseStorage.getInstance().getReference("Pets");

        reference = FirebaseDatabase.getInstance().getReference().child("Pets").child(id1);
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Picasso.with(img.getContext())
                        .load(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("url").getValue()).toString().trim()).into(img);

                name.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("name").getValue()).toString().trim());

                birthdate = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("birthdate").getValue()).toString().trim();

                if (!birthdate.equals("-")) {
                    age.setText(String.valueOf(calculateAge(birthdate)));
                } else {
                    age.setText(getString(R.string.unknown));
                }

                breed.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("breed").getValue()).toString().trim());

                species = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("species").getValue()).toString().trim();
            }
        });

        recyclerView();
        tabSetup();

        //Edit info of a pet
        edit.setOnClickListener(v -> {
            delete.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            age.setVisibility(View.INVISIBLE);

            editName = findViewById(R.id.nameEditText);
            editAge = findViewById(R.id.ageEditText);
            breedButton = findViewById(R.id.breedButton);
            editPic = findViewById(R.id.editPicture);

            editName.setVisibility(View.VISIBLE);
            editAge.setVisibility(View.VISIBLE);
            editPic.setVisibility(View.VISIBLE);
            breedButton.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);

            editName.setText(name.getText().toString().trim());
            editAge.setText(birthdate);

            breedButton.setOnClickListener(v1 -> {
                Intent intent = new Intent(PetProfile.this, Breeds.class);
                intent.putExtra(EXTRA_CATEGORY, species);
                intent.putExtra(EXTRA_INFO, "1");
                startActivityForResult(intent, 1);
            });

            editPic.setOnClickListener(v1 -> {
                openFileChooser();
            });
        });

        //Updating info of a pet
        done.setOnClickListener(v -> {
            editName.setVisibility(View.INVISIBLE);
            editAge.setVisibility(View.INVISIBLE);
            editPic.setVisibility(View.INVISIBLE);
            breedButton.setVisibility(View.INVISIBLE);
            done.setVisibility(View.INVISIBLE);

            if (!name.getText().toString().trim().equals(editName.getText().toString().trim())) {
                name.setText(editName.getText().toString().trim());
                reference.child("name").setValue(name.getText().toString().trim());
            }
            if (!age.getText().toString().trim().equals(String.valueOf(calculateAge(editAge.getText().toString().trim())))) {
                birthdate = editAge.getText().toString().trim();
                age.setText(String.valueOf(calculateAge(birthdate)));
                reference.child("birthdate").setValue(birthdate);
            }

            updatePic();

            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            age.setVisibility(View.VISIBLE);
        });

        //Deleting a pet
        delete.setOnClickListener(v -> {
            reference.removeValue();
            startActivity(new Intent(PetProfile.this, ClientProfile.class));

            FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(id1).removeValue();
        });

        FloatingActionButton add = findViewById(R.id.addNew2);
        add.setOnClickListener(v1 -> startActivity(new Intent(PetProfile.this, AddMedicalHistory.class)));
    }

    private void tabSetup() {
        TabHost.TabSpec spec = tabHost.newTabSpec("Medical history");
        spec.setContent(tag -> findViewById(R.id.recView));
        spec.setIndicator("Medical History");
        tabHost.addTab(spec);
    }

    private void recyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<MedicalHistory> options = new FirebaseRecyclerOptions.Builder<MedicalHistory>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(id1), MedicalHistory.class).build();

        adapter = new MedicalAdapter(options, this);
        recyclerView.setAdapter(adapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updatePic() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while ((!uri.isComplete()));
                        Uri url = uri.getResult();
                        assert url != null;
                        reference.child("url").setValue(url.toString());
                    })
                    .addOnFailureListener(e -> Toast.makeText(PetProfile.this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onViewClick(int position) {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(id1);
        DatabaseReference finalReference = reference;
        reference.get().addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e("firebase", "Error getting data", task1.getException());
            } else {
                finalReference.get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        for (DataSnapshot dataSnapshot : Objects.requireNonNull(task.getResult()).getChildren()) {
                            if (Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString().equals(adapter.getItem(position).getDate()) &&
                                    Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString()
                                            .equals(adapter.getItem(position).getDescription()) &&
                                    Objects.requireNonNull(dataSnapshot.child("problem").getValue()).toString()
                                            .equals(adapter.getItem(position).getProblem()) &&
                                    Objects.requireNonNull(dataSnapshot.child("treatment").getValue()).toString()
                                            .equals(adapter.getItem(position).getTreatment())) {

                                id2 = dataSnapshot.getKey();

                                SharedPreferences settings = getSharedPreferences(PREFS_MEDICAL_ID, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("id", id2);
                                editor.apply();

                                startActivity(new Intent(PetProfile.this, MedicalHistoryActivity.class));
                            }
                        }
                    }
                });
            }
        });
    }

    private int calculateAge(String birthdate) {
        StringBuilder sb = new StringBuilder(birthdate);

        String day = sb.substring(0,2);
        String month = sb.substring(3,5);
        String year = sb.substring(6,10);

        LocalDate currentDate = LocalDate.now();                       //Today's date
        LocalDate birthDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return Period.between(birthDate, currentDate).getYears();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(img);

        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            String category = data.getStringExtra("result");
            breed.setKeyListener(null);
            breed.setText(category);
            reference.child("breed").setValue(breed.getText().toString().trim());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (accType.equals("client")) {
            startActivity(new Intent(PetProfile.this, ClientProfile.class));
        } else if (accType.equals("vet")){
            startActivity(new Intent(PetProfile.this, VetProfile.class));
        }
    }
}
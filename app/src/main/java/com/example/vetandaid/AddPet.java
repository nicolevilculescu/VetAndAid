package com.example.vetandaid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vetandaid.RecyclerViews.Breeds;
import com.example.vetandaid.model.Pet;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AddPet extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_CATEGORY = "extraCategory";
    public static final String EXTRA_INFO = "extraInfo";

    private String s;

    private Button breed, choose;
    private FloatingActionButton next, again;
    private ImageView pic;
    private TextView b, another;
    private EditText name, birth, bName;

    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Spinner spinner1 = findViewById(R.id.spinner1);

        name = findViewById(R.id.name);
        birth = findViewById(R.id.date);
        ImageView info = findViewById(R.id.infoIconBirth);
        bName = findViewById(R.id.breedName);
        bName.setKeyListener(null);

        breed = findViewById(R.id.breed);
        b = findViewById(R.id.option2);

        again = findViewById(R.id.again);

        choose = findViewById(R.id.add_picture);
        pic = findViewById(R.id.pic);
        another = findViewById(R.id.another);

        storageReference = FirebaseStorage.getInstance().getReference("Pets");
        databaseReference = FirebaseDatabase.getInstance().getReference("Pets");

        next = findViewById(R.id.next2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.species,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(this);

        info.setOnClickListener(v1 -> Toast.makeText(AddPet.this, "Birthdate must look like: dd.mm.yyyy. It can also be left empty.",
                Toast.LENGTH_LONG).show());

        choose.setOnClickListener(v -> openFileChooser());

        another.setOnClickListener(v -> openFileChooser());

        next.setOnClickListener(v -> {
            if (validate()) {
                uploadInfo();
                Intent intent = new Intent(AddPet.this, ClientProfile.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        s = selected;
        bName.setVisibility(View.GONE);
        again.setVisibility(View.GONE);
        if (selected.equals("Cat") || selected.equals("Dog") || selected.equals("Rabbit") || selected.equals("Rodent") ||
                selected.equals("Bird") || selected.equals("Lizard")) {
            next.setClickable(true);
            b.setVisibility(View.VISIBLE);
            breed.setVisibility(View.VISIBLE);
            breed.setOnClickListener(v-> {
                Intent intent = new Intent(AddPet.this, Breeds.class);
                intent.putExtra(EXTRA_CATEGORY, selected);
                intent.putExtra(EXTRA_INFO, "1");
                startActivityForResult(intent, 1);
            });
            again.setOnClickListener(v-> {
                Intent intent = new Intent(AddPet.this, Breeds.class);
                intent.putExtra(EXTRA_CATEGORY, selected);
                intent.putExtra(EXTRA_INFO, "1");
                startActivityForResult(intent, 1);
            });
        } else {
            breed.setVisibility(View.GONE);
            b.setVisibility(View.GONE);
            bName.setVisibility(View.GONE);
            again.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Animal species must be chosen!", Toast.LENGTH_SHORT).show();
        parent.requestFocus();
        next.setClickable(false);
    }

    private boolean validate() {
        if (s.equals("Cat") || s.equals("Dog") || s.equals("Rabbit") || s.equals("Rodent") || s.equals("Bird") || s.equals("Lizard") || s.equals("Fish") ||
                s.equals("Hedgehog") || s.equals("Snake") || s.equals("Turtle")) {
            if (name.getText().length() > 0) {
                if (s.equals("Cat") || s.equals("Dog") || s.equals("Rabbit") || s.equals("Rodent") || s.equals("Bird") || s.equals("Lizard")) {
                    if (bName.getText().length() == 0) {
                        Toast.makeText(this, "Breed must be chosen!", Toast.LENGTH_SHORT).show();
                        bName.requestFocus();
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    bName.setText("-");
                    return true;
                }
            } else {
                Toast.makeText(this, "Pet must have a name!", Toast.LENGTH_SHORT).show();
                name.requestFocus();
                return false;
            }
        } else {
            Toast.makeText(this, "Animal species must be chosen!", Toast.LENGTH_SHORT).show();
            return false;
        }
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

    private void uploadInfo() {
        if (birth.getText().length() == 0) {
            birth.setText("-");
        }
        if (imageUri == null) {
            Pet pet = new Pet(s, bName.getText().toString().trim(), name.getText().toString().trim(), birth.getText().toString().trim(),
                    "no photo", firebaseUser.getUid());
            String uploadId = databaseReference.push().getKey();
            assert uploadId != null;
            databaseReference.child(uploadId).setValue(pet);
        } else {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while ((!uri.isComplete()));
                        Uri url = uri.getResult();
                        assert url != null;
                        Pet pet = new Pet(s, bName.getText().toString().trim(), name.getText().toString().trim(), birth.getText().toString().trim(),
                                url.toString(), firebaseUser.getUid());
                        String uploadId = databaseReference.push().getKey();
                        assert uploadId != null;
                        databaseReference.child(uploadId).setValue(pet);
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddPet.this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            choose.setVisibility(View.INVISIBLE);
            Picasso.with(this).load(imageUri).into(pic);
            pic.setVisibility(View.VISIBLE);

            another.setVisibility(View.VISIBLE);
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String category = data.getStringExtra("result");
                bName.setVisibility(View.VISIBLE);
                breed.setVisibility(View.INVISIBLE);
                again.setVisibility(View.VISIBLE);
                bName.setKeyListener(null);
                bName.setText(category);
            }
            if (resultCode == RESULT_CANCELED) {
                bName.setText(getString(R.string.nothing_selected));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(AddPet.this, ClientProfile.class));
    }
}
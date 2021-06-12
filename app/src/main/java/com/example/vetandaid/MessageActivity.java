package com.example.vetandaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.RecyclerViews.MessagesAdapter;
import com.example.vetandaid.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    private TextView name;
    private EditText msg_text;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private List<Chat> list;

    String id, accType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_ACC_TYPE, Context.MODE_PRIVATE);
        accType = settings.getString("accType", "default");

        name = findViewById(R.id.name_chat);

        recyclerView = findViewById(R.id.recViewChat);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        msg_text = findViewById(R.id.text_send);
        ImageButton send = findViewById(R.id.btn_send);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        settings = getSharedPreferences(Constants.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        databaseReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                if (accType.equals("client")) {
                    name.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("clinic_name").getValue()).toString().trim());
                } else {
                    name.setText(getString(R.string.fullName,
                            Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("firstName").getValue()).toString().trim(),
                            Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("lastName").getValue()).toString().trim()));
                }

                readMessages(firebaseUser.getUid(), id);
            }
        });

        send.setOnClickListener(v -> {
            String message = msg_text.getText().toString().trim();

            if (!message.equals("")) {
                sendMessage(firebaseUser.getUid(), id, message);
            }

            msg_text.setText("");
        });
    }

    private void sendMessage(String uid, String id, String message) {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", uid);
        hashMap.put("receiver", id);
        hashMap.put("message", message);

        databaseReference.child("Chats").push().setValue(hashMap);

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(id);
        databaseReference.child("id").setValue(id);

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(id).child(firebaseUser.getUid());
        databaseReference.child("id").setValue(firebaseUser.getUid());
    }

    private void readMessages(String id1, String id2) {
        list = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    assert chat != null;
                    if ((chat.getReceiver().equals(id1) && chat.getSender().equals(id2)) || (chat.getReceiver().equals(id2) && chat.getSender().equals(id1))) {
                        list.add(chat);
                    }

                    adapter = new MessagesAdapter(list);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }
}
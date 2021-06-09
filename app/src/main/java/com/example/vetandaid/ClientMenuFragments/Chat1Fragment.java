package com.example.vetandaid.ClientMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.Constants;
import com.example.vetandaid.MessageActivity;
import com.example.vetandaid.R;
import com.example.vetandaid.RecyclerViews.ChatsAdapter;
import com.example.vetandaid.model.ChatList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chat1Fragment extends Fragment implements ChatsAdapter.RecyclerViewClickListener{

    private ChatsAdapter adapter;
    private List<Map<String, String>> vetList;
    private List<ChatList> chatList;

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;

    private TextView noChat;

    String id;

    Chat1Fragment ceva;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recycler_view, container, false);

        ceva = this;

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noChat = view.findViewById(R.id.noChats);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatList = new ArrayList<>();

        assert firebaseUser != null;
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ChatList list = dataSnapshot.getValue(ChatList.class);
                    chatList.add(list);
                }
                chatList();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        return view;
    }

    private void chatList() {
        vetList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                vetList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    id = dataSnapshot.getKey();

                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    assert map != null;
                    map.put("id", id);

                    for (ChatList list: chatList) {
                        assert id != null;
                        if (id.equals(list.getId())) {
                            vetList.add(map);
                        }
                    }
                }

                if (chatList.size() != 0) {
                    noChat.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noChat.setVisibility(View.VISIBLE);

                    noChat.setText(getString(R.string.chatMessage1));
                }

                adapter = new ChatsAdapter(vetList, ceva, 0);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    @Override
    public void onViewClick(int position) {
        String id = vetList.get(position).get("id");

        SharedPreferences setting = requireActivity().getSharedPreferences(Constants.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("id", id);
        editor.apply();

        setting = requireActivity().getSharedPreferences(Constants.PREFS_ACC_TYPE, Context.MODE_PRIVATE);
        editor = setting.edit();
        editor.putString("accType", "client");
        editor.apply();

        startActivity(new Intent(getActivity(), MessageActivity.class));
    }
}

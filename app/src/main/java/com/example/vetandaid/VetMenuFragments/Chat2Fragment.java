package com.example.vetandaid.VetMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import java.util.Objects;

public class Chat2Fragment extends Fragment implements ChatsAdapter.RecyclerViewClickListener, SearchView.OnQueryTextListener,
        MenuItem.OnActionExpandListener {

    private ChatsAdapter adapter;
    private List<Map<String, String>> clientList;
    private List<ChatList> chatList;

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;

    private TextView noChat;

    String id;

    Chat2Fragment ceva;

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

        setHasOptionsMenu(true);

        return view;
    }

    private void chatList() {
        clientList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                clientList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    id = dataSnapshot.getKey();

                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    assert map != null;
                    map.put("id", id);

                    for (ChatList list: chatList) {
                        assert id != null;
                        if (id.equals(list.getId())) {
                            clientList.add(map);
                        }
                    }
                }

                if (chatList.size() != 0) {
                    noChat.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noChat.setVisibility(View.VISIBLE);

                    noChat.setText(getString(R.string.chatMessage2));
                }

                adapter = new ChatsAdapter(clientList, ceva, 1);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    @Override
    public void onViewClick(int position) {
        String id = clientList.get(position).get("id");

        SharedPreferences setting = requireActivity().getSharedPreferences(Constants.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("id", id);
        editor.apply();

        setting = requireActivity().getSharedPreferences(Constants.PREFS_ACC_TYPE, Context.MODE_PRIVATE);
        editor = setting.edit();
        editor.putString("accType", "vet");
        editor.apply();

        startActivity(new Intent(getActivity(), MessageActivity.class));
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        adapter.setFilter(clientList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            ArrayList<Map<String, String>> filteredList = new ArrayList<>(clientList);
            adapter.setFilter(filteredList);
            return false;
        }
        newText = newText.toLowerCase();
        final ArrayList<Map<String, String>> filteredList = new ArrayList<>();
        for (Map<String, String> item : clientList) {
            if (Objects.requireNonNull(item.get("firstName")).toLowerCase().contains(newText.toLowerCase()) ||
                    Objects.requireNonNull(item.get("lastName")).toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setFilter(filteredList);
        return true;
    }
}

package com.example.vetandaid.RecyclerViews;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.AddPet;
import com.example.vetandaid.R;
import com.example.vetandaid.model.Schedule;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ScheduleRecView extends AppCompatActivity implements ScheduleAdapter.RecyclerViewClickListener {

    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        Intent intent = getIntent();
        String category1 = intent.getStringExtra(AddPet.EXTRA_CATEGORY);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Schedule> options = new FirebaseRecyclerOptions.Builder<Schedule>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child(category1 + "-Breeds"), Schedule.class).build();

        adapter = new ScheduleAdapter(options, this);
        recyclerView.setAdapter(adapter);
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
    public void onViewClick(int position) {
        /*Intent resultIntent = new Intent();
        resultIntent.putExtra("result", adapter.getItem(position).getName());
        setResult(RESULT_OK, resultIntent);
        finish();*/
    }
}

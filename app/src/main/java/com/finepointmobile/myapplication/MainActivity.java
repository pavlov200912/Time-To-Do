package com.finepointmobile.myapplication;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    AppDatabase db;

    FloatingActionButton fab;
    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;

    Task daniel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler_view);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        adapter = new TaskAdapter(db.taskDao().getAll());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,TaskActivity.class);
                        String card_id = String.valueOf(db.taskDao().getAll().get(position).getId());
                        intent.putExtra("id",card_id);
                        startActivity(intent);
                    }
                })
        );


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TaskActivity.class);
                String card_id = String.valueOf(db.taskDao().getAll().size() + 1);
                intent.putExtra("id",card_id);
                startActivity(intent);
            }
        });
        for (Task task : db.taskDao().getAll()) {
           Log.d("ID","id:" + task.taskId + " text:" + task.getShortText());
        }
    }
}

package com.finepointmobile.myapplication;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    HashMap<Integer,Integer> reindex = new HashMap<>();
    private static final String TAG = "MainActivity";

    AppDatabase db;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    SwipeController swipeController = null;
    TaskAdapter adapter;

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
        Log.d("myLog", "onCreate:");
        for (Task task : db.taskDao().getAll()) {
            Log.d("myLog","id:" + task.taskId + " text:" + task.shortText);
        }
        adapter = new TaskAdapter(db.taskDao().getAll());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,TaskActivity.class);
                        String card_id = String.valueOf(db.taskDao().getAll().get(position).getId());
                        intent.putExtra("id",card_id);
                        startActivity(intent);
                    }
                })
        );*/
        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                //TODO remove from db
                adapter.tasks.remove(position);
                db.taskDao().deleteTaskById(position + 1);
                db.checkDao().deleteById(position + 1);
                Log.d("LOL", String.valueOf(db.taskDao().getAll().size()));
                db.taskDao().reindexTasks(position + 1);
                db.checkDao().reindexChecks(position + 1);
                Log.d("myLog","OnDeleted" + position);
                for (Task task : db.taskDao().getAll()) {
                    Log.d("myLog","id:" + task.taskId + " text:" + task.shortText);
                }
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position) {
                Intent intent = new Intent(MainActivity.this,TaskActivity.class);
                String card_id = String.valueOf(position + 1);
                intent.putExtra("id",card_id);
                startActivity(intent);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });


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

package com.finepointmobile.myapplication;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private static final String TAG = "LOGS";

    AppDatabase db;
    SharedPreferences sharedPreferences;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    SwipeController swipeController = null;
    TaskAdapter adapter;

    Task daniel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler_view);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        Log.d(TAG, "onCreate:");
        for (Task task : db.taskDao().getAllSorted()) {
            Log.d(TAG,"id:" + task.taskId + " text:" + task.shortText + " date:" + task.expireDate);
        }
        adapter = new TaskAdapter(db.taskDao().getAllSorted());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this,TaskActivity.class);
                        String card_id = String.valueOf(db.taskDao().getAll().get(position).getTaskId());
                        intent.putExtra("id",card_id);
                        startActivity(intent);
                    }
                })
        );*/
        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                //TODO remove from db
                Log.d(TAG, "onDeleteClicked from ADAPTER id_deleted:" + adapter.tasks.get(position).getTaskId() + " text deleted" + adapter.tasks.get(position).getShortText() );
                db.taskDao().deleteTaskById(adapter.tasks.get(position).getTaskId());
                db.checkDao().deleteById(adapter.tasks.get(position).getTaskId());
                adapter.tasks.remove(position);
                //db.taskDao().reindexTasks(position + 1);
                //db.checkDao().reindexChecks(position + 1);
                Log.d(TAG,"After Deleting:" + position);
                for (Task task : db.taskDao().getAllSorted()) {
                    Log.d(TAG,"id:" + task.taskId + " text:" + task.shortText + " date:" + String.valueOf(task.expireDate));
                }
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position) {
                Intent intent = new Intent(MainActivity.this,TaskActivity.class);
                String card_id = String.valueOf(adapter.tasks.get(position).getTaskId());

                Log.d(TAG, "onEditClicked: " + String.valueOf(card_id));
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
                String card_id = String.valueOf(sharedPreferences.getInt("id",1));
                SavePreferences("id",sharedPreferences.getInt("id",1) + 1);
                Log.d(TAG, "onNewClicked: " + String.valueOf(card_id));
                intent.putExtra("id",card_id);
                startActivity(intent);
            }
        });
    }
    public void SavePreferences(String key, int value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }
}

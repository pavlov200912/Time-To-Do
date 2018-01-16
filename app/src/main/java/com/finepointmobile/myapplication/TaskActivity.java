package com.finepointmobile.myapplication;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

/**
 * Created by danielmalone on 10/27/17.
 */

public class TaskActivity extends AppCompatActivity {

    AppDatabase db;

    EditText textShort;
    EditText textFull;
    EditText checkText;

    FloatingActionButton buttonSave, buttonCheck;

    RecyclerView recyclerView;
    CheckAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        buttonCheck = findViewById(R.id.checkButton);
        textShort = findViewById(R.id.editTextShort);
        textFull = findViewById(R.id.editTextFull);
        checkText = findViewById(R.id.checkText);
        buttonSave = findViewById(R.id.buttonSave);

        Intent intent = getIntent();
        final String cur_id = intent.getStringExtra("id");
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        Log.d("ID", cur_id + ' ');
        final List<Task> cur_task = db.taskDao().getTaskById(cur_id);
        Log.d("ID", "Size = " + String.valueOf(cur_task.size()));
        if (cur_task.size() != 0) {
            textShort.setText(cur_task.get(0).shortText);
            textFull.setText(cur_task.get(0).longText);
            checkText.setText(cur_task.get(0).checkText);
        }
        recyclerView = findViewById(R.id.checkList);
        adapter = new CheckAdapter(db.checkDao().getChecksById(cur_id));
        recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
        recyclerView.setAdapter(adapter);
        /**
         * Button to main_activity
         */
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String short_text = textShort.getText().toString();
                String long_text = textFull.getText().toString();
                String check_box = checkText.getText().toString();
                if(!checkIfEmpty(short_text, long_text)) {
                    if (cur_task.size() == 0) {
                        Task task = new Task(short_text, long_text, check_box, cur_id);
                        db.taskDao().insertAll(task);
                    } else {
                        Task task = db.taskDao().getTaskById(cur_id).get(0);
                        db.taskDao().updateTask(cur_id, short_text, long_text, check_box);
                    }
                    for (Check check : adapter.checks) {
                        Log.d("myLog",check.getCheckText() + ' ' + String.valueOf(check.getIsComplete()));
                        db.checkDao().updateCheck(cur_id, check.getCheckText(), check.getIsComplete());
                    }
                    startActivity(new Intent(TaskActivity.this, MainActivity.class));
                }
            }
        });
        /**
         * Button adding checkText
         */
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Check check : adapter.checks) {
                    Log.d("myLog",check.getCheckText() + ' ' + String.valueOf(check.getIsComplete()));
                    db.checkDao().updateCheck(cur_id, check.getCheckText(), check.getIsComplete());
                }
                Log.d("Button", "CLICKED " + String.valueOf(db.checkDao().getAll().size()));
                String checkName = String.valueOf(checkText.getText());
                db.checkDao().insertAll(new Check(checkName,0,cur_id));
                adapter = new CheckAdapter(db.checkDao().getChecksById(cur_id));
                recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    boolean checkIfEmpty(String short_text, String long_text) {
        return short_text.isEmpty() || long_text.isEmpty();
    }
}

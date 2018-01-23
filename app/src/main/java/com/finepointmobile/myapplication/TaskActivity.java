package com.finepointmobile.myapplication;

import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danielmalone on 10/27/17.
 */

public class TaskActivity extends AppCompatActivity implements OnSelectDateListener {

    AppDatabase db;
    String TAG = "myLog";
    EditText textShort;
    EditText textFull;
    EditText checkText;
    ImageButton buttonCalendar;
    FloatingActionButton buttonCheck;
    ImageButton buttonEdit, buttonSave;
    private int mYear, mMonth, mDay, mHour, mMinute;
    RecyclerView recyclerView;
    CheckAdapter adapter;
    TextView textCalendar;

    String expireTime;
    long expireTimeInMillisecond;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task);

        buttonCheck = findViewById(R.id.checkButton);
        textShort = findViewById(R.id.editTextShort);
        textFull = findViewById(R.id.editTextFull);
        checkText = findViewById(R.id.checkText);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCalendar = findViewById(R.id.buttonCalendar);
        buttonEdit = findViewById(R.id.buttonEdit);
        textCalendar = findViewById(R.id.textCalendar);
        Intent intent = getIntent();
        final boolean edit = intent.getBooleanExtra("edit",false);
        final int cur_id = Integer.parseInt(intent.getStringExtra("id"));
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        Log.d("myLog", "onSecondCreate: " + cur_id);
        for (Task task : db.taskDao().getAll()) {
            Log.d("myLog","id:" + task.taskId + " text:" + task.shortText);
        }
        Log.d("ID", String.valueOf(cur_id) + ' ');
        final List<Task> cur_task = db.taskDao().getTaskById(cur_id);
        Log.d("ID", "Size = " + String.valueOf(cur_task.size()));
        if(!edit){
            textShort.setEnabled(false);
            textFull.setEnabled(false);
            checkText.setEnabled(false);
            buttonCheck.setEnabled(false);
            buttonCalendar.setEnabled(false);
        }
        if (cur_task.size() != 0) {
            textShort.setText(cur_task.get(0).shortText);
            textFull.setText(cur_task.get(0).longText);
            checkText.setText(cur_task.get(0).checkText);
            textCalendar.setText(cur_task.get(0).dateText);
            expireTimeInMillisecond = cur_task.get(0).expireDate;
            expireTime = cur_task.get(0).dateText;
        }
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new CheckAdapter(db.checkDao().getChecksById(cur_id));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
        recyclerView.setAdapter(adapter);
        /**
         * Button to main_activity
         */
        SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                db.checkDao().deleteCheck(adapter.checks.get(viewHolder.getAdapterPosition()));
                adapter.removeItem(viewHolder.getAdapterPosition());

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String short_text = textShort.getText().toString();
                String long_text = textFull.getText().toString();
                String check_box = checkText.getText().toString();
                if(!checkIfEmpty(short_text, long_text)) {
                    if (cur_task.size() == 0) {
                        Task task = new Task(short_text, long_text, check_box, expireTime, cur_id, expireTimeInMillisecond);
                        db.taskDao().insertAll(task);
                    } else {
                        Task task = db.taskDao().getTaskById(cur_id).get(0);
                        db.taskDao().updateTask(cur_id, short_text, long_text, check_box , expireTime, expireTimeInMillisecond);
                    }
                    for (Check check : adapter.checks) {
                        Log.d("myLog",check.getCheckText() + ' ' + String.valueOf(check.getIsComplete()));
                        db.checkDao().updateCheck(cur_id, check.getCheckText(), check.getIsComplete());
                    }
                    startActivity(new Intent(TaskActivity.this, MainActivity.class));
                }
            }
        });
        buttonEdit.setImageResource(R.drawable.ic_edit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textShort.setEnabled(true);
                textFull.setEnabled(true);
                checkText.setEnabled(true);
                buttonCheck.setEnabled(true);
                buttonCalendar.setEnabled(true);
                textShort.setTextColor(Color.BLACK);
                textFull.setTextColor(Color.BLACK);
                checkText.setTextColor(Color.BLACK);
            }
        });
        /**
         * Button adding checkText
         */
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkName = String.valueOf(checkText.getText());
                db.checkDao().insertAll(new Check(checkName,0,cur_id));
                adapter = new CheckAdapter(db.checkDao().getChecksById(cur_id));
                recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
                recyclerView.setAdapter(adapter);
               // checkText.setText("");
            }
        });
        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, 5);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 5);
        buttonCalendar.setOnClickListener(v -> {
            DatePickerBuilder oneDayBuilder = new DatePickerBuilder(this,this)
                    .pickerType(CalendarView.ONE_DAY_PICKER)
                    .date(max)
                    .headerColor(R.color.colorPrimaryDark)
                    .headerLabelColor(R.color.currentMonthDayColor)
                    .selectionColor(R.color.daysLabelColor)
                    .todayLabelColor(R.color.colorAccent)
                    .dialogButtonsColor(R.color.colorPrimaryDark)
                    .disabledDaysLabelsColor(android.R.color.holo_purple)
                    .disabledDays(getDisabledDays());

            DatePicker oneDayPicker = oneDayBuilder.build();
            oneDayPicker.show();
        });
    }

    boolean checkIfEmpty(String short_text, String long_text) {
        return false;
        //return short_text.isEmpty() || long_text.isEmpty();
    }
    private List<Calendar> getDisabledDays() {
        /*Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(Calendar.DAY_OF_MONTH, 2);

        Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(Calendar.DAY_OF_MONTH, 1);

        Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(Calendar.DAY_OF_MONTH, 18);*/

        List<Calendar> calendars = new ArrayList<>();
        /*calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);*/
        return calendars;
    }

    @Override
    public void onSelect(List<Calendar> calendars) {
        final String[] t = new String[1];
        Stream.of(calendars).forEach(calendar ->
                t[0] = calendar.getTime().toString());
                /*Toast.makeText(getApplicationContext(),
                        calendar.getTime().toString(),
                        Toast.LENGTH_SHORT).show();*/

        //buttonCalendar.setText(calendars.get(0).getTime().toString());
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        expireTimeInMillisecond = calendars.get(0).getTimeInMillis() + hourOfDay * 3600000 + minute * 60000;
                        String date = calendars.get(0).getTime().toString();
                        expireTime = date.substring(4, 11) + ' ' + parseDate(hourOfDay)
                                + ':' + parseDate(minute);
                       textCalendar.setText(expireTime);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }
    public String parseDate(int date){
        if(date<10){
            return '0' + String.valueOf(date);
        }
        return String.valueOf(date);
    }
}

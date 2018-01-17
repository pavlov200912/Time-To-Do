package com.finepointmobile.myapplication;

import android.app.TimePickerDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.applandeo.materialcalendarview.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by danielmalone on 10/27/17.
 */

public class TaskActivity extends AppCompatActivity implements OnSelectDateListener {

    AppDatabase db;

    EditText textShort;
    EditText textFull;
    EditText checkText;
    Button buttonCalendar;
    FloatingActionButton buttonSave, buttonCheck;
    private int mYear, mMonth, mDay, mHour, mMinute;
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
        buttonCalendar = findViewById(R.id.buttonCalendar);

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
            buttonCalendar.setText(cur_task.get(0).dateText);
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
                        Task task = new Task(short_text, long_text, check_box, String.valueOf(buttonCalendar.getText()), cur_id);
                        db.taskDao().insertAll(task);
                    } else {
                        Task task = db.taskDao().getTaskById(cur_id).get(0);
                        db.taskDao().updateTask(cur_id, short_text, long_text, check_box , String.valueOf(buttonCalendar.getText()));
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
        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, 0);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 12);
        buttonCalendar.setOnClickListener(v -> {
            DatePickerBuilder oneDayBuilder = new DatePickerBuilder(this,this)
                    .pickerType(CalendarView.ONE_DAY_PICKER)
                    .date(max)
                    .headerColor(R.color.colorPrimaryDark)
                    .headerLabelColor(R.color.currentMonthDayColor)
                    .selectionColor(R.color.daysLabelColor)
                    .todayLabelColor(R.color.colorAccent)
                    .dialogButtonsColor(android.R.color.holo_green_dark)
                    .disabledDaysLabelsColor(android.R.color.holo_purple)
                    .minimumDate(min)
                    .maximumDate(max)
                    .disabledDays(getDisabledDays());

            DatePicker oneDayPicker = oneDayBuilder.build();
            oneDayPicker.show();
        });
    }

    boolean checkIfEmpty(String short_text, String long_text) {
        return short_text.isEmpty() || long_text.isEmpty();
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
        Stream.of(calendars).forEach(calendar ->
                Toast.makeText(getApplicationContext(),
                        calendar.getTime().toString(),
                        Toast.LENGTH_SHORT).show());
        buttonCalendar.setText(calendars.get(0).getTime().toString());
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        buttonCalendar.setText( String.valueOf(buttonCalendar.getText()) + ' ' + mHour + ':' + mMinute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
    public String parseDate(String date){
        String month, day;
        for(int i = 0;i<date.length();i++){
            Log.d("myLog",i + ":"  + date.charAt(i));
        }
        month = date.substring(3,7);
        day = date.substring(7,9);
        return day + ' ' + month + ' ';
    }
}

package com.finepointmobile.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by пк on 09.01.2018.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static String oldApp = "com.android.settings";
    private static String newApp = "com.android.calendar";
    private long time = 0;
    private final String TAG = "myLog";
    SharedPreferences sharedPreferences;
    AppDatabase db;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        checkDate();
        final String sourcePackageName = (String) accessibilityEvent.getPackageName();
        newApp = sourcePackageName;
        if(!oldApp.equals(newApp)){
            Log.d(TAG, "was:" + oldApp + " now:" + newApp);
            if(!db.circlesDao().getCirclesByName(oldApp).isEmpty()){
                db.circlesDao().updateTime(oldApp,System.currentTimeMillis() - time);
            }
            oldApp = newApp;
            time = System.currentTimeMillis();

        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        db = Room.databaseBuilder(this, AppDatabase.class, "production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkDate();
        super.onServiceConnected();
        time = System.currentTimeMillis();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        this.setServiceInfo(info);
    }
    public void SavePreferences(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void reStart(){
        db.circlesDao().reWriteAll();
    }
    public void checkDate(){
        DateFormat df = new SimpleDateFormat("EEE, MMM d, ''yy");
        String date = df.format(Calendar.getInstance().getTime());
        if(sharedPreferences.getString("ServiceStart","NONE").equals("NONE")){
            SavePreferences("ServiceStart", date);
        }
        else {
            if(!date.equals(sharedPreferences.getString("ServiceStart","NONE"))){
                reStart();
                SavePreferences("ServiceStart", date);
            }
        }
    }
}

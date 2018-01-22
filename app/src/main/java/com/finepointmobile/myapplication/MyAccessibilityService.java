package com.finepointmobile.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
    private final String TAG = "AmyLog";
    SharedPreferences sharedPreferences;
    AppDatabase db;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        final int eventType = accessibilityEvent.getEventType();
        checkDate();

        final String sourcePackageName = (String) accessibilityEvent.getPackageName();
        newApp = sourcePackageName;
        Log.d(TAG, "old:" + oldApp + " new:" + newApp);
        Log.d(TAG,"Size is:" +  String.valueOf(db.circlesDao().getCirclesByName(newApp).size()));
        if(db.circlesDao().getCirclesByName(newApp).size() > 0) {
            if (System.currentTimeMillis() - time > db.circlesDao().getCirclesByName(newApp).get(0).getLimitTime()
                    && sharedPreferences.getString(oldApp + "good", "yes").equals("yes")) {
                //TODO Push
                SavePreferences(oldApp + "good", "no");
                SavePreferences("good", "no");
                if (db.taskDao().getAllSorted().size() > 0) {
                    push(db.taskDao().getAllSorted().get(0).shortText);
                } else {
                    push("");
                }
            }
        }
        if(!oldApp.equals(newApp)){
            Log.d(TAG, "time in " + oldApp  + " :" + String.valueOf(System.currentTimeMillis() - time));
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
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED | AccessibilityEvent.TYPE_VIEW_SCROLLED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        this.setServiceInfo(info);
    }
    public void SavePreferences(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public void SavePreferencesInt(String key, int value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
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
                if(sharedPreferences.getString("good","yes").equals("yes")){
                    SavePreferencesInt("TP",sharedPreferences.getInt("TP",0) + 50);
                }
                SavePreferences("ServiceStart", date);
                SavePreferences("good","yes");
            }
        }
    }
    public void push(String input){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_ab_app)
                        .setContentTitle("TimeToDo")
                        .setContentText("Вы превысили установленный режим в данном приложении,советуем вам:" + input);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
// Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
        inboxStyle.addLine("Вы превысили установленный режим в данном приложении");
        inboxStyle.addLine("советуем вам:" + input);
// Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
}

package com.finepointmobile.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by пк on 09.01.2018.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static String oldApp = "";
    private static String newApp = "com.android.calendar";
    private long time = 0;
    private final String TAG = "myLog";
    private SharedPreferences sharedPreferences;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent: ");
        final String sourcePackageName = (String) accessibilityEvent.getPackageName();
        newApp = sourcePackageName;
        if(oldApp != newApp){

            //TODO Write Data in FILE? (With new Run Map == null)
            SavePreferences(oldApp,String.valueOf(
                    Long.valueOf(sharedPreferences.getString(newApp,"0"))
                            +  System.currentTimeMillis() - time));
            oldApp = newApp;
            time = System.currentTimeMillis();

        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected: ");
        SavePreferences("ServiceWork","true");
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
        SavePreferences("ServiceWork","false");
    }
}

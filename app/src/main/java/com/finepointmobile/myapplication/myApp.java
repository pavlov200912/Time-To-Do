package com.finepointmobile.myapplication;

import android.app.Application;

import com.vk.sdk.VKSdk;

/**
 * Created by Роман on 21.01.2018.
 */

public class myApp extends Application {
    public myApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(getApplicationContext());
    }
}

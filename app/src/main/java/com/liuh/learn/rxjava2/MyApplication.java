package com.liuh.learn.rxjava2;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}

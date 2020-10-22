package com.example.miz.mizgram;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by miz on 17/2/2019.
 */

public class IntryPoint extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

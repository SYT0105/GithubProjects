package com.services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    static final String TAG = SecondActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"OnCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"OnStop");
    }
}

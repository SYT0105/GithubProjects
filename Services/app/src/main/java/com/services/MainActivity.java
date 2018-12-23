package com.services;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.providers.R;

public class MainActivity extends AppCompatActivity implements ServiceConnection, OnCountDownListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView timer;

    boolean isServiceBinded = false;
    private MyService serviceInstance;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = findViewById(R.id.timer_text);

//        showAlertDialog();

//        showDialogFragment();


        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceBinded) {
                    serviceInstance.startTimer();
                }
            }
        });

        String url = "https://api.github.com/users/hadley/orgs";

        intent = new Intent(this, MyService.class);
        intent.putExtra("url", url);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        startService(intent);
        startService(intent);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);

    }

    private void showDialogFragment() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        myDialogFragment.show(fragmentTransaction, "dialog");

    }

    private void showAlertDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Samples:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Sample 1");
        arrayAdapter.add("Sample 2");
        arrayAdapter.add("Sample 3");
        arrayAdapter.add("Sample 4");
        arrayAdapter.add("Sample 5");
        arrayAdapter.add("Sample 2");
        arrayAdapter.add("Sample 3");
        arrayAdapter.add("Sample 4");
        arrayAdapter.add("Sample 5");
        arrayAdapter.add("Sample 2");
        arrayAdapter.add("Sample 3");
        arrayAdapter.add("Sample 4");
        arrayAdapter.add("Sample 5");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    @Override
    public void onServiceConnected(ComponentName name, final IBinder service) {

        isServiceBinded = true;

        MyService.MyBinder myBinder = (MyService.MyBinder) service;
        serviceInstance = myBinder.getServiceInstance();
        serviceInstance.setCallbackListener(MainActivity.this);
        Log.d(TAG, serviceInstance.toString());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isServiceBinded = false;
    }

    @Override
    public void onTick(final long millis) {
        Log.d(TAG, "onTick");
        timer.setText("seconds remaining: " + millis / 1000);
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "Timer finished");
        timer.setText("seconds remaining finished");
        stopService(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startActivity(new Intent(this,SecondActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
    }
}

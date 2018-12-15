package com.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.providers.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();
    private OnCountDownListener onCountDownListener;
    private MyCount countDownTimer;

    public void startTimer() {
        if (countDownTimer != null) {
            Log.d(TAG, "count down is null and it is initialised ");
            countDownTimer.start();
        }
    }

    private class MyCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        private MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (onCountDownListener != null) {
                onCountDownListener.onTick(millisUntilFinished);
            }
        }

        @Override
        public void onFinish() {
            if (onCountDownListener != null) {
                onCountDownListener.onFinish();
            }
        }
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "oncreate");
        countDownTimer = new MyCount(10000, 1000);
    }

    /**
     * // Only called when service is binded
     *
     * @param intent
     * @return Binder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OnBind");
        return new MyBinder();
    }

    public void setCallbackListener(OnCountDownListener onCountDownListener) {
        this.onCountDownListener = onCountDownListener;
    }

    class MyBinder extends Binder {
        MyService getServiceInstance() {
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {

        Log.d(TAG, "onStartCommand");

        /*Toast.makeText(getBaseContext(), "Called " + startId, Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getOrgs(intent, startId);
            }
        }).start();*/
        showNotification();
        return START_REDELIVER_INTENT;
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(getApplicationContext(), getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getPackageName(),
                    getPackageName()/* some channel name*/,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationCompat.setChannelId(getPackageName());
        }
        notificationCompat.setAutoCancel(true);
        notificationCompat.setSmallIcon(R.mipmap.ic_launcher);
        notificationCompat.setContentTitle(getString(R.string.app_name));
        notificationCompat.setContentText("Sample Forground Service");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);

//            notificationCompat.addAction(R.mipmap.ic_launcher, "YES", pendingIntent); //add a btn to the Notification with a corresponding intent

        notificationManager.notify(MyService.this.hashCode(), notificationCompat.build());

    }

    private void getOrgs(Intent intent, int startId) {
        try {
            if (intent != null) {
                String extra = intent.getStringExtra("url");
                URL url = new URL(extra);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                if (httpsURLConnection.getResponseCode() >= HttpsURLConnection.HTTP_OK
                        && httpsURLConnection.getResponseCode() < HttpsURLConnection.HTTP_MULT_CHOICE) {
                    String result = getStringFromStream(httpsURLConnection.getInputStream());
                    Log.d(TAG, result);
                    Log.d(TAG, startId + "");
                    Thread.sleep(2000);
                    stopSelf(startId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStringFromStream(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        inputStream.close();
        bufferedReader.close();
        return stringBuilder.toString();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "OnDESTROY");
    }
}
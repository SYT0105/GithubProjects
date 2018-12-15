package com.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MyIntentService extends IntentService {

    private static final String TAG = MyIntentService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MyIntentService() {
        super(MyIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        getOrgs(intent, "1");
        getOrgs(intent, "2");
        getOrgs(intent, "3");
    }

    private void getOrgs(Intent intent, String option) {
        try {
            if (intent != null) {
                String extra = intent.getStringExtra("url");
                URL url = new URL(extra);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                if (httpsURLConnection.getResponseCode() >= HttpsURLConnection.HTTP_OK
                        && httpsURLConnection.getResponseCode() < HttpsURLConnection.HTTP_MULT_CHOICE) {
                    String result = getStringFromStream(httpsURLConnection.getInputStream());
                    Log.d(TAG + option, result);
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

}
package com.example.mockup;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIService extends Service {
    public APIService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AsyncTaskExample asyncTask = new AsyncTaskExample();
        asyncTask.execute("https://ac-storage-space.xyz/smd.xml");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class AsyncTaskExample extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Service starting", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String line = "", xmlData = "";
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                StringBuilder content = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream()));
                while((line = reader.readLine()) != null){
                    content.append(line);
                }
                xmlData = content.toString();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            return xmlData;
        }

        @Override
        protected void onPostExecute(String xmlData) {
            super.onPostExecute(xmlData);
            Log.d("sender", "Broadcasting message");
            Intent intent = new Intent("notify_activity");
            intent.putExtra("XML Data",xmlData);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            Toast.makeText(getApplicationContext(), "Service has finished. Please refresh.", Toast.LENGTH_SHORT).show();

        }
    }

}
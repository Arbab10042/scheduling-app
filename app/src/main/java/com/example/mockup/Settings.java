package com.example.mockup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Advanceable;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Settings extends AppCompatActivity {

    boolean service_running = false;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        adView=findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }

    public void importBtnHandler(View v) {
        if (!service_running) {
            startService(new Intent(getBaseContext(), APIService.class));
            service_running = true;
        } else {
            Toast.makeText(getApplicationContext(), "You have already started the service.", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearBtnHandler(View v) {
        NodeDBDAO dao = new NodeDBDAO(getApplicationContext());
        dao.clearDatabase();
        Toast.makeText(getApplicationContext(), "Database has been cleared.", Toast.LENGTH_SHORT).show();
    }

    public void backupBtnHandler(View v) {
        //TO TEST

        try {
            File dbFile = new File(this.getDatabasePath(DBHelper.DATABASE_NAME).getAbsolutePath());
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(dbFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            @SuppressLint("SdCardPath") String outFileName = "/data/data/com.example.mockup/databases/backup.db";

            // Open the empty db as the output stream
            OutputStream output = null;
            try {
                output = new FileOutputStream(outFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length = 0;
            while (true) {
                try {
                    if (!((length = fis.read(buffer)) > 0)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();


        } catch (IOException e) {
            Log.e("dbBackup:", e.getMessage());
        }

        //TO TEST ENDS
        Toast.makeText(getApplicationContext(), "Backup Created", Toast.LENGTH_SHORT).show();
    }

    public void restorebackupBtnHandler(View v) {
        //check if file exists

        @SuppressLint("SdCardPath") String file = "/data/data/com.example.mockup/databases/backup.db";
        File dbFile = new File(file);
        if (dbFile.exists()) {
            //if it exists copy and paste as database.db in same folder
            File oldDB = new File(this.getDatabasePath(DBHelper.DATABASE_NAME).getAbsolutePath());
            if (oldDB.exists()) {
                if (oldDB.delete()) {
                    Log.d("Restore backup", "Deleted database.db");
                } else {
                    Log.d("Restore backup", "Could not delete database.db");
                }
            }
            try {
//                File dbFile = new File(this.getDatabasePath(DBHelper.DATABASE_NAME).getAbsolutePath());
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(dbFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                @SuppressLint("SdCardPath") String outFileName = "/data/data/com.example.mockup/databases/database.db";

                // Open the empty db as the output stream
                OutputStream output = null;
                try {
                    output = new FileOutputStream(outFileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length = 0;
                while (true) {
                    try {
                        if (!((length = fis.read(buffer)) > 0)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    output.write(buffer, 0, length);
                }
                // Close the streams
                output.flush();
                output.close();
                fis.close();


            } catch (IOException e) {
                Log.e("dbBackupRestore:", e.getMessage());
            }

            //TO TEST ENDS
            Toast.makeText(getApplicationContext(), "Backup Restored", Toast.LENGTH_SHORT).show();

        } else {
            Log.d("restore backup", "backup.db not found");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
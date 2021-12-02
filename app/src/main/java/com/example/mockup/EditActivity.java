package com.example.mockup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.Objects;

public class EditActivity extends AppCompatActivity {

    private Node node = null;
    private String action = "none";  //save or delete or none
    private Integer old_bookmark = 0;
    private String old_day = "none";
    private NodeDBDAO dao;


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.edit_title);
        TextInputLayout currEp = (TextInputLayout) findViewById(R.id.edit_currEpisodes);
        TextInputLayout totalEp = (TextInputLayout) findViewById(R.id.edit_totalEpisodes);
        CheckBox checkBox = (CheckBox) findViewById(R.id.bookmark_btn);

        outState.putSerializable("node", node);
        outState.putString("title", textInputLayout.getEditText().getText().toString().trim());
        outState.putString("current", currEp.getEditText().getText().toString().trim());
        outState.putString("total", totalEp.getEditText().getText().toString().trim());
        outState.putBoolean("bookmark", checkBox.isChecked());
        outState.putBoolean("old_bookmark", old_bookmark == 1);
        outState.putString("old_day", old_day);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Edit Node");

        dao = new NodeDBDAO(this);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.bookmark_btn);
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.edit_title);
        TextInputLayout currEpisode = (TextInputLayout) findViewById(R.id.edit_currEpisodes);
        TextInputLayout totalEpisode = (TextInputLayout) findViewById(R.id.edit_totalEpisodes);

        if (savedInstanceState != null) {
            node = (Node) savedInstanceState.getSerializable("node");
            textInputLayout.getEditText().setText((String)savedInstanceState.get("title"));
            currEpisode.getEditText().setText((String)savedInstanceState.get("current"));
            totalEpisode.getEditText().setText((String)savedInstanceState.get("total"));
            checkBox.setChecked((Boolean)savedInstanceState.get("bookmark"));
            if((Boolean)savedInstanceState.get("old_bookmark") == true){
                old_bookmark = 1;
            }else{
                old_bookmark = 0;
            }
            old_day = savedInstanceState.getString("old_day");
        } else {
            node = (Node) getIntent().getSerializableExtra("node");
            old_bookmark = node.getBookmarked();
            old_day = node.getTimeDay();
            textInputLayout.getEditText().setText(node.getTitle());
            currEpisode.getEditText().setText(Integer.toString(node.getCurrent_episode()));
            totalEpisode.getEditText().setText(Integer.toString(node.getTotal_episodes()));
        }

        setTimePicker(node.getTime());        //Set Time Picker and add listener
        set_day_spinner(node.getTime());      //Instantiate day spinner and add listener

        String status;
        if (node.getStatus() == 1) {
            status = "Watching";
        } else if (node.getStatus() == 2) {
            status = "On-Hold";
        } else {
            status = "Plan To Watch";
        }

        set_status_spinner(status);   //Instantiate status spinner and add listener
        checkBox.setChecked(node.getBookmarked() == 1);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                node.setBookmarked(1);      //Add Bookmarked show
            } else {
                node.setBookmarked(0);      //Remove Bookmarked show
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTimePicker(String dateTime) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        String[] dateTimeData = dateTime.split("-", 2);       //Split Day and Time
        String time = dateTimeData[1];
        String[] times = time.split(":", 2);
        int hour = Integer.parseInt(times[0]);
        int min = Integer.parseInt(times[1]);
        timePicker.setHour(hour);
        timePicker.setMinute(min);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String hourStr;
                String minuteStr;
                if (hourOfDay < 10)
                    hourStr = "0" + hourOfDay;
                else
                    hourStr = Integer.toString(hourOfDay);
                if (minute < 10)
                    minuteStr = "0" + minute;
                else
                    minuteStr = Integer.toString(minute);
                String newTime = hourStr + ":" + minuteStr;
                node.setTimeHoursMinutes(newTime);
            }
        });
    }

    private void set_day_spinner(String dateTime) {
        String[] dateTimeData = dateTime.split("-", 2);
        String currDay = dateTimeData[0];
        String[] dayArray = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_list, dayArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        final Spinner days = (Spinner) findViewById(R.id.day_spinner);
        days.setAdapter(arrayAdapter);
        days.setSelection(arrayAdapter.getPosition(currDay));
        days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String day;
                day = parent.getItemAtPosition(position).toString();
                node.setTimeDay(day);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void set_status_spinner(String currStatus) {
        String[] statusArray = new String[]{"Watching", "On-Hold", "Plan To Watch"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_list, statusArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        final Spinner statuses = (Spinner) findViewById(R.id.status_spinner);
        statuses.setAdapter(arrayAdapter);
        statuses.setSelection(arrayAdapter.getPosition(currStatus));

        statuses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String status;
                status = parent.getItemAtPosition(position).toString();
                int stat = node.getStatus();
                if (status.equals("Watching")) {
                    stat = 1;
                } else if (status.equals("On-Hold")) {
                    stat = 2;
                } else {
                    stat = 3;
                }
                node.setStatus(stat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void deleteNodeHandler(View v) {
        action = "delete";
        onBackPressed();
    }

    public void saveNodeHandler(View v) {
        action = "save";
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.edit_title);
        String text = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim();
        if (text.length() > 0)
            node.setTitle(text);

        TextInputLayout totalEpisodes = (TextInputLayout) findViewById(R.id.edit_totalEpisodes);
        String totalEpisodesText = Objects.requireNonNull(totalEpisodes.getEditText()).getText().toString().trim();
        if (totalEpisodesText.length() > 0) {
            int totalEpNum = Integer.parseInt(totalEpisodesText);
            if (totalEpNum >= 0)
                node.setTotal_episodes(totalEpNum);
        }

        TextInputLayout currEpisodes = (TextInputLayout) findViewById(R.id.edit_currEpisodes);
        String currEpisodeText = Objects.requireNonNull(currEpisodes.getEditText()).getText().toString().trim();
        if (currEpisodeText.length() > 0) {
            int currEpNum = Integer.parseInt(currEpisodeText);
            if (currEpNum >= 0 && currEpNum <= node.getTotal_episodes())
                node.setCurrent_episode(currEpNum);
        }
        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        switch(action){
            case "save":
                dao.saveNode(node);
                break;
            case "delete":
                dao.deleteNode(node);
                break;
            default:
                break;
        }

        Intent intent = new Intent();
        intent.putExtra("node", node);
        Log.d("Node Title in EditAct ", node.getTitle());
        intent.putExtra("action", action);
        intent.putExtra("old_bookmark", old_bookmark.toString());
        intent.putExtra("old_day", old_day);
        setResult(RESULT_OK, intent);
        finish();
    }

}
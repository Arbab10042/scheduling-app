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

import java.util.Calendar;
import java.util.Objects;

public class InsertActivity extends AppCompatActivity {

    private Node node = null;
    private String action = "none";    //insert or none
    private NodeDBDAO dao;
    int node_id, bookmark = 0, status = 1, currentEpisode, totalEpisodes;
    String title, time, day;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.insert_title);
        TextInputLayout currEp = (TextInputLayout) findViewById(R.id.insert_currEpisodes);
        TextInputLayout totalEp = (TextInputLayout) findViewById(R.id.insert_totalEpisodes);
        CheckBox checkBox = (CheckBox) findViewById(R.id.insert_bookmark_btn);

        super.onSaveInstanceState(outState);
        outState.putSerializable("node", node);
        outState.putString("title", Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim());
        outState.putString("current", Objects.requireNonNull(currEp.getEditText()).getText().toString().trim());
        outState.putString("total", Objects.requireNonNull(totalEp.getEditText()).getText().toString().trim());
        outState.putBoolean("bookmark", checkBox.isChecked());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Insert Node");
        dao = new NodeDBDAO(this);

        setTimePicker();
        set_day_spinner();
        set_status_spinner();
        final CheckBox checkBox = (CheckBox) findViewById(R.id.insert_bookmark_btn);
        if (savedInstanceState != null) {
            TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.insert_title);
            TextInputLayout currEp = (TextInputLayout) findViewById(R.id.insert_currEpisodes);
            TextInputLayout totalEp = (TextInputLayout) findViewById(R.id.insert_totalEpisodes);

            node = (Node) savedInstanceState.getSerializable("node");
            Objects.requireNonNull(textInputLayout.getEditText()).setText((String)savedInstanceState.get("title"));
            Objects.requireNonNull(currEp.getEditText()).setText((String)savedInstanceState.get("current"));
            Objects.requireNonNull(totalEp.getEditText()).setText((String)savedInstanceState.get("total"));
            checkBox.setChecked((Boolean)savedInstanceState.get("bookmark"));
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bookmark = 1;      //Add Bookmarked show
                } else {
                    bookmark = 0;      //Remove Bookmarked show
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTimePicker() {
        TimePicker timePicker = (TimePicker) findViewById(R.id.insert_time_picker);
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
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
                time = hourStr + ":" + minuteStr;

            }
        });
    }

    private void set_day_spinner() {
        String[] dayArray = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_list, dayArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        final Spinner days = (Spinner) findViewById(R.id.insert_day_spinner);
        days.setAdapter(arrayAdapter);
        days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                day = "Monday";
            }
        });
    }

    public void set_status_spinner() {
        String[] statusArray = new String[]{"Watching", "On-Hold", "Plan To Watch"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_list, statusArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        final Spinner statuses = (Spinner) findViewById(R.id.insert_status_spinner);
        statuses.setAdapter(arrayAdapter);
        statuses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String stat;
                stat = parent.getItemAtPosition(position).toString();
                if (stat.equals("Watching")) {
                    status = 1;
                } else if (stat.equals("On-Hold")) {
                    status = 2;
                } else {
                    status = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                status = 1;
            }
        });
    }

    private Boolean confirmTextFields() {
        TextInputLayout titleField = (TextInputLayout) findViewById(R.id.insert_title);
        TextInputLayout currEpField = (TextInputLayout) findViewById(R.id.insert_currEpisodes);
        TextInputLayout totalEpField = (TextInputLayout) findViewById(R.id.insert_totalEpisodes);
        String titleText = Objects.requireNonNull(titleField.getEditText()).getText().toString().trim();
        String curr = Objects.requireNonNull(currEpField.getEditText()).getText().toString().trim();
        String total = Objects.requireNonNull(totalEpField.getEditText()).getText().toString().trim();

        boolean validation = true;
        if (titleText.isEmpty()) {
            titleField.setError("Field cannot be empty");
            validation = false;
        } else {
            titleField.setError(null);
            title = titleText;
        }
        if (total.isEmpty()) {
            totalEpField.setError("Field cannot be empty");
            validation = false;
        } else {
            int num = Integer.parseInt(total);
            if (num >= 0) {
                totalEpisodes = num;
                totalEpField.setError(null);
            } else {
                totalEpField.setError("Invalid Total Episodes");
                validation = false;
            }
        }
        if (curr.isEmpty()) {
            currEpField.setError("Field cannot be empty");
            validation = false;
        } else {
            int numCurrEp = Integer.parseInt(curr);
            if (numCurrEp >= 0 && numCurrEp <= totalEpisodes) {
                currentEpisode = numCurrEp;
                currEpField.setError(null);
            } else {
                currEpField.setError("Invalid Current Episodes");
                validation = false;
            }
        }
        return validation;
    }

    public void insertNodeBtn(View view) {
        if (confirmTextFields()) {
            action = "insert";
            node_id = dao.getMaxID() + 1;
            node = new Node(node_id, title, day + "-" + time, currentEpisode, totalEpisodes, status, bookmark);
            onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (action.equals("insert")) {
            dao.insertNode(node);
        }

        Intent intent = new Intent();
        intent.putExtra("node", node);
        if(node!=null) {
            Log.d("Node Title in InsertAct", node.getTitle());
            Log.d("Node_id in InsertAct", Integer.toString(node.getNode_id()));
        }
        intent.putExtra("action", action);
        setResult(RESULT_OK, intent);
        finish();
    }
}
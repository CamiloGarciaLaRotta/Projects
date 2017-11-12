package ca.mcgill.ecse321.group10.eventregistration;

import android.annotation.SuppressLint;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import group10.EventRegistration.Controller.EventRegistrationController;
import group10.EventRegistration.Controller.InvalidInputException;
import group10.EventRegistration.Model.Event;
import group10.EventRegistration.Model.Participant;
import group10.EventRegistration.Model.RegistrationManager;
import group10.EventRegistration.Persistence.PersistenceXStream;

public class MainActivity extends AppCompatActivity {

    private RegistrationManager rm = null;
    EventRegistrationController pc = null;
    private String fileName;
    String error = null;

    TextView errorView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        errorView = (TextView) findViewById(R.id.errorMessage);

        // Initialize file name and XStream
        fileName = getFilesDir().getAbsolutePath() + "/eventRegistration.xml";
        rm = PersistenceXStream.initializeModelManager(fileName);

        pc = new EventRegistrationController(rm);

        refreshData();
    }

    public void addParticipant(View v) {
        // retrieve relevant information: participant name
        TextView tv = (TextView) findViewById(R.id.newparticipant_name);

        try {
            pc.createParticipant(tv.getText().toString());
        } catch (InvalidInputException e) {
            errorView.setText(e.getMessage());
        }
        refreshData();
    }

    public void addEvent (View v) {
        // retrieve relevant information: event name, date, start time, end time
        TextView tv = (TextView) findViewById(R.id.newevent_name);
        TextView dateView = (TextView) findViewById(R.id.newevent_date);
        TextView startView = (TextView) findViewById(R.id.newevent_start);
        TextView endView = (TextView) findViewById(R.id.newevent_end);

        // Formatters for the Date and Time
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        DateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

        try{
            Date date = new Date(dateFormat.parse(dateView.getText().toString()).getTime());
            Time startTime = new Time(timeFormat.parse(startView.getText().toString()).getTime());
            Time endTime = new Time(timeFormat.parse(endView.getText().toString()).getTime());

            pc.createEvent(tv.getText().toString(), date, startTime, endTime);

        } catch (Exception e) {
            errorView.setText(e.getMessage());
        }

        refreshData();
    }

    public void registerParticipant(View v) {
        Spinner participantSpinner = (Spinner) findViewById(R.id.participantspinner);
        Spinner eventSpinner = (Spinner)  findViewById(R.id.eventspinner);

        // retrieve correspondent participant and event
        int participantIndex = participantSpinner.getSelectedItemPosition();
        Participant p = rm.getParticipant(participantIndex);

        int eventIndex = eventSpinner.getSelectedItemPosition();
        Event e = rm.getEvent(eventIndex);

        try {
            pc.register(p, e);
        } catch (InvalidInputException error) {
            errorView.setText(error.toString());
        }

        refreshData();
    }

    private void refreshData() {
        TextView participantName = (TextView) findViewById(R.id.newparticipant_name);
        TextView eventName = (TextView) findViewById(R.id.newevent_name);
        participantName.setText("");
        eventName.setText("");
        errorView.setText("");

        // Initialize the data in the participant spinner
        Spinner participantSpinner = (Spinner) findViewById(R.id.participantspinner);
        ArrayAdapter<CharSequence> participantAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        participantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Participant p: rm.getParticipants() ) {
            participantAdapter.add(p.getName());
        }
        participantSpinner.setAdapter(participantAdapter);

        // Initialize the data in the participant spinner
        Spinner eventSpinner = (Spinner) findViewById(R.id.eventspinner);
        ArrayAdapter<CharSequence> eventAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Event e: rm.getEvents() ) {
            eventAdapter.add(e.getName());
        }
        eventSpinner.setAdapter(eventAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText());
        args.putInt("id", v.getId());

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getTimeFromLabel(tf.getText());
        args.putInt("id", v.getId());
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private Bundle getTimeFromLabel(CharSequence text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split(":");
        int hour = 12;
        int minute = 0;

        if (comps.length == 2) {
            hour = Integer.parseInt(comps[0]);
            minute = Integer.parseInt(comps[1]);
        }

        rtn.putInt("hour", hour);
        rtn.putInt("minute", minute);

        return rtn;
    }

    private Bundle getDateFromLabel(CharSequence text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split("-");
        int day = 1;
        int month = 1;
        int year = 1;

        if (comps.length == 3) {
            day = Integer.parseInt(comps[0]);
            month = Integer.parseInt(comps[1]);
            year = Integer.parseInt(comps[2]);
        }

        rtn.putInt("day", day);
        rtn.putInt("month", month-1);
        rtn.putInt("year", year);

        return rtn;
    }

    public void setTime(int id, int h, int m) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d:%02d", h, m));
    }

    public void setDate(int id, int d, int m, int y) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d-%02d-%04d", d, m + 1, y));
    }
}

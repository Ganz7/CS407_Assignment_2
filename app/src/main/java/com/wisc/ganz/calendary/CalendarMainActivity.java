package com.wisc.ganz.calendary;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.TimeZone;

public class CalendarMainActivity extends AppCompatActivity {

    CalendarView calendar;
    ContentValues values;
    Uri calendarURI;

    static final int MY_PERMISSIONS_REQUEST_RW_CALENDAR = 101;
    static final String ACCOUNT_NAME = "CYUserAccount";
    static final String CALENDAR_ID_STRING = "CALENDAR_ID";
    static final String SELECTED_DATE_STRING = "SELECTED_DATE";

    static long CALENDAR_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeCalendarView();
        createCalendar();
        setAndHandleDateSelect();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(view.getContext(), CreateEvent.class);
                createIntent.putExtra(CALENDAR_ID_STRING, CALENDAR_ID);
                startActivity(createIntent);
            }
        });
    }

    /***
     * Initialize the calendar view and add any additional parameters to the calendar
     * if necessary
     */
    private void initializeCalendarView() {
        calendar = (CalendarView) findViewById(R.id.calendar_main_view);
    }

    /**
     * Sets the DateChangeListener for the calendar view and handles
     * onSelectDayChange events
     */
    private void setAndHandleDateSelect() {
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = String.format("%02d", (month+1)) + "/" +
                        String.format("%02d", dayOfMonth) + "/" + year;
                Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();

                Intent viewEventsIntent = new Intent(view.getContext(), ViewEvents.class);
                viewEventsIntent.putExtra(SELECTED_DATE_STRING, date);
                viewEventsIntent.putExtra(CALENDAR_ID_STRING, CALENDAR_ID);
                startActivity(viewEventsIntent);
            }
        });
    }

    /***
     * Create a calendar locally and set it's ID
     */
    private void createCalendar() {
        CALENDAR_ID = getCalendarId();
        /*
        If Calendar already exists, do not create it!
         */
        if(CALENDAR_ID != -1){
            return;
        }
        else {
            values = new ContentValues();
            values.put(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
            values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL); // Do not sync
            values.put(Calendars.NAME, "CalYCalendar");
            values.put(Calendars.CALENDAR_DISPLAY_NAME, "CS407's Calendar");
            values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
            values.put(Calendars.OWNER_ACCOUNT, "ganzse7en@gmail.com");
            values.put(Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
            values.put(Calendars.SYNC_EVENTS, 1); //Store the contents on the device
            values.put(Calendars.VISIBLE, 1);

            Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
            builder.appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
            builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");
            calendarURI = getContentResolver().insert(builder.build(), values);

            CALENDAR_ID = getCalendarId();
        }
    }

    private long getCalendarId() {
        String[] projection = new String[]{Calendars._ID};
        String selection = Calendars.ACCOUNT_NAME + " = ? AND " + Calendars.ACCOUNT_TYPE + " = ? ";
        String[] selArgs = new String[]{ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL};

        checkForAndRequestPermission();
        Cursor cursor = getContentResolver().query(
                Calendars.CONTENT_URI,
                projection,
                selection,
                selArgs,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long returnValue = cursor.getLong(0);
                cursor.close();
                return returnValue;
            }
        }
        return -1; //Return an invalid ID if the calendar does not exist
    }

    /***
     * Check for and request calendar Read/Write persmission
     * Not implemented completely for the scope of this assignment.
     */
    private void checkForAndRequestPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.CALENDAR) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.CALENDAR},
                    MY_PERMISSIONS_REQUEST_RW_CALENDAR);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RW_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Carry on

                } else {
                    // permission was denied.
                    // Obviously not production ready. But works for this assignment

                }
            }
        }
    }

}

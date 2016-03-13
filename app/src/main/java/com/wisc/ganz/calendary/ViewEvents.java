package com.wisc.ganz.calendary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewEvents extends AppCompatActivity {

    static final String SELECTED_DATE_STRING = "SELECTED_DATE";
    static long NUMBER_OF_MILLIS_IN_A_DAY = 86700000;
    private long CALENDAR_ID;
    private static final String CALENDAR_ID_STRING = "CALENDAR_ID";
    private String SELECTED_DATE;

    private ListView eventListView;
    private EventListCursorAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        SELECTED_DATE = extras.getString(SELECTED_DATE_STRING);
        CALENDAR_ID = extras.getLong(CALENDAR_ID_STRING);

        eventListView = (ListView) findViewById(R.id.event_list);

        getEvent(SELECTED_DATE);
    }

    private void getEvent(String dateString){
        checkForAndRequestPermission();
        String[] projection = new String[] {
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND};

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy"); //Custom time format

        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            Log.e("Parse Exception", "Error : " + e.getMessage());
        }

        long startTimeInMillis = date.getTime();

        String selection = "(( " + CalendarContract.Events.DTSTART +
                " >= " + startTimeInMillis + " ) AND ( " +
                CalendarContract.Events.DTSTART + " <= " +
                (startTimeInMillis+NUMBER_OF_MILLIS_IN_A_DAY-1) + " ) AND ("+
                CalendarContract.Events.CALENDAR_ID +" == "+ CALENDAR_ID + "))";

        Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection,
                selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            eventAdapter = new EventListCursorAdapter(this, cursor, 0);
            eventListView.setAdapter(eventAdapter);
            /*
            do {
                //Only display events for this app's calendar
                if(cursor.getLong(0) == CALENDAR_ID) {

                    Toast.makeText(this.getApplicationContext(), "In GETEVENTS Title: " + cursor.getString(1) +
                            " Start-Time: " + (new Date(cursor.getLong(3))).toString(), Toast.LENGTH_SHORT).show();
                }
            } while ( cursor.moveToNext());
            */
        }
    }

    /***
     * Checks if Calendar Read/Write permission has been granted. If not,
     * Requests for it.
     */
    private void checkForAndRequestPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.CALENDAR) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.CALENDAR},
                    CalendarMainActivity.MY_PERMISSIONS_REQUEST_RW_CALENDAR);
            return;
        }
    }
}

package com.wisc.ganz.calendary;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

    private void getEvent(final String dateString){
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
            eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long eventID) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(ViewEvents.this);
                    ad.setTitle("Delete");
                    ad.setMessage("Sure you want to delete record ?");
                    ad.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ViewEvents.this, "Clicked : " + eventID, Toast.LENGTH_SHORT).show();

                            ContentResolver cr = getContentResolver();
                            ContentValues values = new ContentValues();
                            Uri deleteUri = null;

                            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                            getContentResolver().delete(deleteUri, null, null);

                            getEvent(dateString); //Call the method recursively to repopulate
                        }
                    });

                    ad.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); //Close dialog
                        }
                    });

                    ad.show();
                    return false;
                }
            });
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

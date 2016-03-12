package com.wisc.ganz.calendary;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        addEvent();
    }

    private void addEvent() {
        long calID = 5;
        long startMillis = 0;
        long endMillis = 0;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 2, 14, 7, 40);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 2, 14, 8, 40);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, "Dancing!");
        values.put(Events.DESCRIPTION, "Group workout");
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "America/Chicago");

        checkForAndRequestPermission();

        Uri uri = cr.insert(Events.CONTENT_URI, values);

        long eventID = Long.parseLong(uri.getLastPathSegment());
        Toast.makeText(this, "Event ID is"+eventID, Toast.LENGTH_LONG).show();
    }

    private void checkForAndRequestPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.CALENDAR) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.CALENDAR},
                    CalendarMainActivity.MY_PERMISSIONS_REQUEST_RW_CALENDAR);
            return;
        }
    }
}

package com.wisc.ganz.calendary;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

public class CalendarMainActivity extends AppCompatActivity {

    CalendarView calendar;
    ContentValues values;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initializeCalendarView(){
        calendar = (CalendarView) findViewById(R.id.calendar_main_view);
    }

    /**
     * Sets the DateChangeListener for the calendar view and handles
     * onSelectDayChange events
     */
    private void setAndHandleDateSelect(){
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(getApplicationContext(), (month + 1) + "/" + dayOfMonth +
                        "/" + year, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createCalendar(){
        values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, "CYUserAccount");
        values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);// Do not sync
        values.put(Calendars.NAME, "CYCalendar");
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "407's Calendar");
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        values.put(Calendars.OWNER_ACCOUNT, "ganzse7en@gmail.com");
        values.put(Calendars.CALENDAR_TIME_ZONE, "America/Chicago");
        values.put(Calendars.SYNC_EVENTS, 1); //Store the contents on the device
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar_main, menu);
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
}

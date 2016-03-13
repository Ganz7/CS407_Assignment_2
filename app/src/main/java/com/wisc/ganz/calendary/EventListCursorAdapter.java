package com.wisc.ganz.calendary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Date;

/***
 * Custom Cursor Adapter for Event List
 */
public class EventListCursorAdapter extends CursorAdapter {

    public EventListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView eventTitle = (TextView) view.findViewById(R.id.event_title);
        TextView eventTimes = (TextView) view.findViewById(R.id.event_timings);
        TextView eventDescription = (TextView) view.findViewById(R.id.event_description);

        eventTitle.setText(cursor.getString(2));

        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(new Date(cursor.getLong(4)).toString().trim());
        timeBuilder.append(" to ");
        timeBuilder.append(new Date(cursor.getLong(5)).toString().trim());
        eventTimes.setText(timeBuilder);

        eventDescription.setText("Description: " + cursor.getString(3));
    }

}

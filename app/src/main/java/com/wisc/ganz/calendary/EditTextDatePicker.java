package com.wisc.ganz.calendary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Ganz on 3/12/16.
 */
public class EditTextDatePicker implements OnClickListener, OnDateSetListener {
    EditText editText;
    private int day;
    private int month;
    private int year;
    private Context context;

    public EditTextDatePicker(Context context, int editTextViewID)
    {
        Activity activity = (Activity)context;
        this.editText = (EditText)activity.findViewById(editTextViewID);
        this.editText.setOnClickListener(this);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Calendar myCalendar = Calendar.getInstance();
        new DatePickerDialog(context, this, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;

        updateEditText();
    }
    private void updateEditText(){
        StringBuilder date = new StringBuilder();
        date.append(month+1).append("/").append(day).append("/").append(year).append(" ");
        editText.setText(date);
    }
}

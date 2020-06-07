package com.ics.freecashregister.helper;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

public class EditTextDatePicker implements OnClickListener, OnDateSetListener {
    private int _month, _year;
    private Context _context;
    private EditText _editTextView;

    public EditTextDatePicker(Context context, EditText editTextView) {
        this._editTextView = editTextView;
        editTextView.setOnClickListener(this);
        this._context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _year = year - 2000;
        _month = monthOfYear;
        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(_context, this, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        //((ViewGroup) dialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        dialog.show();
    }

    // updates the date in the birth date EditText
    private void updateDisplay() {

        _editTextView.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(String.format("%02d", (_month + 1))).append("/").append(String.format("%02d", _year)));
    }
}

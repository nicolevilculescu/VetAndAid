package com.example.vetandaid;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        SharedPreferences settings = requireActivity().getSharedPreferences(Constants.PREFS_DATE, Context.MODE_PRIVATE);
        String date = settings.getString("date", "default");

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);

        if (date.equals("after")) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
        }

        return datePickerDialog;
    }
}

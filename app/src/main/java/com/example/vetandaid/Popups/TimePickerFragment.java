package com.example.vetandaid.Popups;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment  implements TimePickerDialog.OnTimeSetListener{

    private int mId;
    private TimePickedListener mListener;

    public static TimePickerFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("picker_id", id);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // the current time is used as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        assert getArguments() != null;
        mId = getArguments().getInt("picker_id");

        // a new instance of TimePickerDialog is created and returned
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onAttach(@NotNull Context context) {
        // when the fragment is initially shown (i.e. attached to the activity), the activity is cast to the callback interface type
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a = (Activity) context;

            try {
                mListener = (TimePickedListener) a;
            } catch(ClassCastException e) {
                throw new ClassCastException(a.toString()
                        + " must implement " + TimePickedListener.class.getName());
            }
        }
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // when the time is selected, it is sent to the activity via its callback interface method
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        if(mListener != null)
            mListener.onTimePicked(c, mId);
    }

    public interface TimePickedListener {
        void onTimePicked(Calendar time, int id);
    }
}

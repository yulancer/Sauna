package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TimePicker;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StartSaunaDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_PARAM = "start_params";
    long mSecondsRemain;
    long mSecondsDelay;
    LocalTime mDialogStartTime;

    private OnFragmentInteractionListener mListener;

    public static StartSaunaDialog newInstance(long secondsRemain) {
        StartSaunaDialog fragment = new StartSaunaDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM, secondsRemain);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mSecondsRemain = args.getLong(ARG_PARAM);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View form = getActivity().getLayoutInflater().inflate(R.layout.activity_start_sauna_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle("Сауна будет готова к этому часу").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        TimePicker tpReadyTime = (TimePicker) form.findViewById(R.id.tpReadyTime);
        if (tpReadyTime != null) {
            mDialogStartTime = new LocalTime();
            DateTimeFormatter fmt = DateTimeFormat.shortTime().withLocale(getResources().getConfiguration().locale);
            LocalTime readyTime = mDialogStartTime.plusSeconds((int) mSecondsRemain);
            tpReadyTime.setCurrentHour(readyTime.getHourOfDay());
            tpReadyTime.setCurrentMinute(readyTime.getMinuteOfHour());
            tpReadyTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    calculateStartDelay(hourOfDay, minute);
                }
            });
        }
        return dialog;
    }

    private void calculateStartDelay(int hourOfDay, int minute) {
        LocalTime delayedReadyTime = new LocalTime(hourOfDay, minute);
        LocalTime noDelayReadyTime = mDialogStartTime.plusSeconds((int) mSecondsRemain);
        int delayMillis = delayedReadyTime.getMillisOfDay() - noDelayReadyTime.getMillisOfDay();
        if (delayMillis > 0) {
            mSecondsDelay = delayMillis / 1000;

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mListener.onStartSauna(mSecondsDelay);
    }

    public interface OnFragmentInteractionListener {
        void onStartSauna(long startDelay);
    }
}

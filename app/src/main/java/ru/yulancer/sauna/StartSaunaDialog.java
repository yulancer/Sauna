package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class StartSaunaDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_PARAM = "start_params";
    long mSecondsRemain;
    long mSecondsDelay;
    LocalTime mDialogStartTime;

    TextView mTvResetDelay;
    TextView mTvDelay;
    TimePicker mTpReadyTime;

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

        mTpReadyTime = (TimePicker) form.findViewById(R.id.tpReadyTime);
        if (mTpReadyTime != null) {
            mDialogStartTime = new LocalTime();
            DateTimeFormatter fmt = DateTimeFormat.shortTime().withLocale(getResources().getConfiguration().locale);
            LocalTime readyTime = mDialogStartTime.plusSeconds((int) mSecondsRemain);
            mTpReadyTime.setCurrentHour(readyTime.getHourOfDay());
            mTpReadyTime.setCurrentMinute(readyTime.getMinuteOfHour());
            mTpReadyTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    calculateStartDelay(hourOfDay, minute);
                }
            });
        }

        //
        mTvDelay = (TextView) form.findViewById(R.id.tvDelay);
        mTvResetDelay = (TextView) form.findViewById(R.id.tvResetDelay);
        SpannableString string = new SpannableString(mTvResetDelay.getText());
        string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        mTvResetDelay.setText(string);
        mTvResetDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSaunaDialog.this.onResetDelayClick(v);
            }
        });

        return dialog;
    }

    private void calculateStartDelay(int hourOfDay, int minute) {
        LocalTime delayedReadyTime = new LocalTime(hourOfDay, minute);
        LocalTime noDelayReadyTime = mDialogStartTime.plusSeconds((int) mSecondsRemain);
        int delayMillis = delayedReadyTime.getMillisOfDay() - noDelayReadyTime.getMillisOfDay();
        if (Math.abs(delayMillis) < 60000) //задержки меньше минуты не рассматриваем
            delayMillis = 0;
        if (delayMillis < 0) {             //время назад считаем следующими сутками
            delayMillis = 24 * 3600 * 1000 - delayMillis;
        }
        mSecondsDelay = delayMillis / 1000;
        DateTimeFormatter fmt = ISODateTimeFormat.hourMinute();
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendHours()
                .appendSuffix(":")
                .appendMinutes()
                .toFormatter();
        String delayTimeString = formatter.print(new Period(delayMillis));
        //String delayTimeString2 = ISOPeriodFormat.standard().print(new Period(70000));
        //String delayTimeString = new DateTime(delayMillis).toString();
        mTvDelay.setText(String.format("Задержка на %s", delayTimeString));
        mTvResetDelay.setEnabled(mSecondsDelay > 60);

    }

    public void onResetDelayClick(View v) {
        mSecondsDelay = 0;
        LocalTime noDelayReadyTime = mDialogStartTime.plusSeconds((int) mSecondsRemain);
        mTpReadyTime.setCurrentHour(noDelayReadyTime.getHourOfDay());
        mTpReadyTime.setCurrentMinute(noDelayReadyTime.getMinuteOfHour());
        mTpReadyTime.invalidate();
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

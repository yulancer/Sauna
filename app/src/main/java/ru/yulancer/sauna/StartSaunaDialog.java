package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class StartSaunaDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_PARAM = "start_params";
    long mSecondsRemain;
    long mSecondsDelay;
    LocalTime mDialogStartTime;

    TextView mTvDelay;
    TextView mTvReady;

    TimePicker mTpStartTime;

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
        Dialog dialog = builder.setTitle("Включить все нагреватели").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        mTpStartTime = (TimePicker) form.findViewById(R.id.tpReadyTime);
        mTpStartTime.setIs24HourView(true);
        if (mTpStartTime != null) {
            mDialogStartTime = new LocalTime();
            LocalTime startTime = new LocalTime();
            mTpStartTime.setCurrentHour(startTime.getHourOfDay());
            mTpStartTime.setCurrentMinute(startTime.getMinuteOfHour());

            mTpStartTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    calculateStartDelay(hourOfDay, minute);
                }
            });
        }

        //
        mTvDelay = (TextView) form.findViewById(R.id.tvDelay);

        mTvReady = (TextView) form.findViewById(R.id.tvReady);
        UpdateReadyTimeText(false);

        return dialog;
    }

    private void onResetTimeClick(View v) {
        LocalTime currentTime = new LocalTime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTpStartTime.setHour(currentTime.getHourOfDay());
            mTpStartTime.setMinute(currentTime.getMinuteOfHour());
        } else {
            mTpStartTime.setCurrentHour(currentTime.getHourOfDay());
            mTpStartTime.setCurrentMinute(currentTime.getMinuteOfHour());
        }
    }

    private void calculateStartDelay(int hourOfDay, int minute) {
        boolean readyTomorrow = false;
        LocalTime userInputTime = new LocalTime(hourOfDay, minute);
        int delayMillis = userInputTime.getMillisOfDay() - mDialogStartTime.getMillisOfDay();
        if (Math.abs(delayMillis) < 60000) //задержки меньше минуты не рассматриваем
            delayMillis = 0;
        if (delayMillis < 0) {             //время назад считаем следующими сутками
            delayMillis = 24 * 3600 * 1000 - delayMillis;
            readyTomorrow = true;
        }
        mSecondsDelay = delayMillis / 1000;
        boolean noDelay = delayMillis < 70000;
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendHours()
                .appendSuffix(":")
                .appendMinutes()
                .toFormatter();

        String delayTimeString = formatter.print(new Period(delayMillis));
        mTvDelay.setText(noDelay ? "Немедленный старт" : String.format("Задержка на %s", delayTimeString));

        UpdateReadyTimeText(readyTomorrow);
    }

    private void UpdateReadyTimeText(boolean readyTomorrow){
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendHours()
                .appendSuffix(":")
                .appendMinutes()
                .toFormatter();
        LocalTime readyTime = mDialogStartTime.plusSeconds((int) mSecondsRemain).plusSeconds((int) mSecondsDelay);
        String readyTimeString = formatter.print(new Period(readyTime.getMillisOfDay()));
        String tomorrowString = readyTomorrow ? " завтра" : "";
        mTvReady.setText(String.format("Готово%s в %s", tomorrowString, readyTimeString));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.setStartSaunaDialog(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.setStartSaunaDialog(null);
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mListener.onStartSauna(mSecondsDelay);
    }

    public interface OnFragmentInteractionListener {
        void onStartSauna(long startDelay);

        StartSaunaDialog getStartSaunaDialog();

        void setStartSaunaDialog(StartSaunaDialog startSaunaDialog);
    }
}

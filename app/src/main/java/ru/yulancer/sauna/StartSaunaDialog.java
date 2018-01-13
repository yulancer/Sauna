package ru.yulancer.sauna;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class StartSaunaDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_PARAM = "start_params";
    long mMaxHeatingSeconds;
    long mRequiredToReadySeconds;
    LocalTime mDialogStartTime;

    TextView mTvDelay;
    TextView mTvReady;

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
            mMaxHeatingSeconds = args.getLong(ARG_PARAM);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View form = getActivity().getLayoutInflater().inflate(R.layout.activity_start_sauna_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle("Время готовности").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        mTpReadyTime = (TimePicker) form.findViewById(R.id.tpReadyTime);
        mTpReadyTime.setIs24HourView(true);
        if (mTpReadyTime != null) {
            mDialogStartTime = new LocalTime();

            LocalTime readyTime = new LocalTime().plusSeconds((int) mMaxHeatingSeconds);
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

        mTvReady = (TextView) form.findViewById(R.id.tvReady);
        UpdateReadyTimeText(false);

        return dialog;
    }

    private void onResetTimeClick(View v) {
        LocalTime currentTime = new LocalTime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTpReadyTime.setHour(currentTime.getHourOfDay());
            mTpReadyTime.setMinute(currentTime.getMinuteOfHour());
        } else {
            mTpReadyTime.setCurrentHour(currentTime.getHourOfDay());
            mTpReadyTime.setCurrentMinute(currentTime.getMinuteOfHour());
        }
    }

    private void calculateStartDelay(int hourOfDay, int minute) {
        boolean readyTomorrow = false;
        LocalTime userInputTime = new LocalTime(hourOfDay, minute);
        int delayMillisToReady = userInputTime.getMillisOfDay() - mDialogStartTime.getMillisOfDay();
        if (Math.abs(delayMillisToReady) < 60000) //задержки меньше минуты не рассматриваем
            delayMillisToReady = 0;
        if (delayMillisToReady < 0) {             //время назад считаем следующими сутками
            delayMillisToReady = 24 * 3600 * 1000 - delayMillisToReady;
            readyTomorrow = true;
        }
        int delayMillisToStart = (int) (delayMillisToReady - mMaxHeatingSeconds * 1000);
        if (delayMillisToStart < 0)  // если задержка отрицательная, включаем сразу
            delayMillisToStart = 0;
        mRequiredToReadySeconds = delayMillisToReady / 1000;
        boolean noDelay = delayMillisToStart < 70000;
        boolean partialDelay = false;
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendHours()
                .appendSuffix(":")
                .appendMinutes()
                .toFormatter();

        String delayTimeString = formatter.print(new Period(delayMillisToStart));
        mTvDelay.setText(noDelay ? "Немедленный старт" : (partialDelay ? "Частичный старт" : String.format("Задержка на %s", delayTimeString)));

        UpdateReadyTimeText(readyTomorrow);
    }

    private void UpdateReadyTimeText(boolean readyTomorrow){
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendHours()
                .appendSuffix(":")
                .appendMinutes()
                .toFormatter();
        int remainSeconds = Math.max((int) mRequiredToReadySeconds, (int) mMaxHeatingSeconds);
        LocalTime readyTime = mDialogStartTime.plusSeconds(remainSeconds);
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
        mListener.onStartSauna(mRequiredToReadySeconds);
    }

    public interface OnFragmentInteractionListener {
        void onStartSauna(long startDelay);

        StartSaunaDialog getStartSaunaDialog();

        void setStartSaunaDialog(StartSaunaDialog startSaunaDialog);
    }
}

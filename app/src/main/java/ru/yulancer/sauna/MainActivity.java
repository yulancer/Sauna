package ru.yulancer.sauna;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import static ru.yulancer.sauna.R.color.colorHeaterReady;
import static ru.yulancer.sauna.R.color.colorHeaterWarming;

public class MainActivity extends FragmentActivity implements SettingsDialog.OnFragmentInteractionListener {

    public static final String SaunaInfoTag = "SaunaInfoTag";

    private SaunaSettings mSaunaSettings = new SaunaSettings();
    private SaunaInfo mSaunaInfo;

    //private IModbusActor mActor = new Modbus4jActor("192.168.1.77", 502);
     private IModbusActor mActor = new Modbus4jActor("10.0.2.2", 502);
    //private IModbusActor mActor = new J2modActor("10.0.2.2", 502);

    @Override
    public void onSaveSettings(SaunaSettings saunaSettings) {
        mSaunaSettings = saunaSettings;
        SaveSettingsTask t = new SaveSettingsTask();
        t.execute();
    }

    class RefreshValuesTask extends AsyncTask<Void, Void, SaunaInfo> {

        @Override
        protected SaunaInfo doInBackground(Void... params) {

            return mActor.GetSaunaInfo();
        }

        @Override
        protected void onPostExecute(SaunaInfo saunaInfo) {
            mSaunaInfo = saunaInfo;
            RefreshSaunaInfo();

            ProgressBar p = (ProgressBar) findViewById(R.id.progressBar);
            p.setVisibility(View.GONE);
            ImageButton b = (ImageButton) findViewById(R.id.btnTest);
            b.setEnabled(true);
        }


        @Override
        protected void onPreExecute() {
            ImageButton b = (ImageButton) findViewById(R.id.btnTest);
            b.setEnabled(false);
            ProgressBar p = (ProgressBar) findViewById(R.id.progressBar);
            p.setVisibility(View.VISIBLE);
        }

    }

    class StartSaunaTask extends RefreshValuesTask {

        @Override
        protected SaunaInfo doInBackground(Void... params) {
            mActor.SendSwitchSignal();
            return super.doInBackground(params);
        }
    }

    class SaveSettingsTask extends RefreshValuesTask {

        @Override
        protected SaunaInfo doInBackground(Void... params) {
            mActor.SaveSettings(mSaunaSettings);
            return super.doInBackground(params);
        }
    }

    private void RefreshSaunaInfo() {
        if (mSaunaInfo != null) {
            TextView t0 = (TextView) findViewById(R.id.tempSaunaCurrent);
            TextView t1 = (TextView) findViewById(R.id.tempBoilerCurrent);
            TextView t2 = (TextView) findViewById(R.id.tempRoomCurrent);
            TextView t3 = (TextView) findViewById(R.id.tempWaterpipe);
            TextView t4 = (TextView) findViewById(R.id.tempOutdoor);
            TextView t5 = (TextView) findViewById(R.id.tempSaunaSetPoint);
            TextView t6 = (TextView) findViewById(R.id.tempBoilerSetPoint);
            TextView t7 = (TextView) findViewById(R.id.tempRoomSetPoint);
            TextView tvSaunaHeaterStatus = (TextView) findViewById(R.id.tempSaunaHeaterStatus);
            TextView tvBoilerHeaterStatus = (TextView) findViewById(R.id.tempBoilerHeaterStatus);
            TextView tvRoomHeaterStatus = (TextView) findViewById(R.id.tempRoomHeaterStatus);
            TextView tvDoorShower = (TextView) findViewById(R.id.tvDoorShower);
            TextView tvDoorSauna = (TextView) findViewById(R.id.tvDoorSauna);
            ToggleButton tbSaunaOn = (ToggleButton) findViewById(R.id.btnSaunaOn);
            TextView tvException = (TextView) findViewById(R.id.tvException);

            TextView tvSaunaReady = (TextView) findViewById(R.id.tvSaunaReady);
            TextView tvBoilerReady = (TextView) findViewById(R.id.tvBoilerReady);
            TextView tvRoomReady = (TextView) findViewById(R.id.tvRoomReady);

            if (mSaunaInfo.exception == null) {
                t0.setText(String.format("%.2f", mSaunaInfo.SaunaCurrentTemp));
                t1.setText(String.format("%.2f", mSaunaInfo.BoilerCurrentTemp));
                t2.setText(String.format("%.2f", mSaunaInfo.RoomCurrentTemp));
                t3.setText(String.format("%.2f", mSaunaInfo.WaterPipeCurrentTemp));
                t4.setText(String.format("%.2f", mSaunaInfo.OutdoorCurrentTemp));
                t5.setText(String.format("%.2f", mSaunaInfo.SaunaSetpoint));
                t6.setText(String.format("%.2f", mSaunaInfo.BoilerSetpoint));
                t7.setText(String.format("%.2f", mSaunaInfo.RoomSetpoint));

                tvSaunaHeaterStatus.setText(mSaunaInfo.SaunaHeaterOn ? "I" : "O");
                tvBoilerHeaterStatus.setText(mSaunaInfo.BoilerHeaterOn ? "I" : "O");
                tvRoomHeaterStatus.setText(mSaunaInfo.RoomHeaterOn ? "I" : "O");
                tvDoorSauna.setText(mSaunaInfo.DoorSaunaOpen ? "открыта" : "закрыта");
                tvDoorShower.setText(mSaunaInfo.DoorShowerOpen ? "открыта" : "закрыта");
                tbSaunaOn.setChecked(mSaunaInfo.SaunaOn);

                mSaunaSettings.SaunaSetpoint = mSaunaInfo.SaunaSetpoint;
                mSaunaSettings.BoilerSetpoint = mSaunaInfo.BoilerSetpoint;
                mSaunaSettings.RoomSetpoint = mSaunaInfo.RoomSetpoint;

                tvException.setText("");

                if (!mSaunaInfo.SaunaOn) {
                    tvSaunaReady.setText("Выкл");
                    tvSaunaReady.setTextColor(Color.GRAY);
                    tvRoomReady.setText("Выкл");
                    tvRoomReady.setTextColor(Color.GRAY);
                    tvBoilerReady.setText("Выкл");
                    tvBoilerReady.setTextColor(Color.GRAY);
                } else {
                    LocalTime currentTime = new LocalTime ();
                    DateTimeFormatter fmt = DateTimeFormat.shortTime().withLocale(getResources().getConfiguration().locale);

                    LocalTime saunaReadyTime = currentTime.plusSeconds((int) mSaunaInfo.SaunaSecondsRemain);
                    tvSaunaReady.setText(mSaunaInfo.SaunaReady ? "Готова" : fmt.print(saunaReadyTime));
                    tvSaunaReady.setTextColor(getResources().getColor(mSaunaInfo.SaunaReady ? colorHeaterReady : colorHeaterWarming));

                    LocalTime boilerReadyTime = currentTime.plusSeconds((int) mSaunaInfo.BoilerSecondsRemain);
                    tvBoilerReady.setText(mSaunaInfo.BoilerReady ? "Готова" : fmt.print(boilerReadyTime));
                    tvBoilerReady.setTextColor(getResources().getColor(mSaunaInfo.BoilerReady ? colorHeaterReady : colorHeaterWarming));

                    LocalTime roomReadyTime = currentTime.plusSeconds((int) mSaunaInfo.RoomSecondsRemain);
                    tvRoomReady.setText(mSaunaInfo.RoomReady ? "Готова" : fmt.print(roomReadyTime));
                    tvRoomReady.setTextColor(getResources().getColor(mSaunaInfo.RoomReady ? colorHeaterReady : colorHeaterWarming));
                }
            } else {
                t0.setText("");
                t1.setText("");
                t2.setText("");
                t3.setText("");
                t4.setText("");
                t5.setText("");
                t6.setText("");
                t7.setText("");
                tvException.setText(mSaunaInfo.exception.getLocalizedMessage());

                tvSaunaReady.setText("?");
                tvSaunaReady.setTextColor(Color.GRAY);
                tvRoomReady.setText("?");
                tvRoomReady.setTextColor(Color.GRAY);
                tvBoilerReady.setText("?");
                tvBoilerReady.setTextColor(Color.GRAY);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSaunaInfo != null) {
            outState.putParcelable(SaunaInfoTag, mSaunaInfo);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Parcelable savedInfo = savedInstanceState.getParcelable(SaunaInfoTag);
        if (savedInfo != null) {
            mSaunaInfo = (SaunaInfo) savedInfo;
            RefreshSaunaInfo();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        RefreshValuesTask t = new RefreshValuesTask();
        t.execute();
    }

    public void onStartClick(View v) {
        StartSaunaTask t = new StartSaunaTask();
        t.execute();
    }

    public void onSettingsClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialog dialog = SettingsDialog.newInstance(mSaunaSettings);
        dialog.show(fm, "settings");
    }
}

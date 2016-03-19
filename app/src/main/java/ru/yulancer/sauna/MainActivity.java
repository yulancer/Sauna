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

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static ru.yulancer.sauna.R.color.colorHeaterReady;
import static ru.yulancer.sauna.R.color.colorHeaterWarming;

public class MainActivity extends FragmentActivity implements SettingsDialog.OnFragmentInteractionListener {

    public static final String SaunaInfoTag = "SaunaInfoTag";

    private SaunaSettings mSaunaSettings = new SaunaSettings();
    private SaunaInfo mSaunaInfo;

    private IModbusActor mActor = new Modbus4jActor("192.168.1.77", 502);
    //private IModbusActor mActor = new Modbus4jActor("10.0.2.2", 502);
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
                if (t0 != null)
                    t0.setText(String.format("%.2f", mSaunaInfo.SaunaCurrentTemp));
                if (t1 != null)
                    t1.setText(String.format("%.2f", mSaunaInfo.BoilerCurrentTemp));
                if (t2 != null)
                    t2.setText(String.format("%.2f", mSaunaInfo.RoomCurrentTemp));
                if (t3 != null)
                    t3.setText(String.format("%.2f", mSaunaInfo.WaterPipeCurrentTemp));
                if (t4 != null)
                    t4.setText(String.format("%.2f", mSaunaInfo.OutdoorCurrentTemp));
                if (t5 != null)
                    t5.setText(String.format("%.2f", mSaunaInfo.SaunaSetpoint));
                if (t6 != null)
                    t6.setText(String.format("%.2f", mSaunaInfo.BoilerSetpoint));
                if (t7 != null)
                    t7.setText(String.format("%.2f", mSaunaInfo.RoomSetpoint));

                if (tvSaunaHeaterStatus != null)
                    tvSaunaHeaterStatus.setText(mSaunaInfo.SaunaHeaterOn ? "I" : "O");
                if (tvBoilerHeaterStatus != null)
                    tvBoilerHeaterStatus.setText(mSaunaInfo.BoilerHeaterOn ? "I" : "O");
                if (tvRoomHeaterStatus != null)
                    tvRoomHeaterStatus.setText(mSaunaInfo.RoomHeaterOn ? "I" : "O");
                if (tvDoorSauna != null)
                    tvDoorSauna.setText(mSaunaInfo.DoorSaunaOpen ? "открыта" : "закрыта");
                if (tvDoorShower != null)
                    tvDoorShower.setText(mSaunaInfo.DoorShowerOpen ? "открыта" : "закрыта");
                if (tbSaunaOn != null)
                    tbSaunaOn.setChecked(mSaunaInfo.SaunaOn);

                mSaunaSettings.SaunaSetpoint = mSaunaInfo.SaunaSetpoint;
                mSaunaSettings.BoilerSetpoint = mSaunaInfo.BoilerSetpoint;
                mSaunaSettings.RoomSetpoint = mSaunaInfo.RoomSetpoint;

                if (tvException != null)
                    tvException.setText("");

                if (!mSaunaInfo.SaunaOn) {
                    if (tvSaunaReady != null) {
                        tvSaunaReady.setText("Выкл");
                        tvSaunaReady.setTextColor(Color.GRAY);
                    }
                    if (tvRoomReady != null) {
                        tvRoomReady.setText("Выкл");
                        tvRoomReady.setTextColor(Color.GRAY);
                    }
                    if (tvBoilerReady != null) {
                        tvBoilerReady.setText("Выкл");
                        tvBoilerReady.setTextColor(Color.GRAY);
                    }
                } else {
                    LocalTime currentTime = new LocalTime();
                    DateTimeFormatter fmt = DateTimeFormat.shortTime().withLocale(getResources().getConfiguration().locale);

                    LocalTime saunaReadyTime = currentTime.plusSeconds((int) mSaunaInfo.SaunaSecondsRemain);
                    if (tvSaunaReady != null) {
                        tvSaunaReady.setText(mSaunaInfo.SaunaReady ? "Готова" : fmt.print(saunaReadyTime));
                        tvSaunaReady.setTextColor(getResources().getColor(mSaunaInfo.SaunaReady ? colorHeaterReady : colorHeaterWarming));
                    }
                    LocalTime boilerReadyTime = currentTime.plusSeconds((int) mSaunaInfo.BoilerSecondsRemain);
                    if (tvBoilerReady != null) {
                        tvBoilerReady.setText(mSaunaInfo.BoilerReady ? "Готова" : fmt.print(boilerReadyTime));
                        tvBoilerReady.setTextColor(getResources().getColor(mSaunaInfo.BoilerReady ? colorHeaterReady : colorHeaterWarming));
                    }
                    LocalTime roomReadyTime = currentTime.plusSeconds((int) mSaunaInfo.RoomSecondsRemain);
                    if (tvRoomReady != null) {
                        tvRoomReady.setText(mSaunaInfo.RoomReady ? "Готова" : fmt.print(roomReadyTime));
                        tvRoomReady.setTextColor(getResources().getColor(mSaunaInfo.RoomReady ? colorHeaterReady : colorHeaterWarming));
                    }
                }
            } else {
                if (t0 != null)
                    t0.setText("");
                if (t1 != null)
                    t1.setText("");
                if (t2 != null)
                    t2.setText("");
                if (t3 != null)
                    t3.setText("");
                if (t4 != null)
                    t4.setText("");
                if (t5 != null)
                    t5.setText("");
                if (t6 != null)
                    t6.setText("");
                if (t7 != null)
                    t7.setText("");
                if (tvException != null)
                    tvException.setText(mSaunaInfo.exception.getLocalizedMessage());

                if (tvSaunaReady != null) {
                    tvSaunaReady.setText("?");
                    tvSaunaReady.setTextColor(Color.GRAY);
                }
                if (tvRoomReady != null) {
                    tvRoomReady.setText("?");
                    tvRoomReady.setTextColor(Color.GRAY);
                }
                if (tvBoilerReady != null) {
                    tvBoilerReady.setText("?");
                    tvBoilerReady.setTextColor(Color.GRAY);
                }
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

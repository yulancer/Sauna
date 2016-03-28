package ru.yulancer.sauna;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Timer;
import java.util.TimerTask;

import static ru.yulancer.sauna.R.color.colorHeaterReady;
import static ru.yulancer.sauna.R.color.colorHeaterWarming;

public class MainActivity extends FragmentActivity implements SettingsDialog.OnFragmentInteractionListener, CompoundButton.OnCheckedChangeListener {

    public static final String SaunaInfoTag = "SaunaInfoTag";

    private SaunaSettings mSaunaSettings = new SaunaSettings();
    private SaunaInfo mSaunaInfo;
    private Timer mTimer;

    private IModbusActor mActivityActor = new Modbus4jActor("192.168.1.77", 502);
    //private IModbusActor mActivityActor = new Modbus4jActor("10.0.2.2", 502);
    //private IModbusActor mActivityActor = new J2modActor("10.0.2.2", 502);

    @Override
    public void onSaveSettings(SaunaSettings saunaSettings) {
        mSaunaSettings = saunaSettings;
        SaveSettingsTask t = new SaveSettingsTask();
        t.execute();
    }

    class SaunaQueryTask extends TimerTask {

       // private IModbusActor mTaskActor = new Modbus4jActor("10.0.2.2", 502);
        private IModbusActor mTaskActor = new Modbus4jActor("192.168.1.77", 502);

        private void switchProgress(boolean on) {
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            ImageView connectIcon = (ImageView) findViewById(R.id.ivConnectStatus);
            if (connectIcon != null)
                connectIcon.setVisibility(on ? View.GONE : View.VISIBLE);
            if (bar != null)
                bar.setVisibility(on ? View.VISIBLE : View.GONE);
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchProgress(true);
                }
            });

            MainActivity.this.mSaunaInfo = mTaskActor.GetSaunaInfo();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.RefreshSaunaInfo();
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchProgress(false);
                }
            });
        }

    }

    abstract class BaseCommunicationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            ImageView b = (ImageView) findViewById(R.id.ivConnectStatus);
            if (b != null)
                b.setVisibility(View.GONE);
            ProgressBar p = (ProgressBar) findViewById(R.id.progressBar);
            if (p != null)
                p.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void params) {
            RefreshSaunaInfo();

            ImageView b = (ImageView) findViewById(R.id.ivConnectStatus);
            if (b != null)
                b.setVisibility(View.VISIBLE);
            ProgressBar p = (ProgressBar) findViewById(R.id.progressBar);
            if (p != null)
                p.setVisibility(View.GONE);
        }
    }

    class StartSaunaTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Void... params) {
            mActivityActor.SendSwitchSignal();
            recreateRefreshTimer();
            return null;
        }
    }

    class SaveSettingsTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Void... params) {
            mActivityActor.SaveSettings(mSaunaSettings);
            recreateRefreshTimer();
            return null;
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
            ImageView ivSaunaHeaterStatus = (ImageView) findViewById(R.id.ivSaunaHeaterStatus);
            ImageView ivBoilerHeaterStatus = (ImageView) findViewById(R.id.ivBoilerHeaterStatus);
            ImageView ivRoomHeaterStatus = (ImageView) findViewById(R.id.ivRoomHeaterStatus);
            TextView tvDoorShower = (TextView) findViewById(R.id.tvDoorShower);
            TextView tvDoorSauna = (TextView) findViewById(R.id.tvDoorSauna);
            TextView tvException = (TextView) findViewById(R.id.tvException);

            TextView tvSaunaReady = (TextView) findViewById(R.id.tvSaunaReady);
            TextView tvBoilerReady = (TextView) findViewById(R.id.tvBoilerReady);
            TextView tvRoomReady = (TextView) findViewById(R.id.tvRoomReady);

            if (mSaunaInfo.exception == null) {
                currentTemperatureOutput(t0,mSaunaInfo.SaunaCurrentTemp, mSaunaInfo.SaunaOn, mSaunaInfo.SaunaReady);
                currentTemperatureOutput(t1,mSaunaInfo.BoilerCurrentTemp, mSaunaInfo.SaunaOn, mSaunaInfo.BoilerReady);
                currentTemperatureOutput(t2,mSaunaInfo.RoomCurrentTemp, mSaunaInfo.SaunaOn, mSaunaInfo.RoomReady);

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

                if (ivSaunaHeaterStatus != null)
                    ivSaunaHeaterStatus.setVisibility(mSaunaInfo.SaunaHeaterOn ? View.VISIBLE : View.INVISIBLE);
                if (ivBoilerHeaterStatus != null)
                    ivBoilerHeaterStatus.setVisibility(mSaunaInfo.BoilerHeaterOn ? View.VISIBLE : View.INVISIBLE);
                if (ivRoomHeaterStatus != null)
                    ivRoomHeaterStatus.setVisibility(mSaunaInfo.RoomHeaterOn ? View.VISIBLE : View.INVISIBLE);

                if (tvDoorSauna != null)
                    tvDoorSauna.setText(mSaunaInfo.DoorSaunaOpen ? "открыта" : "закрыта");
                if (tvDoorShower != null)
                    tvDoorShower.setText(mSaunaInfo.DoorShowerOpen ? "открыта" : "закрыта");

                Switch mainSwitch = (Switch) findViewById(R.id.mainSwitch);
                if (mainSwitch != null) {
                    mainSwitch.setChecked(mSaunaInfo.SaunaOn);
                }

                mSaunaSettings.SaunaSetpoint = mSaunaInfo.SaunaSetpoint;
                mSaunaSettings.BoilerSetpoint = mSaunaInfo.BoilerSetpoint;
                mSaunaSettings.RoomSetpoint = mSaunaInfo.RoomSetpoint;

                if (tvException != null && tvException.getVisibility() != View.GONE) {
                    tvException.setVisibility(View.GONE);
                    tvException.setText("");
                }

                remainSecondsOutput(tvSaunaReady, mSaunaInfo.SaunaSecondsRemain, mSaunaInfo.SaunaOn, mSaunaInfo.SaunaReady, mSaunaInfo.SaunaRemainHistorical);
                remainSecondsOutput(tvBoilerReady, mSaunaInfo.BoilerSecondsRemain, mSaunaInfo.SaunaOn, mSaunaInfo.BoilerReady, mSaunaInfo.BoilerRemainHistorical);
                remainSecondsOutput(tvRoomReady, mSaunaInfo.RoomSecondsRemain, mSaunaInfo.SaunaOn, mSaunaInfo.RoomReady, mSaunaInfo.RoomRemainHistorical);

                ImageView connectIcon = (ImageView) findViewById(R.id.ivConnectStatus);
                if (connectIcon != null)
                    connectIcon.setImageResource(R.drawable.ic_connect);
                TextView tvLastSuccessfulQuery = (TextView) findViewById(R.id.tvLastSuccessfulQuery);
                if (tvLastSuccessfulQuery != null) {
                    DateTimeFormatter fmt = DateTimeFormat.fullTime().withLocale(getResources().getConfiguration().locale);
                    tvLastSuccessfulQuery.setText(String.format("Данные на %s", fmt.print(new LocalTime())));
                }

            } else {
                if (tvException != null) {
                    tvException.setText(mSaunaInfo.exception.getLocalizedMessage());
                    tvException.setVisibility(View.VISIBLE);
                }
                ImageView connectIcon = (ImageView) findViewById(R.id.ivConnectStatus);
                if (connectIcon != null)
                    connectIcon.setImageResource(R.drawable.ic_disconnect);
            }
        }
    }

    private void currentTemperatureOutput(TextView tv, float temp, boolean isOn, boolean isReady){
        if (tv == null)
            return;
        tv.setText(String.format("%.2f", temp));
        if (!isOn) {
            tv.setTextColor(Color.BLACK);
        } else {
            tv.setTextColor(getResources().getColor(isReady ? colorHeaterReady : colorHeaterWarming));
        }
    }
    private void remainSecondsOutput(TextView tv, long seconds, boolean isOn, boolean isReady, boolean isHistorical) {
        if (tv == null)
            return;
        if (!isOn) {
            tv.setText("Выкл");
            tv.setTextColor(Color.GRAY);
        }
        LocalTime currentTime = new LocalTime();
        DateTimeFormatter fmt = DateTimeFormat.shortTime().withLocale(getResources().getConfiguration().locale);
        LocalTime readyTime = currentTime.plusSeconds((int) seconds);
        tv.setText(isReady ? "Готова" : String.format("Нагреется в %s", (isHistorical ? "~" : "") + fmt.print(readyTime)));
    }

    private void recreateRefreshTimer() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
        SaunaQueryTask saunaQueryTask = new SaunaQueryTask();
        mTimer.schedule(saunaQueryTask, 1000, 30000);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        StartSaunaTask t = new StartSaunaTask();
        t.execute();
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
        Switch mainSwitch = (Switch) findViewById(R.id.mainSwitch);
        if (mainSwitch != null) {
            mainSwitch.setOnCheckedChangeListener(this);
        }
        recreateRefreshTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTimer != null)
            mTimer.cancel();
    }

    public void onSetupClick(View v) {
        Toast.makeText(this, "No setup implemented", Toast.LENGTH_SHORT).show();
    }

    public void onStartClick(View v) {
        StartSaunaTask t = new StartSaunaTask();
        t.execute();
    }

    public void onSettingsClick(View v) {
        mTimer.cancel();
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialog dialog = SettingsDialog.newInstance(mSaunaSettings);
        dialog.show(fm, "settings");
    }
}

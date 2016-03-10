package ru.yulancer.sauna;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends FragmentActivity implements SettingsDialog.OnFragmentInteractionListener {

    private SaunaSettings mSaunaSettings = new SaunaSettings();

    //private IModbusActor mActor = new Modbus4jActor("192.168.1.77", 502);
    //private IModbusActor mActor = new Modbus4jActor("localhost", 502);
    //private IModbusActor mActor = new Modbus4jActor("10.0.2.2", 502);
    private IModbusActor mActor = new J2modActor("10.0.2.2", 502);

    @Override
    public void onSaveSettings(SaunaSettings saunaSettings) {
        mSaunaSettings = saunaSettings;
    }

    class RefreshValuesTask extends AsyncTask<Void, Void, SaunaInfo> {

        @Override
        protected SaunaInfo doInBackground(Void... params) {

            return mActor.GetSaunaInfo();
        }

        @Override
        protected void onPostExecute(SaunaInfo saunaInfo) {
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
            if (saunaInfo.exception == null) {
                t0.setText(String.format("%.2f", saunaInfo.SaunaCurrentTemp));
                t1.setText(String.format("%.2f", saunaInfo.BoilerCurrentTemp));
                t2.setText(String.format("%.2f", saunaInfo.RoomCurrentTemp));
                t3.setText(String.format("%.2f", saunaInfo.WaterPipeCurrentTemp));
                t4.setText(String.format("%.2f", saunaInfo.OutdoorCurrentTemp));
                t5.setText(String.format("%.2f", saunaInfo.SaunaSetpoint));
                t6.setText(String.format("%.2f", saunaInfo.BoilerSetpoint));
                t7.setText(String.format("%.2f", saunaInfo.RoomSetpoint));

                tvSaunaHeaterStatus.setText(saunaInfo.SaunaHeaterOn ? "I" : "O");
                tvBoilerHeaterStatus.setText(saunaInfo.BoilerHeaterOn ? "I" : "O");
                tvRoomHeaterStatus.setText(saunaInfo.RoomHeaterOn ? "I" : "O");
                tvDoorSauna.setText(saunaInfo.DoorSaunaOpen ? "открыта" : "закрыта");
                tvDoorShower.setText(saunaInfo.DoorShowerOpen ? "открыта" : "закрыта");
                tbSaunaOn.setChecked(saunaInfo.SaunaOn);
                tvException.setText("");
            } else {
                t0.setText("");
                t1.setText("");
                t2.setText("");
                t3.setText("");
                t4.setText("");
                t5.setText("");
                t6.setText("");
                t7.setText("");
                tvException.setText(saunaInfo.exception.getLocalizedMessage());
            }

            ProgressBar p = (ProgressBar) findViewById(R.id.progressBar);
            p.setVisibility(View.GONE);
            Button b = (Button) findViewById(R.id.btnTest);
            b.setEnabled(true);
        }

        @Override
        protected void onPreExecute() {
            Button b = (Button) findViewById(R.id.btnTest);
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
        //StartSaunaTask t = new StartSaunaTask();
        //t.execute();
    }
}

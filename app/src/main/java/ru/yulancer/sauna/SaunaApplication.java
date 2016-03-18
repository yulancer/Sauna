package ru.yulancer.sauna;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by matveev_yuri on 18.03.2016.
 */
public class SaunaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
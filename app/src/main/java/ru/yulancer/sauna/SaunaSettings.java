package ru.yulancer.sauna;

/**
 * Created by matveev_yuri on 09.03.2016.
 */
public class SaunaSettings {
    public float SaunaSetpoint;
    public float BoilerSetpoint;
    public float RoomSetpoint;

    public SaunaSettings(float saunaSetpoint, float boilerSetpoint, float roomSetpoint) {
        SaunaSetpoint = saunaSetpoint;
        BoilerSetpoint = boilerSetpoint;
        RoomSetpoint = roomSetpoint;
    }

    public SaunaSettings() {
        this(60, 40, 18);
    }
}

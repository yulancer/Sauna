package ru.yulancer.sauna;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public interface IModbusActor {
    public SaunaInfo GetSaunaInfo();
    public void SendSwitchSignal();
    public boolean SaveSettings(SaunaSettings saunaSettings);
}

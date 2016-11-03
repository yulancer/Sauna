package ru.yulancer.sauna;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public interface IModbusActor {
    SaunaInfo GetSaunaInfo();
    boolean SetDelayStart(long seconds);
    void SendSwitchSignal();
    boolean SaveSettings(SaunaSettings saunaSettings);
    void RebootController();
}


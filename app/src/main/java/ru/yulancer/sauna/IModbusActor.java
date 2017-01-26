package ru.yulancer.sauna;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public interface IModbusActor {
    SaunaInfo GetSaunaInfo();

    boolean SetDelayStart(long seconds);

    void SendSwitchSignal();

    void SendSwitchSignal(int commandCode);

    boolean SaveSettings(SaunaSettings saunaSettings);

    void RebootController();

    int SaunaHeaterCommand = 1;
    int BoilerHeaterCommand = 2;
    int RoomHeaterCommand = 3;
}


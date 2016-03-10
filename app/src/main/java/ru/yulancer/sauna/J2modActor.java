package ru.yulancer.sauna;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class J2modActor  implements IModbusActor {
    public String mHost;
    public int mPort;

    public J2modActor(String host, int port){
        mHost = host;
        mPort = port;
    }

    @Override
    public SaunaInfo GetSaunaInfo() {
        SaunaInfo saunaInfo = new SaunaInfo();
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(mHost);
        } catch (UnknownHostException e) {
            saunaInfo.exception = e;
            return saunaInfo;
        }

        TCPMasterConnection connection = new TCPMasterConnection(inetAddress);
        connection.setPort(mPort);

        try {
            connection.connect();
        } catch (Exception e) {
            saunaInfo.exception = e;
            return saunaInfo;
        }


        return saunaInfo;
    }

    @Override
    public void SendSwitchSignal() {

    }

    @Override
    public boolean SaveSettings(SaunaSettings saunaSettings) {
        return false;
    }
}

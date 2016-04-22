package ru.yulancer.sauna;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class J2modActor implements IModbusActor {
    public String mHost;
    public int mPort;

    public J2modActor(String host, int port) {
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
            e.printStackTrace();
            saunaInfo.exception = e;
            return saunaInfo;
        }

        TCPMasterConnection connection = new TCPMasterConnection(inetAddress);
        connection.setPort(mPort);
        int slaveId = 1;


        ModbusRequest request = new ReadMultipleRegistersRequest(0, 17);
        request.setUnitID(slaveId);
        request.setHeadless();

        ModbusResponse response = null;
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            saunaInfo.exception = e;
            return saunaInfo;
        }

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        try {
            transaction.execute();

        } catch (ModbusException e) {
            e.printStackTrace();
            saunaInfo.exception = e;
            return saunaInfo;
        }
        response = transaction.getResponse();
        if (response != null) {
            saunaInfo.SaunaCurrentTemp = response.getDataLength();
        }


        return saunaInfo;
    }

    @Override
    public boolean SetDelayStart(long seconds) {
        return false;
    }

    @Override
    public void SendSwitchSignal() {

    }

    @Override
    public boolean SaveSettings(SaunaSettings saunaSettings) {
        return false;
    }
}

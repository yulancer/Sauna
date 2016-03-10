package ru.yulancer.sauna;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.WriteCoilsRequest;
import com.serotonin.modbus4j.msg.WriteCoilsResponse;

import java.util.List;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class Modbus4jActor implements IModbusActor {

    public String mHost;
    public int mPort;

    public Modbus4jActor(String host, int port) {
        mHost = host;
        mPort = port;
    }

    @Override
    public SaunaInfo GetSaunaInfo() {

        SaunaInfo saunaInfo = new SaunaInfo();

        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(mHost);
        ipParameters.setPort(mPort);


        ModbusFactory modbusFactory = new ModbusFactory();
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, false);
        master.setTimeout(2000);

        BatchResults<Integer> results = null;
        BatchRead<Integer> batch = new BatchRead<Integer>();
        int slaveId = 1;

        batch.addLocator(0, BaseLocator.holdingRegister(slaveId, 0, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(1, BaseLocator.holdingRegister(slaveId, 2, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(2, BaseLocator.holdingRegister(slaveId, 4, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(3, BaseLocator.holdingRegister(slaveId, 6, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(4, BaseLocator.holdingRegister(slaveId, 8, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(5, BaseLocator.holdingRegister(slaveId, 10, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(6, BaseLocator.holdingRegister(slaveId, 12, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(7, BaseLocator.holdingRegister(slaveId, 14, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(8, BaseLocator.holdingRegister(slaveId, 16, DataType.TWO_BYTE_INT_UNSIGNED));

        try {
            master.init();
            results = master.send(batch);
        } catch (Exception e) {
            saunaInfo.exception = e;
        } finally {
            master.destroy();
        }
        if (results != null) {
            saunaInfo.SaunaCurrentTemp = results.getFloatValue(0);
            saunaInfo.BoilerCurrentTemp = results.getFloatValue(1);
            saunaInfo.RoomCurrentTemp = results.getFloatValue(2);
            saunaInfo.WaterPipeCurrentTemp = results.getFloatValue(3);
            saunaInfo.OutdoorCurrentTemp = results.getFloatValue(4);
            saunaInfo.SaunaSetpoint = results.getFloatValue(5);
            saunaInfo.BoilerSetpoint = results.getFloatValue(6);
            saunaInfo.RoomSetpoint = results.getFloatValue(7);

            int flags = results.getIntValue(8);
            saunaInfo.SaunaHeaterOn = (flags & 2) == 2;
            saunaInfo.BoilerHeaterOn = (flags & 4) == 4;
            saunaInfo.RoomHeaterOn = (flags & 8) == 8;
            saunaInfo.DoorSaunaOpen = (flags & 16) == 16;
            saunaInfo.DoorShowerOpen = (flags & 32) == 32;
            saunaInfo.SaunaOn = (flags & 64) == 64;
        }
        return saunaInfo;
    }

    @Override
    public void SendSwitchSignal() {
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(mHost);
        ipParameters.setPort(mPort);

        ModbusFactory modbusFactory = new ModbusFactory();
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, false);
        master.setTimeout(2000);
        int slaveId = 1;
        if (master.testSlaveNode(slaveId))
            try {
                WriteCoilsRequest request = new WriteCoilsRequest(slaveId, (16 * 16), new boolean[]{true});
                WriteCoilsResponse response = (WriteCoilsResponse) master.send(request);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        master.destroy();
    }

    @Override
    public boolean SaveSettings(SaunaSettings saunaSettings) {
        return true;
    }
}
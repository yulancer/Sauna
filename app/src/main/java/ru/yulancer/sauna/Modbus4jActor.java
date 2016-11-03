package ru.yulancer.sauna;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteCoilResponse;
import com.serotonin.modbus4j.msg.WriteCoilsRequest;
import com.serotonin.modbus4j.msg.WriteCoilsResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;

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

    private ModbusMaster CreateMaster(){

        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(mHost);
        ipParameters.setPort(mPort);


        ModbusFactory modbusFactory = new ModbusFactory();
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, false);
        master.setTimeout(600);
        master.setRetries(10);
        return  master;
    }
    @Override
    public SaunaInfo GetSaunaInfo() {

        SaunaInfo saunaInfo = new SaunaInfo();

        ModbusMaster master = CreateMaster();

        BatchResults<Integer> results = null;
        BatchRead<Integer> batch = new BatchRead<>();
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
        batch.addLocator(9, BaseLocator.holdingRegister(slaveId, 18, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        batch.addLocator(10, BaseLocator.holdingRegister(slaveId, 20, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        batch.addLocator(11, BaseLocator.holdingRegister(slaveId, 22, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        batch.addLocator(12, BaseLocator.holdingRegister(slaveId, 24, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        batch.addLocator(13, BaseLocator.holdingRegister(slaveId, 26, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        batch.addLocator(14, BaseLocator.holdingRegister(slaveId, 28, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED));
        batch.addLocator(15, BaseLocator.holdingRegister(slaveId, 30, DataType.FOUR_BYTE_FLOAT_SWAPPED));

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
            saunaInfo.SaunaReady = (flags & 128) == 128;
            saunaInfo.BoilerReady = (flags & 256) == 256;
            saunaInfo.RoomReady = (flags & 512) == 512;
            saunaInfo.SaunaRemainHistorical = (flags & 1024) == 1024;
            saunaInfo.BoilerRemainHistorical = (flags & 2048) == 2048;
            saunaInfo.RoomRemainHistorical = (flags & 4096) == 4096;
            saunaInfo.WarningSaunaStartedWithDoorOpen = (flags & 8192) == 8192;

            saunaInfo.SaunaSecondsRemain = results.getLongValue(9);
            saunaInfo.BoilerSecondsRemain = results.getLongValue(10);
            saunaInfo.RoomSecondsRemain = results.getLongValue(11);
            saunaInfo.AllSecondsRemain = results.getLongValue(12);
            saunaInfo.SecondsBeforeStartRequested = results.getLongValue(13);
            saunaInfo.SecondsBeforeStartRemain = results.getLongValue(14);

            float waterPressure = results.getFloatValue(15);
            // может ошибочно писать большое давление при наличии разрежения в трубе
            saunaInfo.WaterPressure = Math.abs(waterPressure) > 10 ? 0 : waterPressure;
        }
        return saunaInfo;
    }

    @Override
    public boolean SetDelayStart(long seconds) {
        ModbusMaster master = CreateMaster();

        int slaveId = 1;
        try {
            NumericLocator locator = (NumericLocator) BaseLocator.holdingRegister(slaveId, 0, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED);

            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, 26, locator.valueToShorts(seconds));
            WriteRegistersResponse response = (WriteRegistersResponse) master.send(request);

        } catch (ModbusTransportException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void SendSwitchSignal() {
        ModbusMaster master = CreateMaster();

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

        ModbusMaster master = CreateMaster();

        int slaveId = 1;
        try {
            NumericLocator locator = (NumericLocator) BaseLocator.holdingRegister(slaveId, 0, DataType.FOUR_BYTE_FLOAT_SWAPPED);

            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, 10, locator.valueToShorts(saunaSettings.SaunaSetpoint));
            WriteRegistersResponse response = (WriteRegistersResponse) master.send(request);

            request = new WriteRegistersRequest(slaveId, 12, locator.valueToShorts(saunaSettings.BoilerSetpoint));
            response = (WriteRegistersResponse) master.send(request);

            request = new WriteRegistersRequest(slaveId, 14, locator.valueToShorts(saunaSettings.RoomSetpoint));
            response = (WriteRegistersResponse) master.send(request);
        } catch (ModbusTransportException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void RebootController() {
        ModbusMaster master = CreateMaster();

        int slaveId = 1;
        if (master.testSlaveNode(slaveId))
            try {
                int offset = (16 * 16) + 15;
                WriteCoilRequest request = new WriteCoilRequest(slaveId, offset, true);
                WriteCoilResponse response = (WriteCoilResponse) master.send(request);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        master.destroy();
    }
}

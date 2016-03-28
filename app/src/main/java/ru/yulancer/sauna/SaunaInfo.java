package ru.yulancer.sauna;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class SaunaInfo implements Parcelable {
    public Exception exception;
    public float SaunaCurrentTemp;
    public float BoilerCurrentTemp;
    public float RoomCurrentTemp;
    public float WaterPipeCurrentTemp;
    public float OutdoorCurrentTemp;
    public float SaunaSetpoint;
    public float BoilerSetpoint;
    public float RoomSetpoint;
    public boolean SaunaHeaterOn;
    public boolean BoilerHeaterOn;
    public boolean RoomHeaterOn;
    public boolean DoorSaunaOpen;
    public boolean DoorShowerOpen;
    public boolean SaunaOn;
    public boolean SaunaReady;
    public boolean BoilerReady;
    public boolean RoomReady;

    public long SaunaSecondsRemain;
    public long BoilerSecondsRemain;
    public long RoomSecondsRemain;
    public long AllSecondsRemain;
    public boolean SaunaRemainHistorical;
    public boolean BoilerRemainHistorical;
    public boolean RoomRemainHistorical;

    public boolean WarningSaunaStartedWithDoorOpen;

    public SaunaInfo() {

    }

    protected SaunaInfo(Parcel in) {
        SaunaCurrentTemp = in.readFloat();
        BoilerCurrentTemp = in.readFloat();
        RoomCurrentTemp = in.readFloat();
        WaterPipeCurrentTemp = in.readFloat();
        OutdoorCurrentTemp = in.readFloat();
        SaunaSetpoint = in.readFloat();
        BoilerSetpoint = in.readFloat();
        RoomSetpoint = in.readFloat();
        SaunaHeaterOn = in.readByte() != 0;
        BoilerHeaterOn = in.readByte() != 0;
        RoomHeaterOn = in.readByte() != 0;
        DoorSaunaOpen = in.readByte() != 0;
        DoorShowerOpen = in.readByte() != 0;
        SaunaOn = in.readByte() != 0;
        SaunaReady = in.readByte() != 0;
        BoilerReady = in.readByte() != 0;
        RoomReady = in.readByte() != 0;
        SaunaSecondsRemain = in.readLong();
        BoilerSecondsRemain = in.readLong();
        RoomSecondsRemain = in.readLong();
        AllSecondsRemain = in.readLong();
        SaunaRemainHistorical =  in.readByte() != 0;
        BoilerRemainHistorical =  in.readByte() != 0;
        RoomRemainHistorical =  in.readByte() != 0;
        WarningSaunaStartedWithDoorOpen =  in.readByte() != 0;
    }

    public static final Creator<SaunaInfo> CREATOR = new Creator<SaunaInfo>() {
        @Override
        public SaunaInfo createFromParcel(Parcel in) {
            return new SaunaInfo(in);
        }

        @Override
        public SaunaInfo[] newArray(int size) {
            return new SaunaInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(SaunaCurrentTemp);
        dest.writeFloat(BoilerCurrentTemp);
        dest.writeFloat(RoomCurrentTemp);
        dest.writeFloat(WaterPipeCurrentTemp);
        dest.writeFloat(OutdoorCurrentTemp);
        dest.writeFloat(SaunaSetpoint);
        dest.writeFloat(BoilerSetpoint);
        dest.writeFloat(RoomSetpoint);
        dest.writeByte((byte) (SaunaHeaterOn ? 1 : 0));
        dest.writeByte((byte) (BoilerHeaterOn ? 1 : 0));
        dest.writeByte((byte) (RoomHeaterOn ? 1 : 0));
        dest.writeByte((byte) (DoorSaunaOpen ? 1 : 0));
        dest.writeByte((byte) (DoorShowerOpen ? 1 : 0));
        dest.writeByte((byte) (SaunaOn ? 1 : 0));
        dest.writeByte((byte) (SaunaReady ? 1 : 0));
        dest.writeByte((byte) (BoilerReady ? 1 : 0));
        dest.writeByte((byte) (RoomReady ? 1 : 0));
        dest.writeLong(SaunaSecondsRemain);
        dest.writeLong(BoilerSecondsRemain);
        dest.writeLong(RoomSecondsRemain);
        dest.writeLong(AllSecondsRemain);
        dest.writeByte((byte) (SaunaRemainHistorical ? 1 : 0));
        dest.writeByte((byte) (BoilerRemainHistorical ? 1 : 0));
        dest.writeByte((byte) (RoomRemainHistorical ? 1 : 0));
        dest.writeByte((byte) (WarningSaunaStartedWithDoorOpen ? 1 : 0));
    }
}

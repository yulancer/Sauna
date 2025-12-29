package ru.yulancer.sauna;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matveev_yuri on 03.11.2016.
 */

public class SaunaSetupData implements Parcelable {
    public boolean DoReboot;
    public String ModbusHost;

    protected SaunaSetupData(Parcel in) {
        DoReboot = in.readByte() != 0;
        ModbusHost = in.readString();
    }

    public static final Creator<SaunaSetupData> CREATOR = new Creator<SaunaSetupData>() {
        @Override
        public SaunaSetupData createFromParcel(Parcel in) {
            return new SaunaSetupData(in);
        }

        @Override
        public SaunaSetupData[] newArray(int size) {
            return new SaunaSetupData[size];
        }
    };

    public SaunaSetupData() {
        ModbusHost = "192.168.1.77";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (DoReboot ? 1 : 0));
        dest.writeString(ModbusHost);
    }
}

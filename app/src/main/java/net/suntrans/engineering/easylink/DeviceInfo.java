package net.suntrans.engineering.easylink;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Looney on 2018/3/2.
 * Des:
 */

public class DeviceInfo {


    /**
     * Name : EMW3165#10E09F
     * IP : 192.168.0.251
     * Port : 8080
     * MAC : D0:BA:E4:10:E0:9F
     * Firmware Rev : ATV1.1.2@EMW3165
     * MICO OS Rev : 31620002.042
     * Model : EMW3165
     * Protocol : com.mxchip.at
     * Manufacturer : MXCHIP Inc.
     * Seed : 1156
     */

    public String Name;
    public String IP;
    public int Port;
    public String MAC;
    @SerializedName("Firmware Rev")
    public String FirmwareRev; // FIXME check this code
    @SerializedName("MICO OS Rev")
    public String MICOOSRev; // FIXME check this code
    public String Model;
    public String Protocol;
    public String Manufacturer;
    public String Seed;
}

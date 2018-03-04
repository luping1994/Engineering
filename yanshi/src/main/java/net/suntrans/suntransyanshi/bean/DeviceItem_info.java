package net.suntrans.suntransyanshi.bean;

/**
 * Created by Administrator on 2017/4/11.
 */

public class DeviceItem_info {

    public static final String ewm="EWM";
    public static final String hx="HX";
    private String Name;
    private String IP;
    private String Port;
    private String Vendor;
    private String Firmware;
    private String Socket1_Type;
    private String Socket1_Port;
    private String Socket2_Type;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String port) {
        Port = port;
    }

    public String getVendor() {
        return Vendor;
    }

    public void setVendor(String vendor) {
        Vendor = vendor;
    }

    public String getFirmware() {
        return Firmware;
    }

    public void setFirmware(String firmware) {
        Firmware = firmware;
    }

    public String getSocket1_Type() {
        return Socket1_Type;
    }

    public void setSocket1_Type(String socket1_Type) {
        Socket1_Type = socket1_Type;
    }

    public String getSocket1_Port() {
        return Socket1_Port;
    }

    public void setSocket1_Port(String socket1_Port) {
        Socket1_Port = socket1_Port;
    }

    public String getSocket2_Type() {
        return Socket2_Type;
    }

    public void setSocket2_Type(String socket2_Type) {
        Socket2_Type = socket2_Type;
    }

    public String getSocket2_Port() {
        return Socket2_Port;
    }

    public void setSocket2_Port(String socket2_Port) {
        Socket2_Port = socket2_Port;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getProtocol() {
        return Protocol;
    }

    public void setProtocol(String protocol) {
        Protocol = protocol;
    }

    public String getHardware() {
        return Hardware;
    }

    public void setHardware(String hardware) {
        Hardware = hardware;
    }

    public String getSeed() {
        return Seed;
    }

    public void setSeed(String seed) {
        Seed = seed;
    }

    private String Socket2_Port;
    private String MAC;
    private String Protocol;
    private String Hardware;
    private String Seed;


    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DeviceItem_info{" +
                "Name='" + Name + '\'' +
                ", IP='" + IP + '\'' +
                ", Port='" + Port + '\'' +
                ", Vendor='" + Vendor + '\'' +
                ", Firmware='" + Firmware + '\'' +
                ", Socket1_Type='" + Socket1_Type + '\'' +
                ", Socket1_Port='" + Socket1_Port + '\'' +
                ", Socket2_Type='" + Socket2_Type + '\'' +
                ", Socket2_Port='" + Socket2_Port + '\'' +
                ", MAC='" + MAC + '\'' +
                ", Protocol='" + Protocol + '\'' +
                ", Hardware='" + Hardware + '\'' +
                ", Seed='" + Seed + '\'' +
                ", type=" + type +
                '}';
    }
}

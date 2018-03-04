package net.suntrans.suntransyanshi.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.suntrans.suntransyanshi.utils.Converts;

import static net.suntrans.suntransyanshi.utils.Converts.GetCRC;
import static net.suntrans.suntransyanshi.utils.Converts.HexString2Bytes;


/**
 * Created by Looney on 2017/2/28.
 */

public class SwitchItem implements Parcelable {
    private String name;
    private String RSaddr;
    private String channel;
    private String state;
    private int imgId;
    private int opImageId;
    private int closeImageId;

    private  String zhilianip;
    private String zhilianport;
    private String waiwangip;
    private String waiwangport;
    private String bendiip;


    private String openCmd;
    private String closeCmd;

    private String openCmdNew;
    private String closeCmdNew;

    public String getZhilianip() {
        return zhilianip;
    }

    public void setZhilianip(String zhilianip) {
        this.zhilianip = zhilianip;
    }

    public String getZhilianport() {
        return zhilianport;
    }

    public void setZhilianport(String zhilianport) {
        this.zhilianport = zhilianport;
    }

    public String getWaiwangip() {
        return waiwangip;
    }

    public void setWaiwangip(String waiwangip) {
        this.waiwangip = waiwangip;
    }

    public String getWaiwangport() {
        return waiwangport;
    }

    public void setWaiwangport(String waiwangport) {
        this.waiwangport = waiwangport;
    }

    public String getBendiip() {
        return bendiip;
    }

    public void setBendiip(String bendiip) {
        this.bendiip = bendiip;
    }

    public String getBendiport() {
        return bendiport;
    }

    public void setBendiport(String bendiport) {
        this.bendiport = bendiport;
    }

    private String bendiport;
    public int getOpImageId() {
        return opImageId;
    }

    public void setOpImageId(int opImageId) {
        this.opImageId = opImageId;
    }

    public int getCloseImageId() {
        return closeImageId;
    }

    public void setCloseImageId(int closeImageId) {
        this.closeImageId = closeImageId;
    }



    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }


    public String getOpenCmd() {
        return openCmd;
    }

    public String getCloseCmd() {
        return closeCmd;
    }

    public void setOpenCmd() {
        String order = "";
        if (channel.equals("10")){
            order  = "aa68"+RSaddr+"06 030"+"a"+"0001";
        }else {
            order  = "aa68"+RSaddr+"06 030"+channel+"0001";
        }
        order = getOrder(order);
        this.openCmd = order;
    }



    public void setCloseCmd() {
        String order = "";
        if (channel.equals("10")){
          order  = "aa68"+RSaddr+"06 030"+"a"+"0000";
        }else {
            order  = "aa68"+RSaddr+"06 030"+channel+"0000";
        }
        order = getOrder(order);
        this.closeCmd = order;
    }



    public void setOpenCmdNew() {
        String order = "";
        int channeOrder = 0x01;
        String s = getChannelString(channeOrder<<(Integer.valueOf(channel)-1));
        order = "AB 68 41 00 " + getRSaddr()+ "03 7b 00"+s+s;
        order =getSwitchOrder(order);
//        String regex ="(.{2})";
//        order = order.replaceAll(regex, "$1 ");
        this.openCmdNew =  order;
    }

    public String getOpenCmdNew() {
        return openCmdNew;
    }

    public String getCloseCmdNew() {
        return closeCmdNew;
    }

    public void setCloseCmdNew() {
        String order = "";
        int channeOrder = 0x01;
        String s = getChannelString(channeOrder<<(Integer.valueOf(channel)-1));
        order = "AB 68 43 00 " + getRSaddr() + "03 7b 00"+s+"0000";
        order =getSwitchOrder(order);

        this.closeCmdNew =  order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRSaddr() {
        return RSaddr;
    }

    public void setRSaddr(String RSaddr) {
        this.RSaddr = RSaddr;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @NonNull
    private String getOrder(String order) {
        order = order.replace(" ", "");
        byte[] bytes = HexString2Bytes(order);
        String crc = GetCRC(bytes, 2, bytes.length);
        order = order + crc + "0d0a";
        return order;
    }


    public SwitchItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.RSaddr);
        dest.writeString(this.channel);
        dest.writeString(this.state);
        dest.writeInt(this.imgId);
        dest.writeInt(this.opImageId);
        dest.writeInt(this.closeImageId);
        dest.writeString(this.zhilianip);
        dest.writeString(this.zhilianport);
        dest.writeString(this.waiwangip);
        dest.writeString(this.waiwangport);
        dest.writeString(this.bendiip);
        dest.writeString(this.openCmd);
        dest.writeString(this.closeCmd);
        dest.writeString(this.openCmdNew);
        dest.writeString(this.closeCmdNew);
        dest.writeString(this.bendiport);
    }

    protected SwitchItem(Parcel in) {
        this.name = in.readString();
        this.RSaddr = in.readString();
        this.channel = in.readString();
        this.state = in.readString();
        this.imgId = in.readInt();
        this.opImageId = in.readInt();
        this.closeImageId = in.readInt();
        this.zhilianip = in.readString();
        this.zhilianport = in.readString();
        this.waiwangip = in.readString();
        this.waiwangport = in.readString();
        this.bendiip = in.readString();
        this.openCmd = in.readString();
        this.closeCmd = in.readString();
        this.openCmdNew = in.readString();
        this.closeCmdNew = in.readString();
        this.bendiport = in.readString();
    }

    public static final Creator<SwitchItem> CREATOR = new Creator<SwitchItem>() {
        @Override
        public SwitchItem createFromParcel(Parcel source) {
            return new SwitchItem(source);
        }

        @Override
        public SwitchItem[] newArray(int size) {
            return new SwitchItem[size];
        }
    };

    private  String getSwitchOrder(String order) {
        byte[] bt = HexString2Bytes(order.replace(" ", ""));
        order = order +GetCRC(bt, 2, bt.length) + "0d0a";
        order = order.replace(" ", "");
        return order;
    }

    private  String getChannelString(int channeOrder) {
        String s = Integer.toHexString(channeOrder);
        StringBuilder sb = new StringBuilder();
        if (s.length() < 2) {
            sb.append("0");
        }
        sb.append(s);
        s=sb.toString()+"00";
        return s;
    }
}

package net.suntrans.suntransyanshi.bean;

/**
 * Created by Administrator on 2017/4/11.
 */

public class EWM_COMMAND {
    private EWM_COMMAND() {
    }

    public static final int PORT = 8089;
    public static final String EMS_CMD_GETCONFIT = "02 00 0A 00 00 00 F3 FF FF FF";
    public static final String EMS_CMD_GET_MAC_ADDR = "0C 00 0A 00 00 00 E9 FF FF FF";
}

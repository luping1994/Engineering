package net.suntrans.engineering;

import android.support.annotation.NonNull;

import net.suntrans.engineering.utils.Converts;
import net.suntrans.engineering.utils.LogUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        byte[] bits = {(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80};     //从1到8只有一位是1，用于按位与计算，获取某一位的值

        String s = "AA 68 41 00 DF 0B 00 00 04 32 00 3C 03 04 00 A9 DA 0D 0A";
        //总开关状态
        s = s.replace(" ", "");
        String s1 = s.substring(22, 26);
        System.out.println(s1);
        s1 = s1.substring(2, 4) + s1.substring(0, 2);
        System.out.println(s1);
        String[] states = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};   //10个通道的状态，state[0]对应1通道
        byte[] a = Converts.HexString2Bytes(s1);
        for (int i = 0; i < 10; i++) {
            if (i < 8) {
                states[i] = ((a[1] & bits[i]) == bits[i]) ? "1" : "0";
            } else {
//                System.out.println("i-8="+(i-8));
                states[i] = ((a[0] & bits[i - 8]) == bits[i - 8]) ? "1" : "0";
            }
            System.out.println("通道" + (i + 1) + ":" + states[i] + ",");
//            System.out.println(states[i]+",");
        }

    }


    @Test
    public void testHostA() {
//        AB 68 41 00 00 00 00 00 03 09 00 0D 31 39 32 2E 31 36 38 2E 31 2E 31 39 31 00 00 23 8D F4 4E 0D 0A

        //AB 68 41 00 00 00 00 00 03 09 00 0D 31 39 32 2E 31 36 38 2E 31 39 31 2E 31 00 00 23 8D B7 83 0D 0A

        //AB 68 41 00 00 00 00 00 03 09 00 0D 49 57 50 46 49 54 56 46 49 57 49 46 49 00 00 23 8D 76 96 0D 0A
        String ipHex = "";
        String portHex = "";
        String ipLength = "";
        String ip = "192.168.191.1";
        portHex = Integer.toHexString(9101);

        portHex = getStringFormat(portHex, 8);
        ipHex = stringToAscii(ip);

        System.out.println("ipHex:" + ipHex);

        ipLength = getStringFormat(Integer.toHexString(ip.length()), 2);

//        System.out.println("ip:" + ipHex);
//        System.out.println("port:" + portHex);
//        System.out.println("ipLength:" + ipLength);

//        String order = "AB 68 41 00 00 00 00 00 03";
//        order = order + "09 00";//09 00  socket1   // 0a 00  socket2
//        order += ipLength;//域名长度
//        order += ipHex;
//        order += portHex;
//
//        order = order.replace(" ", "");
//
//        System.out.println(order.substring(4, order.length()));
//        byte[] bytes = Converts.HexString2Bytes(order.substring(4, order.length()));
//        String crc = Converts.GetCRC(bytes, 0, bytes.length);
//
//        order = order + crc + "0d0a";
//
//        order = order.toUpperCase();
//
//
//        System.out.println(order);
//        String  ipS = "31 39 32 2E 31 36 38 2E 31 2E 31 39 31";
//        ipS = ipS.toLowerCase();

    }

    @NonNull
    private String getStringFormat(String str, int targetLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetLength - str.length(); i++) {
            sb.append("0");
        }
        sb.append(str);
        return sb.toString();
    }
    //AB 68 41 00 DF 0B 00 00 03 7B 00 01 00 01 00 2F BF 0D 0A
    //AB 68 43 00 00 00 00 00 03 7b 00 01 00 01 00
    //AB 68 41 00 DF 0B 00 00 03 7B 00 80 00 80 00 67 D3 0D 0A

    //AB 68 43 00 00 00 00 00 03 7b 00 80 00 80 00

    //AB 68 41 00 DF 0B 00 00 03 7B 00 00 02 00 02 0F D2 0D 0A
    //AB 68 43 00 00 00 00 00 03 7b 00 20 00 02 00 00
    public void setOpenCmd(int channel) {
        String order = "";
        int channeOrder = 0x0001;
        //0000 0000 0000 0000
        int i = channeOrder << (Integer.valueOf(channel) - 1);
        String s = "";
        s = getChannelString(i);
        order = "AB 68 43 00 " + "00 00 00 00 " + "03 7b 00" + s + s;
        order = getSwitchOrder(order);
        System.out.println("通道" + channel + ":" + order);

    }

    private String getChannelString(int channeOrder) {
        String s = Integer.toHexString(channeOrder);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4 - s.length(); i++) {
            sb.append("0");
        }
        sb.append(s);
        s = sb.toString();
        s = s.substring(2, 4) + s.substring(0, 2);
        return s;
    }

    private String getSwitchOrder(String order) {
        byte[] bt = Converts.HexString2Bytes(order.replace(" ", ""));
        order = order + Converts.GetCRC(bt, 2, bt.length) + "0d0a";
        order = order.replace(" ", "");
        return order;
    }

    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    /**
     * 字符串转换为Ascii
     *
     * @param value
     * @return
     */
    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();

    }

    @Test
    public void test() {
//         String closeAllOrder = "41 00 00 00 00 00 03 7B 00 FF 03 FF 03";
        String closeAllOrder = "41 00 " +
                "00 00 00 00 " +
                "01 " +
                "42 00" +
                "00 00 00 00" +
                "43 00" +
                "00 00 00 00" +
                "44 00" +
                "00 00 00 00";
        closeAllOrder = closeAllOrder.replaceAll(" ", "");
        byte[] bytes = Converts.HexString2Bytes(closeAllOrder);
        String crc = Converts.GetCRC(bytes, 0, bytes.length);
        System.out.println(crc);
    }

    @Test
    public void parseTenParam() {
        String content = "aa684100 df0b 0000 02 4200 3c00c800 4300 32000000 4400 2c010000 f942 0d0a";
        content = content.replace(" ", "");
        System.out.println(content.substring(16, 18));

        if (!content.substring(0, 8).equals("aa684100")) {
            return;
        }
        if (!content.substring(16, 18).equals("02")) {
            return;
        }

        String guoliuDan = content.substring(22, 26);
        String guoliuzong = content.substring(26, 30);
        String qianya = content.substring(34, 38);
        String guoya = content.substring(46, 50);

        guoliuDan = reserverHexString(guoliuDan);
        guoliuzong = reserverHexString(guoliuzong);
        qianya = reserverHexString(qianya);
        guoya = reserverHexString(guoya);

        guoliuDan = Integer.parseInt(guoliuDan, 16) + "";
        guoliuzong = Integer.parseInt(guoliuzong, 16) + "";
        qianya = Integer.parseInt(qianya, 16) + "";
        guoya = Integer.parseInt(guoya, 16) + "";


        System.out.println("单通道过流：" + guoliuDan);
        System.out.println("总通道过流:" + guoliuzong);
        System.out.println("欠压:" + qianya);
        System.out.println("过压:" + guoya);

        String s = Converts.reverse32HexString("400b0000");

        System.out.println("reverse:"+Integer.parseInt(s,16));

    }

    public String reserverHexString(String hexString) {
        if (hexString.length() != 4) {
            return null;
        }
        return hexString.substring(2, 4) + hexString.substring(0, 2);
    }

    @Test
    public void test2(){
        //31 39 32 2E 31 36 38 2E 31 2E 31
        //31 39 32 2e 31 36 38 2e 31 2e 31
        //49 57 50 46 49 54 56 46 49 46 49
        String ip = " 3139322e3136382e3139312e31";
        String bytes = Converts.HexString2Int(ip);
        System.out.println(bytes);
        String s = Converts.asciiToString(bytes);
        System.out.println(s);
    }
}
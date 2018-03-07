package net.suntrans.engineering;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.suntrans.engineering.utils.Converts;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("net.suntrans.engineering", appContext.getPackageName());

//        AA 68 41 00 DF 0B 00 00 05 32 00  11 00  10 00  4F 76  0D 0A
        setOpenCmd(10);
    }


    public void setOpenCmd(int channel) {
        String order = "";
        int channeOrder = 0x01;
        String s="";
        if (channel<=8){
            s  = getChannelString(channeOrder<<(Integer.valueOf(channel)-1));
        }
        order = "AB 68 43 00 " + "0000 0000 " + "03 7b 00"+s+s;
        order =getSwitchOrder(order);
        System.out.println("通道"+channel+":"+order);
    }
    private String getChannelString(int channeOrder) {
        String s = Integer.toHexString(channeOrder);
        StringBuilder sb = new StringBuilder();
        if (s.length() < 2) {
            sb.append("0");
        }
        sb.append(s);
        s=sb.toString()+"00";
        return s;
    }

    private String getSwitchOrder(String order) {
        byte[] bt = Converts.HexString2Bytes(order.replace(" ", ""));
        order = order + Converts.GetCRC(bt, 2, bt.length) + "0d0a";
        order = order.replace(" ", "");
        return order;
    }
    private byte[] bits = {(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80,(byte)0x10};     //从1到8只有一位是1，用于按位与计算，获取某一位的值

    private void parseSwitchData(String s) {
        //aa 68 43 00 01 00 00 00 02 9700 3f000000 cf27 0d0a  开关总状态
        //aa 68 43 00 27 00 00 00 02 9700 00000000 767b 0d0a
        //AA 68 43 00 01 00 00 00 04 D200 04000400 A856 0D0A  单个
        //aa 68 41 00 dd 0b 00 00 02 3200 bf030000 ef26 0d0a
        if (s.length()<16)
            return;
//        if (((Stslc6_control_activity2)getActivity()).flag.equals("bendi")){
//
//        }else {
//            if (s.substring(0,16).equals("aa684300"+addrs.get(0))){
//                return;
//            }
//        }
//aa68 4100 1a02 0000 0232 00 00000000 0caa 0d0a

        //AA 68 41 00 DF 0B 00 00 05 32 00  01 00  01 00  47 E6  0D 0A
        //AA 68 41 00 DF 0B 00 00 05 32 00  11 00  10 00  4F 76  0D 0A
        if (!s.substring(4,8).equals("4100")){
            return;
        }
        if (s.substring(16, 18).equals("02") && s.substring(18, 22).equals("3200")) {
            //总开关状态
            String s1 = s.substring(22, 24);
            String[] states = {"0", "0", "0", "0", "0", "0","0","0","0","0"};   //10个通道的状态，state[0]对应1通道
            byte[] a = Converts.HexString2Bytes(s1);
            for (int i = 0; i < 8; i++) {
                states[i] = ((a[0] & bits[i]) == bits[i]) ? "1" : "0";
            }

        }else if (s.substring(16, 18).equals("05") && s.substring(18, 22).equals("d200")) {
            System.out.println("单个通道发生状态改变了");
            //     aa68 4300 01000000 04 d200 0100 0100 abca 0d0a
            //     aa68 4300 27000000 05 9700 0100 0100 00d7 0d0a
            String s2 = s.substring(22, 24);
            byte[] a = Converts.HexString2Bytes(s2);
            int channel =-1;
            int state;
            for (int i=0;i<bits.length;i++){
                if ((bits[i]&a[0])==bits[i]){
                    channel = i+1;
                }
            }
            System.out.println("通道号="+channel);
            String s3 = s.substring(26,28);
            state = s3.equals(s2)?1:0;
            System.out.println("状态="+state);


        }
    }
}

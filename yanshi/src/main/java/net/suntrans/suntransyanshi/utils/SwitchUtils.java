package net.suntrans.suntransyanshi.utils;


import net.suntrans.suntransyanshi.bean.SwitchItem;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Created by Looney on 2017/2/28.
 */

public class SwitchUtils {
    private static byte[] bits = {(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80};     //从1到8只有一位是1，用于按位与计算，获取某一位的值

    public static void parseSwitchState(List<SwitchItem> datas, String s) {
       if (!s.substring(0,4).equals("aa69")){
           return;
       }
        byte a[] = Converts.HexString2Bytes(s);
        String return_addr = s.substring(4, 12);   //返回数据的开关地址
        List<String> addrs = new ArrayList<>();
        for (SwitchItem item :
                datas) {
            if (!addrs.contains(item.getRSaddr())) {
                addrs.add(item.getRSaddr());
            }
        }

        if (!addrs.contains(return_addr)) {
            return;
        }
        if (s.substring(12, 14).equals("03"))   //如果是读寄存器状态，解析出开关状态
        {
            if (s.substring(14, 16).equals("0e") || s.substring(14, 16).equals("07")) {
                String[] states = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};   //十个通道的状态，state[0]对应1通道
                for (int i = 0; i < 8; i++)   //先获取前八位的开关状态
                {
                    states[i] = ((a[9] & bits[i]) == bits[i]) ? "1" : "0";   //1-8通道

                }
                for (int i = 0; i < 2; i++) {
                    states[i + 8] = ((a[8] & bits[i]) == bits[i]) ? "1" : "0";  //9、10通道

                }

                for (int i = 0; i < datas.size(); i++) {//更新状态到集合中
                    if (datas.get(i).getRSaddr().equals(return_addr)) {
                        int channel = Integer.valueOf(datas.get(i).getChannel());
                        if (channel != 0) {
                            datas.get(i).setState(states[channel - 1]);
                        }
                    }


                }
//                errorLayoout.setVisibility(View.INVISIBLE);
//                progressbar.setVisibility(View.INVISIBLE);
//                refresh.setVisibility(View.VISIBLE);
            }
        } else if (s.substring(12, 14).equals("06"))   //单个通道状态发生改变
        {
            //aa69 0001 0001 06 0301 000013690d0a
            int k = 0;         //k是通道号
            int state = Integer.valueOf(s.substring(21, 22));  //开关状态，1代表打开，0代表关闭
            if (s.substring(17, 18).equals("a"))
                k = 10;
            else
                k = Integer.valueOf(s.substring(17, 18));   //通道号,int型
            if (k == 0)                                          //如果通道号为0，则是总开关
            {
                if (state == 0) {
                    for (int i = 0; i < datas.size(); i++) {//更新状态到集合中
                        if (datas.get(i).getRSaddr().equals(return_addr))
                            datas.get(i).setState("0");
                    }
                }
            } else     //如果通道号不为0，则更改data中的状态，并更新
            {
                LogUtil.i("单个通道发生改变==>position=" + k + "," + state);
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getRSaddr().equals(return_addr)){
                        if (datas.get(i).getChannel().equals(valueOf(k))) {
                            datas.get(i).setState(state == 1 ? "1" : "0");
                        }
                    }
                }
            }
        }
    }

    public static void parseSwitchStateNo_addr(List<SwitchItem> datas, String s) {
        if (!s.substring(0, 4).equals("aa69")) {
            return;
        }
        byte a[] = Converts.HexString2Bytes(s);
        String return_addr = s.substring(4, 12);   //返回数据的开关地址
        List<String> addrs = new ArrayList<>();
        for (SwitchItem item :
                datas) {
            if (!addrs.contains(item.getRSaddr())) {
                addrs.add(item.getRSaddr());
            }
        }

//        if (!addrs.contains(return_addr)) {
//            return;
//        }
        if (s.substring(12, 14).equals("03"))   //如果是读寄存器状态，解析出开关状态
        {
            if (s.substring(14, 16).equals("0e") || s.substring(14, 16).equals("07")) {
                String[] states = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};   //十个通道的状态，state[0]对应1通道
                for (int i = 0; i < 8; i++)   //先获取前八位的开关状态
                {
                    states[i] = ((a[9] & bits[i]) == bits[i]) ? "1" : "0";   //1-8通道

                }
                for (int i = 0; i < 2; i++) {
                    states[i + 8] = ((a[8] & bits[i]) == bits[i]) ? "1" : "0";  //9、10通道

                }

                for (int i = 0; i < datas.size(); i++) {//更新状态到集合中
//                    if (datas.get(i).getRSaddr().equals(return_addr)) {
                        int channel = Integer.valueOf(datas.get(i).getChannel());
                        if (channel != 0) {
                            datas.get(i).setState(states[channel - 1]);
//                        }
                    }


                }
//                errorLayoout.setVisibility(View.INVISIBLE);
//                progressbar.setVisibility(View.INVISIBLE);
//                refresh.setVisibility(View.VISIBLE);
            }
        } else if (s.substring(12, 14).equals("06"))   //单个通道状态发生改变
        {
            //aa69 0001 0001 06 0301 000013690d0a
            int k = 0;         //k是通道号
            int state = Integer.valueOf(s.substring(21, 22));  //开关状态，1代表打开，0代表关闭
            if (s.substring(17, 18).equals("a"))
                k = 10;
            else
                k = Integer.valueOf(s.substring(17, 18));   //通道号,int型
            if (k == 0)                                          //如果通道号为0，则是总开关
            {
                if (state == 0) {
                    for (int i = 0; i < datas.size(); i++) {//更新状态到集合中
//                        if (datas.get(i).getRSaddr().equals(return_addr))
                            datas.get(i).setState("0");
                    }
                }
            } else     //如果通道号不为0，则更改data中的状态，并更新
            {
                LogUtil.i("单个通道发生改变==>position=" + k + "," + state);
                for (int i = 0; i < datas.size(); i++) {
//                    if (datas.get(i).getRSaddr().equals(return_addr)) {
                        if (datas.get(i).getChannel().equals(valueOf(k))) {
                            datas.get(i).setState(state == 1 ? "1" : "0");
//                        }
                    }
                }
            }
        }
    }


}

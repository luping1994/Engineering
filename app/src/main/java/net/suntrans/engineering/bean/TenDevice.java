package net.suntrans.engineering.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2018/3/5.
 * Des:
 */

public class TenDevice {
    public List<TenSwitchItem> datas = new ArrayList<>();

    TenDevice() {
        for (int i=0;i<=10;i++){
            TenSwitchItem item = new TenSwitchItem();
        }
    }
}

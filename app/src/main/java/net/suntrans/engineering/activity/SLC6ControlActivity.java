package net.suntrans.engineering.activity;

import android.os.Bundle;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.R;
import net.suntrans.engineering.bean.SixSwitchItem;
import net.suntrans.engineering.bean.TenSwitchItem;
import net.suntrans.engineering.fragment.SixControl_fragment;
import net.suntrans.engineering.fragment.TenControl_fragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2018/3/3.
 * Des:
 */

public class SLC6ControlActivity extends BasedActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        fixStatusBar();
        String ip = getIntent().getStringExtra("ip");
        int port = getIntent().getIntExtra("port", 8899);

        String type = getIntent().getStringExtra("type");
        if (type.equals("4300")) {
            ArrayList<SixSwitchItem> datas = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                SixSwitchItem item = new SixSwitchItem();
                item.setName("未命名");
                item.setRSaddr("00000000");
                item.setChannel(i + "");
                item.setState("0");
                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
            SixControl_fragment fragment = SixControl_fragment.newInstance(datas, ip, port);
            getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        }else if (type.equals("4100")){
            ArrayList<TenSwitchItem> datas = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                TenSwitchItem item = new TenSwitchItem();
                item.setName("未命名");
                item.setRSaddr("00000000");
                item.setChannel(i + "");
                item.setState("0");
                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
            TenControl_fragment fragment = TenControl_fragment.newInstance(datas, ip, port);
            getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        }
    }


}

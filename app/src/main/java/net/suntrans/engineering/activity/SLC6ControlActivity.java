package net.suntrans.engineering.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.Config;
import net.suntrans.engineering.R;
import net.suntrans.engineering.bean.SixSwitchItem;
import net.suntrans.engineering.bean.TenSwitchItem;
import net.suntrans.engineering.fragment.SixControl_fragment;
import net.suntrans.engineering.fragment.TenControl_fragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


/**
 * Created by Looney on 2018/3/3.
 * Des:
 */

public class SLC6ControlActivity extends BasedActivity {
    private TextView titleTx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        fixStatusBar();

        titleTx = findViewById(R.id.title);
        String title = getIntent().getStringExtra("title");
        titleTx.setText(title);


        String ip = getIntent().getStringExtra("ip");
        int port = getIntent().getIntExtra("port", 8899);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String type = getIntent().getStringExtra("type");

        Fragment fragment = null;
        if (Config.CODE_ST_SLC_6.equals(type)) {
            ArrayList<SixSwitchItem> datas = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                SixSwitchItem item = new SixSwitchItem();
                item.setName(getString(R.string.not_named));
                item.setRSaddr("00000000");
                item.setChannel(i + "");
                item.setType(type);
                item.setState("0");
                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
            fragment = SixControl_fragment.newInstance(datas, ip, port, type);
        } else if (Config.CODE_ST_SLC_10.equals(type)) {
            ArrayList<TenSwitchItem> datas = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                TenSwitchItem item = new TenSwitchItem();
                item.setName(getString(R.string.not_named));
                item.setRSaddr("00000000");
                item.setChannel(i + "");
                item.setState("0");

                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
            fragment = TenControl_fragment.newInstance(datas, ip, port);
        } else if (Config.CODE_ST_SLC_3_2.equals(type)) {
            ArrayList<SixSwitchItem> datas = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                SixSwitchItem item = new SixSwitchItem();
                item.setName(getString(R.string.not_named));
                item.setRSaddr("00000000");
                item.setChannel(i + "");
                item.setState("0");
                item.setType(type);

                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
            fragment = SixControl_fragment.newInstance(datas, ip, port, type);
        } else if (Config.CODE_ST_SLC_2PLUS.equals(type)) {
            ArrayList<SixSwitchItem> datas = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                SixSwitchItem item = new SixSwitchItem();
                item.setName(getString(R.string.not_named));
                item.setRSaddr("00000000");
                item.setChannel(i + "");
                item.setState("0");
                item.setType(type);
                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
            fragment = SixControl_fragment.newInstance(datas, ip, port, type);
        }
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();

    }


}

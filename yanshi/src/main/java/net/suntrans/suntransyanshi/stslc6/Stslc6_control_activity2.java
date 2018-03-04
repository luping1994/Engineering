package net.suntrans.suntransyanshi.stslc6;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RadioGroup;

import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.R2;
import net.suntrans.suntransyanshi.bean.SixSwitchItem;
import net.suntrans.suntransyanshi.bean.SwitchItem;
import net.suntrans.suntransyanshi.fragment.SixControl_fragment;
import net.suntrans.suntransyanshi.utils.DbHelper;
import net.suntrans.suntransyanshi.views.SegmentedGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/11.
 */

public class Stslc6_control_activity2 extends AppCompatActivity {
    SixControl_fragment fragment;
    ArrayList<SixSwitchItem> datas;
    @BindView(R2.id.segmented_group)
    SegmentedGroup segmentedGroup;
    public String flag;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st10_con);
        ButterKnife.bind(this);
        setUptoolBar();
        initView();
    }


    private void setUptoolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }


    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        datas = new ArrayList<>();
        final SwitchItem item2 = getIntent().getParcelableExtra("item");
        toolbar.setTitle(item2.getName());
        flag = getIntent().getStringExtra("source");
        if (flag.equals("bendi")) {
            DbHelper dh = new DbHelper(this, "IBMS", null, 1);
            SQLiteDatabase db = dh.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor = db.query(false, "switchs_tb", new String[]{"Name,RSAddr,Channel"}, "Type=? and RSAddr=?", new String[]{"6", item2.getRSaddr()}, null, null, null, null);
            while (cursor.moveToNext()) {
                SixSwitchItem item = new SixSwitchItem();
                item.setName(cursor.getString(0));
                item.setRSaddr(cursor.getString(1));
                item.setChannel(cursor.getString(2));
                item.setState("0");
                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
            cursor.close();

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }else {
            for (int i=1;i<=6;i++){
                SixSwitchItem item = new SixSwitchItem();
                item.setName("未命名");
                item.setRSaddr("00000000");
                item.setChannel(i+"");
                item.setState("0");
                item.setCloseCmd();
                item.setOpenCmd();
                item.setOpImageId(R.drawable.ic_bulb_on);
                item.setCloseImageId(R.drawable.ic_bulb_off);
                datas.add(item);
            }
        }
        fragment = SixControl_fragment.newInstance(datas, item2);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                fragment.disconnect();
                if (checkedId == R.id.radio0) {
                    fragment.connectToServer(item2.getBendiip(), Integer.valueOf(item2.getBendiport()));
                } else if (checkedId == R.id.radio1) {
                    fragment.connectToServer(item2.getZhilianip(), Integer.valueOf(item2.getZhilianport()));

                } else if (checkedId == R.id.radio2) {
                    fragment.connectToServer(item2.getWaiwangip(), Integer.valueOf(item2.getWaiwangport()));

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

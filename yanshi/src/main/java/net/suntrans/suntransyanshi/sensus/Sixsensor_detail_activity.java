package net.suntrans.suntransyanshi.sensus;

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
import net.suntrans.suntransyanshi.bean.SwitchItem;
import net.suntrans.suntransyanshi.fragment.SixSensor_Fragment;
import net.suntrans.suntransyanshi.views.SegmentedGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/11.
 */

public class Sixsensor_detail_activity extends AppCompatActivity {
    SixSensor_Fragment fragment;
    public SwitchItem item;
    @BindView(R2.id.segmented_group)
    SegmentedGroup segmentedGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st10_con);
        ButterKnife.bind(this);
        setUptoolBar();
        initView();
    }

    public String flag;

    private void setUptoolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }


    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        item = getIntent().getParcelableExtra("item");
        flag = getIntent().getStringExtra("source");

        fragment = SixSensor_Fragment.newInstance(item);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                fragment.disconnected();
                if (checkedId==R.id.radio0){
                    fragment.connected(item.getBendiip(),Integer.valueOf(item.getBendiport()));
                }else if (checkedId==R.id.radio1){
                    fragment.connected(item.getZhilianip(),Integer.valueOf(item.getZhilianport()));

                }else if (checkedId==R.id.radio2){
                    fragment.connected(item.getWaiwangip(),Integer.valueOf(item.getWaiwangport()));

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

package net.suntrans.suntransyanshi.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;


import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.R2;
import net.suntrans.suntransyanshi.activity.base.BasedActiity;
import net.suntrans.suntransyanshi.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Looney on 2017/4/5.
 */

public class ApListActivity extends BasedActiity {
    List<ScanResult> scanResults = null;
    @BindView(R2.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanResults = wifiManager.getScanResults();
        LogUtil.i("shuliang:="+scanResults.size());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        MyAdapter adapter = new MyAdapter(R.layout.item_aplist,scanResults);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(adapter);
        recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerview.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("ssid",scanResults.get(position).SSID);
                setResult(101,intent);
                finish();
            }
        });
    }

    @Override
    public int getLayoutSourceId() {
        return R.layout.activity_aplist;
    }

    @Override
    public void setUpToolBar() {
        setTitle("wifi列表");
        super.setUpToolBar();
    }


    class MyAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {

        public MyAdapter(int layoutResId, List<ScanResult> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final ScanResult info) {
            helper.setText(R.id.ssidname,info.SSID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_top, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);
    }
}

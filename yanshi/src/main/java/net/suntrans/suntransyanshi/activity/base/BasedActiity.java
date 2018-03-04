package net.suntrans.suntransyanshi.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;


import net.suntrans.suntransyanshi.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Looney on 2017/2/23.
 */

public abstract class BasedActiity extends AppCompatActivity {
    public final static List<BasedActiity> mList = new LinkedList<BasedActiity>();
    public Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutSourceId());
        init();
        setUpToolBar();
        synchronized (mList) {
            mList.add(this);
        }
        initView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }

    protected abstract void initView();


    public void init() {

    }

    public abstract int getLayoutSourceId();

    public void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synchronized (mList) {
            mList.remove(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    protected void initData() {

    }

    public void killAll() {
        // 复制了一份mActivities 集合
        List<BasedActiity> copy;
        synchronized (mList) {
            copy = new LinkedList<BasedActiity>(mList);
        }
        for (BasedActiity activity : copy) {
            activity.finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

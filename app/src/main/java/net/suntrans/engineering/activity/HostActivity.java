package net.suntrans.engineering.activity;

import android.os.Bundle;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.R;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Looney on 2018/3/2.
 * Des:
 */

public class HostActivity extends BasedActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
    }
}

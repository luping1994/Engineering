package net.suntrans.engineering.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.R;
import net.suntrans.engineering.fragment.SensusFragment;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Looney on 2018/3/8.
 * Des:
 */

public class SensusActivity extends BasedActivity {
    private TextView titleTx;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensus);
        fixStatusBar();
        titleTx = findViewById(R.id.title);
        String title = getIntent().getStringExtra("title");
        titleTx.setText(title);

        findViewById(R.id.back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        String ip = getIntent().getStringExtra("ip");
        int port = getIntent().getIntExtra("port", 8899);

        SensusFragment fragment = SensusFragment.newInstance(ip, port, title);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

}

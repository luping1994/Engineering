package net.suntrans.engineering.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.Config;
import net.suntrans.engineering.R;
import net.suntrans.engineering.fragment.SixParamFragment;
import net.suntrans.engineering.fragment.TenParamFragment;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Looney on 2018/3/5.
 * Des:
 */

public class ParamActivity extends BasedActivity  {
    private String type;
    private TextView titleTx;
    private static final String TAG = "ParamActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

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
        int port = getIntent().getIntExtra("port",8899);
        type = getIntent().getStringExtra("type");
        if (Config.ST_SLC_6.equals(type)){

            SixParamFragment fragment = SixParamFragment.newInstance(ip,port);
            getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();


        }else if (Config.ST_SLC_10.equals(type)){

            TenParamFragment fragment = TenParamFragment.newInstance(ip,port);
            getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();

        }else if (Config.ST_SECC.equals(type)){


        }
    }


}

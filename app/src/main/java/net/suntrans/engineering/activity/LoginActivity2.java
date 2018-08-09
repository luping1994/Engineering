package net.suntrans.engineering.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;

import com.pgyersdk.update.PgyUpdateManager;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.App;
import net.suntrans.engineering.Config;
import net.suntrans.engineering.MainActivity;
import net.suntrans.engineering.R;
import net.suntrans.engineering.api.RetrofitHelper;
import net.suntrans.engineering.bean.LoginEntity;
import net.suntrans.engineering.usbTranslate.UsbTranslateService;
import net.suntrans.engineering.utils.UiUtils;
import net.suntrans.looney.widgets.EditView;
import net.suntrans.looney.widgets.LoadingDialog;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Looney on 2017/5/26.
 */

public class LoginActivity2 extends BasedActivity implements View.OnClickListener {
    public static final String TRANSITION_SLIDE_BOTTOM = "SLIDE_BOTTOM";
    public static final String EXTRA_TRANSITION = "EXTRA_TRANSITION";
    private EditView mobile;
    private EditView password;
    private LoadingDialog dialog;
    private Subscription subscribe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        init();
    }


    private UsbTranslateService.UsbBinder binder;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (UsbTranslateService.UsbBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void init() {
        bindService(new Intent(this, UsbTranslateService.class), connection, Context.BIND_AUTO_CREATE);
        mobile = (EditView) findViewById(R.id.mobile);

        findViewById(R.id.login).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                break;

            case R.id.login:
                String s = mobile.getText().toString();
                if (binder != null)
                    binder.send(s);
                break;
        }
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}

package net.suntrans.engineering.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.App;
import net.suntrans.engineering.R;
import net.suntrans.engineering.databinding.ActivityAutolinkBinding;
import net.suntrans.engineering.utils.UiUtils;

import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;

/**
 * Created by Looney on 2018/3/2.
 * Des:
 */

public class AutoLinkActivity extends BasedActivity {

    private EasyLink easyLink;
    private ActivityAutolinkBinding binding;
    private AlertDialog dialog;
//    private EasylinkP2P easylinkP2P;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_autolink);
        fixStatusBar();
        easyLink = new EasyLink(this);
//        easylinkP2P = new EasylinkP2P(this);

        listenwifichange();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog = new AlertDialog.Builder(AutoLinkActivity.this)
                .setCancelable(false)
                .create();
        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.ssid.getText().toString())||TextUtils.isEmpty(binding.password.getText().toString()))
                {
                    UiUtils.showToast("请确认SSID和密码");
                    return;
                }
                binding.start.setEnabled(false);
                startEasyLink(binding.ssid.getText().toString(),binding.password.getText().toString());
                View view = LayoutInflater.from(AutoLinkActivity.this)
                        .inflate(R.layout.item_loading, null, false);
                view.findViewById(R.id.close)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stopEasyLink();
                                binding.start.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                dialog.setView(view);
                dialog.show();
            }
        });
    }

    int settedCount =0;
    private void startEasyLink(String ssid,String password) {

        EasyLinkParams params = new EasyLinkParams();


        params.ssid = ssid;
        params.password = password;
        params.runSecond = 60000;
        params.sleeptime = 20;

        App.getSharedPreferences()
                .edit()
                .putString(ssid, password)
                .commit();

//        easylinkP2P.startEasyLink(params, new EasyLinkCallBack() {
//            @Override
//            public void onSuccess(int code, String message) {
//                UiUtils.showToast(message);
//
//            }
//
//            @Override
//            public void onFailure(int code, String message) {
//
//            }
//        });
        easyLink.startEasyLink(params, new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                settedCount++;
                UiUtils.showToast(message);
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        stopEasyLink();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void stopEasyLink() {

        easyLink.stopEasyLink(new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {

            }

            @Override
            public void onFailure(int code, String message) {

            }
        });

//        easylinkP2P.stopEasyLink(new EasyLinkCallBack() {
//            @Override
//            public void onSuccess(int code, String message) {
//
//            }
//
//            @Override
//            public void onFailure(int code, String message) {
//
//            }
//        });
    }

    private void listenwifichange() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    binding.ssid.setText(easyLink.getSSID());
                    String password = App.getSharedPreferences().getString(easyLink.getSSID(), "");
                    binding.password.setText(password);
                }
            }
        }
    };

}

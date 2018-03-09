package net.suntrans.engineering.activity;

import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.Config;
import net.suntrans.engineering.R;
import net.suntrans.engineering.api.RetrofitHelper;
import net.suntrans.engineering.bean.ProConfig;
import net.suntrans.engineering.tcp.TcpHelper;
import net.suntrans.engineering.utils.Converts;
import net.suntrans.engineering.utils.LogUtil;
import net.suntrans.engineering.utils.UiUtils;
import net.suntrans.looney.widgets.LoadingDialog;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2018/3/2.
 * Des:
 */

public class HostActivity extends BasedActivity implements TcpHelper.OnReceivedListener {

    private static final java.lang.String TAG = "HostActivity";
    private TextView titleTx;
    private Subscription subscribe;
    private ArrayAdapter<String> adapter;
    List<String> hostName;
    private Spinner spinner;
    private List<ProConfig.DataBean> data;
    private TcpHelper helper;
    private String type;


    private LoadingDialog dialog;

    //wifi模块重启命令
    private String rebootOrder = "AB 68 43 00 00 00 00 00 03 03 00 04 00 00 00 CA EB 0D 0A";
    private String order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        fixStatusBar();
        findViewById(R.id.back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        titleTx = findViewById(R.id.title);
        String title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        titleTx.setText(title);

        spinner = findViewById(R.id.spinner);
        hostName = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, hostName);
        String ip = getIntent().getStringExtra("ip");
        int port = getIntent().getIntExtra("port", -1);
        connectToServer(ip, port);
        spinner.setAdapter(adapter);
        findViewById(R.id.save)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedPos == 0) {
                            UiUtils.showToast(getString(R.string.title_choose_project));
                            return;
                        }
                        new AlertDialog.Builder(HostActivity.this)
                                .setMessage(String.format(getString(R.string.tips_host_setting), hostName.get(selectedPos)))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog1, int which) {
                                        if (dialog == null) {
                                            dialog = new LoadingDialog(HostActivity.this);
                                            dialog.setWaitText(getString(R.string.tips_setting));
                                            dialog.setCancelable(true);
                                            setRemoteIpPort();
                                        }
                                        dialog.show();
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), null)
                                .create().show();
                    }
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPos = position;
                System.out.println(selectedPos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //发送设置远程服务器socket的命令
    private void setRemoteIpPort() {

        String ipHex = "";
        String portHex = "";
        String ipLength = "";

        String ip = "";
        if (Config.TYPE_SLC_10.equals(type)) {
            order = "43 00 00 00 00 00 03";
            ip = data.get(selectedPos - 1).sub.get(0).host;
            portHex = Integer.toHexString(data.get(selectedPos - 1).sub.get(0).port);

        } else if (Config.TYPE_SLC_6.equals(type)) {
            order = "43 00 00 00 00 00 03";
            ip = data.get(selectedPos - 1).sub.get(1).host;
            portHex = Integer.toHexString(data.get(selectedPos - 1).sub.get(1).port);
        }

//        ip = "192.168.191.1";
//        portHex = Integer.toHexString(9101);

        portHex = getStringFormat(portHex, 8);
        ipHex = Converts.strToASCII(ip);
        ipHex = Converts.toHexStringSetp2(ipHex);

        ipLength = getStringFormat(Integer.toHexString(ip.length()), 2);



        order = order + "09 00";//09 00  socket1   // 0a 00  socket2
        order += ipLength;//域名长度
        order += ipHex;
        order += portHex;

        order = Converts.getOrderWithCrc(order);

        order = order.toLowerCase();
        if (helper.binder != null)
            helper.binder.sendOrder(order);

//        ab 68 43 00 00 00 00 00 03 09 00 0b 31 39 32 2e 31 36 38 2e 31 2e 31 00 00 22 c3 41 96 0d 0a
        //AB 68 41 00 00 00 00 00 03 09 00 0B 31 39 32 2E 31 36 38 2E 31 2E 31 00 00 1F 90 BA 89 0D 0A
//        System.out.println(order);
    }

    @NonNull
    private String getStringFormat(String str, int targetLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetLength - str.length(); i++) {
            sb.append("0");
        }
        sb.append(str);
        return sb.toString();
    }

    private int selectedPos = 0;

    public void connectToServer(String ip, int port) {

        helper = new TcpHelper(this, ip, port, null);
        helper.setOnReceivedListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }


    private void getData() {
        if (subscribe != null) {
            if (!subscribe.isUnsubscribed()) {
                subscribe.unsubscribe();
            }
        }
        subscribe = RetrofitHelper.getApi()
                .getProConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ProConfig>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ProConfig proConfig) {

                        data = proConfig.data;
                        hostName.clear();
                        hostName.add("请选择工程");
                        for (ProConfig.DataBean su : data) {
                            hostName.add(su.name);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    public void disconnect() {
        if (helper != null)
            helper.unRegister();
        helper = null;
    }

    @Override
    protected void onDestroy() {
        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
        disconnect();
        super.onDestroy();
    }

    private Handler handler = new Handler();
    @Override
    public void onReceive(String content) {
        LogUtil.i(TAG, content);
        if (content.equals(order)){
            if (dialog!=null){
                dialog.dismiss();
            }
            UiUtils.showToast("命令已发送");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    helper.binder.sendOrder(rebootOrder);

                }
            },500);
        }
        if (content.equals("与服务器连接失败,重连中...") || content.equals("发送失败") || content.equals("连接中断")) {
            UiUtils.showToast(content);
            return;
        }
    }

    @Override
    public void onConnected() {
    }
}

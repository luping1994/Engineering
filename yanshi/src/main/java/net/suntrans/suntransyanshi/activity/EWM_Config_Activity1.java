package net.suntrans.suntransyanshi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.R2;
import net.suntrans.suntransyanshi.activity.base.BasedActiity;
import net.suntrans.suntransyanshi.api.RetrofitHelper;
import net.suntrans.suntransyanshi.bean.DeviceItem_info;
import net.suntrans.suntransyanshi.udp.UDPManager;
import net.suntrans.suntransyanshi.utils.Converts;
import net.suntrans.suntransyanshi.utils.LogUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Looney on 2017/3/28.
 */

public class EWM_Config_Activity1 extends BasedActiity {

    private static final String TAG = "EWM_Config_Activity";
    @BindView(R2.id.qvxiao)
    Button qvxiao;
    @BindView(R2.id.commit)
    Button commit;
    private String ip;
    private String type;
    private UDPManager manager;


    @Override
    public int getLayoutSourceId() {
        return R.layout.activity_ewmconfig1;
    }

    @Override
    protected void initView() {
        ip = getIntent().getStringExtra("IP");
        type = getIntent().getStringExtra("deviceType");
        LogUtil.i(TAG, ip);
        ButterKnife.bind(this);

    }

    @Override
    public void setUpToolBar() {
//        setTitle("网络配置");
//        super.setUpToolBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diveceset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.commit) {


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void commitData() throws UnsupportedEncodingException {
        String SSID = ((EditText) findViewById(R.id.ssid)).getText().toString();
        String pass = ((EditText) findViewById(R.id.pass)).getText().toString();

        Map<String, String> map = new HashMap<>();
        map.put("CLICK", "Save");
        map.put("pass", pass);
        map.put("SSID", SSID);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            System.out.println(key + "," + value);
        }
        RetrofitHelper.getApi("http://" + ip)
                .etBaseEwmSetting(SSID, pass, "Save")
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LogUtil.i(TAG, response.code() + "");
                        try {
                            String s = response.body().string();
                            LogUtil.i(TAG, s);
                            s = s.replace(" ", "");
                            if (s.contains("SaveConfigDone!")) {
                                handler.sendEmptyMessage(CONFIG_DONE);
                            } else {
                                handler.sendEmptyMessage(CONFIG_ERROR);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(CONFIG_ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        handler.sendEmptyMessage(CONFIG_ERROR);
                    }
                });
    }

    public void findAp(View view) {
        Intent intent = new Intent();
        intent.setClass(this, ApListActivity.class);
        startActivityForResult(intent, 100);
        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom, android.support.v7.appcompat.R.anim.abc_slide_out_top);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            String ssid = data.getStringExtra("ssid");
            ((EditText) findViewById(R.id.ssid)).setText(ssid);
        }

    }


    private static final int CONFIG_DONE = 1;
    private static final int CONFIG_ERROR = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CONFIG_DONE) {
                new AlertDialog.Builder(EWM_Config_Activity1.this)
                        .setMessage(getString(R.string.tips_changesuccess))
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                overridePendingTransition(0, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);
                            }
                        }).create().show();
            } else if (msg.what == CONFIG_ERROR) {
                new AlertDialog.Builder(EWM_Config_Activity1.this)
                        .setMessage("修改失败!")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                overridePendingTransition(0, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);
                            }
                        }).create().show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
    }

    @OnClick({R2.id.qvxiao, R2.id.commit})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.qvxiao) {
            finish();
            overridePendingTransition(0, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);

        } else if (i == R.id.commit) {
            new AlertDialog.Builder(this).setMessage("提交修改?")
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (type.equals("ewm"))
                                    commitData();
                                else {
                                    new GetDataTask().execute();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).create().show();

        }
    }

    private void commitDataToHx() throws UnsupportedEncodingException, InterruptedException {
        if (manager == null)
            manager = new UDPManager(handler2);
        String SSID = ((EditText) findViewById(R.id.ssid)).getText().toString();
        String pass = ((EditText) findViewById(R.id.pass)).getText().toString();
        String setSsidCmd = "123456AT+SSID=" + "\"" + SSID + "\"" + '\r';
        String setPasswordCmd = "123456AT+KEY=1,0," + "\"" + pass + "\"" + '\r';
//        String getPassword = "123456AT+PASS=!"+'\r';
        manager.send(ip, setSsidCmd.getBytes("utf-8"), DeviceItem_info.hx);
        Thread.sleep(100);
        manager.send(ip, setPasswordCmd.getBytes("utf-8"), DeviceItem_info.hx);
    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                commitDataToHx();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    MyHandler handler2 = new MyHandler();

    class MyHandler extends Handler {
        public static final int RECEIVE_UDP_DATA = 2;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVE_UDP_DATA:
                    JSONObject data = (JSONObject) msg.obj;
                    try {
                        String content = data.getString("content");

                        content = new String(Converts.HexString2Bytes(content),"utf-8");
                        if (content.contains("ok")){
                            new AlertDialog.Builder(EWM_Config_Activity1.this)
                                    .setMessage(getString(R.string.tips_changesuccess))
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            overridePendingTransition(0, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);
                                        }
                                    }).create().show();
                        }
                        LogUtil.i(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }


    }
}

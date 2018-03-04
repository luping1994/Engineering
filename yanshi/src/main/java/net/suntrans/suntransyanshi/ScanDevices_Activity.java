package net.suntrans.suntransyanshi;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.suntrans.suntransyanshi.activity.AddDevicesActivity2;
import net.suntrans.suntransyanshi.activity.AdvancedSetting_Activity;
import net.suntrans.suntransyanshi.activity.EWM_Config_Activity1;
import net.suntrans.suntransyanshi.adapter.RecyclerAdapter;
import net.suntrans.suntransyanshi.bean.DeviceItem_info;
import net.suntrans.suntransyanshi.bean.EWM_COMMAND;
import net.suntrans.suntransyanshi.bean.SwitchItem;
import net.suntrans.suntransyanshi.sensus.Sixsensor_detail_activity;
import net.suntrans.suntransyanshi.slslc10.St_slc_10_activity;
import net.suntrans.suntransyanshi.stslc6.Stslc6_control_activity2;
import net.suntrans.suntransyanshi.udp.UDPManager;
import net.suntrans.suntransyanshi.utils.Converts;
import net.suntrans.suntransyanshi.utils.LogUtil;
import net.suntrans.suntransyanshi.utils.UiUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.fogcloud.fog_mdns.api.MDNS;
import io.fogcloud.fog_mdns.helper.SearchDeviceCallBack;
import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;
import io.fogcloud.sdk.easylink.plus.EasyLink_v2;
import io.fogcloud.sdk.easylink.plus.EasyLink_v3;

import static net.suntrans.suntransyanshi.ScanDevices_Activity.MyHandler.UPDATE_RECYCLERVIEW;

/**
 * Created by Administrator on 2017/4/11.
 */

public class ScanDevices_Activity extends AppCompatActivity {


    private static final java.lang.String TAG = "ScanDevices_Activity";
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.imageView)
    ImageView imageView;
    @BindView(R2.id.count)
    TextView count;
    @BindView(R2.id.tx_state)
    TextView txState;
    @BindView(R2.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R2.id.choose_wlan)
    TextView chooseWlan;
    @BindView(R2.id.re_scan)
    Button reScan;
    @BindView(R2.id.hand_add)
    Button handAdd;
    @BindView(R2.id.rl_noDevice)
    RelativeLayout rlNoDevice;


    private Animation animation;
    private List<DeviceItem_info> datas;
    private List<DeviceItem_info> datas_copy;
    private UDPManager udpManager;
    private MyAdapter adapter;
    private String type;
    private ItemTouchHelper helper;


    private AlertDialog netConfigDialog;
    private TextView ssid;
    private EditText password;
    private TextView gateway;
    private EditText extrainfo;
    private EditText interval;
    private Button button;
    private LinearLayout linearLayout;
    private TextView textView;
    EasyLink link;
    private MDNS mdns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        udpManager = new UDPManager(handler);
        datas = new ArrayList<>();
        datas_copy = new ArrayList<>();
        animation = AnimationUtils.loadAnimation(this, R.anim.scan_image);
        LinearInterpolator interpolator = new LinearInterpolator();
        animation.setInterpolator(interpolator);

        type = getIntent().getStringExtra("devicetype");
        if (type.equals(RecyclerAdapter.ST_SLC_10)) {
            toolbar.setTitle("十通道控制器");
        } else if (type.equals(RecyclerAdapter.ST_SLC_6)) {
            toolbar.setTitle("六通道控制器");
        } else if (type.equals(RecyclerAdapter.sensus)) {
            toolbar.setTitle("第六感官");
        } else {
            toolbar.setTitle(getResources().getString(R.string.finddevices));
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(manager);
        recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new MyAdapter();
        recyclerview.setAdapter(adapter);

        helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int swipflag = ItemTouchHelper.END;
                return makeMovementFlags(0, swipflag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerview);


        link = new EasyLink(getApplicationContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanDevices_Activity.this);
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.item_dialog, null, false);
        ssid = (TextView) view.findViewById(R.id.ssid);
        password = (EditText) view.findViewById(R.id.password);
        gateway = (TextView) view.findViewById(R.id.gateway);
        extrainfo = (EditText) view.findViewById(R.id.extrainfo);
        interval = (EditText) view.findViewById(R.id.interval);
        button = (Button) view.findViewById(R.id.bt_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted) {
                    EasyLink_v2.getInstence().stopTransmitting();
                    EasyLink_v3.getInstence().stopTransmitting();
                    isStarted = false;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setVisibility(View.INVISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                            button.setBackgroundResource(R.color.colorPrimary);
                            button.setText("开始");
                        }
                    });

                } else {
                    try {
                        startNetConfig();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        textView = (TextView) view.findViewById(R.id.alert_msg);
        linearLayout = (LinearLayout) view.findViewById(R.id.root_setting);
        ssid.setText(link.getSSID());
        builder.setView(view);
        builder.setTitle(getString(R.string.net_setting));
        netConfigDialog = builder.create();

        scanDevice();
        mdns = new MDNS(this.getApplicationContext());
        mdns.startSearchDevices(serviceInfo, new SearchDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                super.onSuccess(code, message);
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
            }

            @Override
            public void onDevicesFind(int code, JSONArray devices) {
                super.onDevicesFind(code, devices);
                LogUtil.i("接收到数据：" + devices.toString());
                try {
                    for (int i = 0; i < devices.length(); i++) {
                        JSONObject object = devices.getJSONObject(i);
                        String name = (String) object.get("Name");
                        String IP = (String) object.get("IP");
                        int Port = (int) object.get("Port");
                        String MAC = (String) object.get("MAC");
//                        String FirmwareRev = (String) object.get("Firmware Rev");
//                        String MICOOSRev = (String) object.get("MICO OS Rev");
//                        String Model = (String) object.get("Model");
//                        String Protocol = (String) object.get("Protocol");
//                        String Manufacturer = (String) object.get("Manufacturer");
//                        String MXCHIPinc = (String) object.get("MXCHIP Inc.");
                        boolean a = true;
                        for (int j = 0; j < deviceItems.size(); j++) {
                            if (deviceItems.get(j).getMAC().equals(MAC)) {
                                a = false;
                            }
                        }
                        if (a) {
                            DeviceItem_info item = new DeviceItem_info();
                            item.setIP(IP);
                            item.setMAC(MAC);
                            deviceItems.add(item);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ArrayList<DeviceItem_info> deviceItems = new ArrayList<>();

    private final String serviceInfo = "_easylink._tcp.local.";

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void scanDevice() {
        datas.clear();
//        mdns.stopSearchDevices(null);
        count.setText("0");
        rlNoDevice.setVisibility(View.INVISIBLE);
        recyclerview.setVisibility(View.INVISIBLE);
        txState.setText(getString(R.string.scaning));
        imageView.startAnimation(animation);
        new GetDataTask().execute();
    }


    private void stopScanDevice() {
        imageView.clearAnimation();

        if (datas.size() == 0) {
            txState.setText(getString(R.string.noscanresult));
            rlNoDevice.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.INVISIBLE);
        } else {

            rlNoDevice.setVisibility(View.INVISIBLE);
            String s = String.format(getString(R.string.hasscanresult), datas.size());
            txState.setText(s);
            count.setText(datas.size() + "");
            recyclerview.setVisibility(View.VISIBLE);
        }
    }

    private void sendUdp() throws InterruptedException {
        if (udpManager == null) {
            udpManager = new UDPManager(handler);
        }
        udpManager.send("255.255.255.255", Converts.HexString2Bytes(EWM_COMMAND.EMS_CMD_GET_MAC_ADDR), DeviceItem_info.ewm);
        Thread.sleep(200);
        udpManager.send("255.255.255.255", "123456AT+QMAC".getBytes(), DeviceItem_info.hx);
    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                sendUdp();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanDevice();
                }
            }, 5500);
            super.onPostExecute(aVoid);
        }
    }

    @OnClick({R2.id.choose_wlan, R2.id.re_scan, R2.id.hand_add})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.choose_wlan) {
        } else if (i == R.id.re_scan) {
            scanDevice();

        } else if (i == R.id.hand_add) {
            netConfigDialog.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.adddevice) {
            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom,0);
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private MyHandler handler = new MyHandler();

    class MyHandler extends Handler {
        public static final int HAS_DEVICE = 1;
        public static final int NO_DEVICES = 0;
        public static final int RECEIVE_UDP_DATA = 2;
        public static final int UPDATE_RECYCLERVIEW = 3;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HAS_DEVICE:
                    break;
                case NO_DEVICES:
                    break;
                case RECEIVE_UDP_DATA:
                    parseData((JSONObject) msg.obj);
                    break;
                case UPDATE_RECYCLERVIEW:
                    stopScanDevice();
                    rlNoDevice.setVisibility(View.INVISIBLE);
                    recyclerview.setVisibility(View.VISIBLE);

                    ArrayList<DeviceItem_info> infos = new ArrayList<>(deviceItems);
                    ArrayList<DeviceItem_info> infos1 = new ArrayList<>(datas);
                    for (DeviceItem_info item : infos) {
                        boolean a = true;
                        for (DeviceItem_info info :
                                infos1) {
                            if (item.getMAC().equals(info.getMAC())) {
                                a = false;
                            }
                        }
                        if (a) {
                            infos1.add(item);
                        }

                    }
                    datas_copy.clear();
                    datas_copy.addAll(infos1);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        udpManager.close();
        mdns.stopSearchDevices(null);
    }

    private boolean parseData(JSONObject obj) {
        try {
            String ip = obj.getString("ip");
            String data = obj.getString("content");
            int port = obj.getInt("port");
            if (data.length() < 16)
                return false;
//            LogUtil.i(TAG, "IP=" + ip + ",content=" + data);
            if (port == 8089) {
                if (!data.substring(0, 16).equals("0C0010000100E2FF"))
                    return false;
            }
            if (port == 988) {
                String s = new String(Converts.HexString2Bytes(data), "utf-8");
                if (!s.contains("+OK=")) {
                    return false;
                }
            }

            handler.removeMessages(UPDATE_RECYCLERVIEW);

            if (port == 8089) {
                byte[] src = Converts.HexString2Bytes(data);
                src = UiUtils.subBytes(src, 8, src.length - 8);
                byte[] mac = UiUtils.subBytes(src, 0, 6);
                String mac1 = Converts.Bytes2HexString(mac);
                StringBuilder builder = new StringBuilder();
                builder.append(mac1.substring(0, 2))
                        .append(":")
                        .append(mac1.substring(2, 4))
                        .append(":")
                        .append(mac1.substring(4, 6))
                        .append(":")
                        .append(mac1.substring(6, 8))
                        .append(":")
                        .append(mac1.substring(8, 10))
                        .append(":")
                        .append(mac1.substring(10, 12));
                mac1 = builder.toString();
                boolean has = false;
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getMAC().equals(mac1)) {
                        has = true;
                    }
                }
                if (!has) {
                    DeviceItem_info info = new DeviceItem_info();
                    info.setIP(ip);
                    info.setMAC(mac1);
                    info.setType(DeviceItem_info.ewm);
                    LogUtil.i(info.toString());
                    datas.add(info);
                }
            } else if (port == 988) {
                String s = new String(Converts.HexString2Bytes(data), "utf-8");

                String mac1 = s.substring(4, 16);
                StringBuilder builder = new StringBuilder();
                builder.append(mac1.substring(0, 2))
                        .append(":")
                        .append(mac1.substring(2, 4))
                        .append(":")
                        .append(mac1.substring(4, 6))
                        .append(":")
                        .append(mac1.substring(6, 8))
                        .append(":")
                        .append(mac1.substring(8, 10))
                        .append(":")
                        .append(mac1.substring(10, 12));
                mac1 = builder.toString();
                boolean has = false;
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getMAC().equals(mac1)) {
                        has = true;
                    }
                }
                if (!has) {
                    DeviceItem_info info = new DeviceItem_info();
                    info.setIP(ip);
                    info.setMAC(mac1);
                    info.setType(DeviceItem_info.hx);
                    LogUtil.i(info.toString());
                    datas.add(info);
                }
            }

            handler.sendEmptyMessageDelayed(UPDATE_RECYCLERVIEW, 2000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_online_device, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            TextView setting;
            TextView add;
            ImageView more;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.ip);
                more = (ImageView) itemView.findViewById(R.id.more);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(ScanDevices_Activity.this, more);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_main, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return parseMenuClick(item);
                            }


                        });
                        popupMenu.show();
                    }
                });
//                itemView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        LogUtil.i("我被点击了");
//                        String[] strings = getResources().getStringArray(R.array.devices);
//                        new AlertDialog.Builder(ScanDevices_Activity.this)
//                                .setTitle("选择设备类型")
//                                .setSingleChoiceItems(strings, -1, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if (which==0){
//                                            final SwitchItem item2 = new SwitchItem();
//                                            item2.setName("未命名");
//                                            item2.setRSaddr("00000000");
//                                            item2.setZhilianip(datas.get(getAdapterPosition()).ip);
//                                            item2.setZhilianport("8000");
//                                            item2.setBendiip(datas.get(getAdapterPosition()).ip);
//                                            item2.setBendiport("8000");
//                                            item2.setWaiwangport("8101");
//                                            item2.setWaiwangip("ys.suntrans.net");
//                                            Intent intent =new Intent(ScanDevices_Activity.this,St_slc_10_activity.class);
//                                            intent.putExtra("item",item2);
//                                            intent.putExtra("source","juyuwang");
//                                            startActivity(intent);
//                                        }else if (which==1){
//                                            final SwitchItem item2 = new SwitchItem();
//                                            item2.setName("未命名");
//                                            item2.setRSaddr("00000000");
//                                            item2.setZhilianip(datas.get(getAdapterPosition()).ip);
//                                            item2.setZhilianport("8000");
//                                            item2.setBendiip(datas.get(getAdapterPosition()).ip);
//                                            item2.setBendiport("8000");
//                                            item2.setWaiwangport("8101");
//                                            item2.setWaiwangip("ys.suntrans.net");
//                                            Intent intent =new Intent(ScanDevices_Activity.this,Stslc6_control_activity2.class);
//                                            intent.putExtra("item",item2);
//                                            intent.putExtra("source","juyuwang");
//                                            startActivity(intent);
//                                        }else if (which==2){
//                                            final SwitchItem item2 = new SwitchItem();
//                                            item2.setName("未命名");
//                                            item2.setRSaddr("00000000");
//                                            item2.setZhilianip(datas.get(getAdapterPosition()).ip);
//                                            item2.setZhilianport("8000");
//                                            item2.setBendiip(datas.get(getAdapterPosition()).ip);
//                                            item2.setBendiport("8000");
//                                            item2.setWaiwangport("8101");
//                                            item2.setWaiwangip("ys.suntrans.net");
//                                            Intent intent =new Intent(ScanDevices_Activity.this,Sixsensor_detail_activity.class);
//                                            intent.putExtra("item",item2);
//                                            startActivity(intent);
//                                        }
//                                    }
//                                }).setNegativeButton(getString(R.string.cancel),null).create().show();
//
//
//                    }
//                });
//                setting = (TextView) itemView.findViewById(R.id.setting);
//                add = (TextView) itemView.findViewById(R.id.add);
//                setting.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(ScanDevices_Activity.this, EWM_Config_Activity1.class);
//                        intent.putExtra("IP",datas.get(getAdapterPosition()).ip);
//                        startActivityForResult(intent,101);
//                        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom,0);
//                    }
//                });
//                add.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(ScanDevices_Activity.this, AddDevicesActivity2.class);
//                        startActivity(intent);
//                        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom,0);
//                    }
//                });


            }

            public void setData(int position) {
//                textView.setText("IP:"+datas.get(position).getIP()+",MAC:"+datas.get(position).getMAC()+"("+datas.get(position).getType()+")");
                textView.setText(datas_copy.get(position).getIP() + "(" + datas_copy.get(position).getType() + ")");
            }

            private boolean parseMenuClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.base_setting) {
                    if (datas.get(getAdapterPosition()).getType().equals(DeviceItem_info.ewm)) {
                        Intent intent = new Intent(ScanDevices_Activity.this, EWM_Config_Activity1.class);
                        intent.putExtra("IP", datas.get(getAdapterPosition()).getIP());
                        intent.putExtra("deviceType", "ewm");
                        startActivityForResult(intent, 101);
                        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom, 0);
                    } else {
                        Intent intent = new Intent(ScanDevices_Activity.this, EWM_Config_Activity1.class);
                        intent.putExtra("IP", datas.get(getAdapterPosition()).getIP());
                        intent.putExtra("deviceType", "hx");
                        startActivityForResult(intent, 101);
                        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom, 0);
                    }


                } else if (i == R.id.advance_setting) {
                    if (datas.get(getAdapterPosition()).getType().equals(DeviceItem_info.ewm)) {

                        Intent intent9 = new Intent(ScanDevices_Activity.this, AdvancedSetting_Activity.class);    //为Intent设置Action属性
                        intent9.putExtra("ip", datas.get(getAdapterPosition()).getIP());
                        startActivity(intent9);
                    } else {
                        String url = "http://" + datas.get(getAdapterPosition()).getIP();
                        Intent intent9 = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
                        intent9.setData(Uri.parse(url)); //为Intent设置DATA属性
                        startActivity(intent9);
                    }

                } else if (i == R.id.add2local) {
                    Intent intent1 = new Intent(ScanDevices_Activity.this, AddDevicesActivity2.class);
                    startActivity(intent1);
                    overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom, 0);

                } else if (i == R.id.control_10) {
                    final SwitchItem item2 = new SwitchItem();
                    item2.setName("未命名");
                    item2.setRSaddr("00000000");
                    item2.setZhilianip(datas.get(getAdapterPosition()).getIP());
                    item2.setZhilianport("8000");
                    item2.setBendiip(datas.get(getAdapterPosition()).getIP());
                    item2.setBendiport("8000");
                    item2.setWaiwangport("8102");
                    item2.setWaiwangip("ys.suntrans.net");
                    Intent intent3 = new Intent(ScanDevices_Activity.this, St_slc_10_activity.class);
                    intent3.putExtra("item", item2);
                    intent3.putExtra("source", "juyuwang");
                    startActivity(intent3);

                } else if (i == R.id.control_6) {
                    final SwitchItem item3 = new SwitchItem();
                    item3.setName("未命名");
                    item3.setRSaddr("00000000");
                    item3.setZhilianip(datas.get(getAdapterPosition()).getIP());
                    item3.setZhilianport("8000");
                    item3.setBendiip(datas.get(getAdapterPosition()).getIP());
                    item3.setBendiport("8000");
                    item3.setWaiwangport("8102");
                    item3.setWaiwangip("ys.suntrans.net");
                    Intent intent4 = new Intent(ScanDevices_Activity.this, Stslc6_control_activity2.class);
                    intent4.putExtra("item", item3);
                    intent4.putExtra("source", "juyuwang");
                    startActivity(intent4);

                } else if (i == R.id.control_sensor) {
                    final SwitchItem item4 = new SwitchItem();
                    item4.setName("未命名");
                    item4.setRSaddr("0000");
                    item4.setZhilianip(datas.get(getAdapterPosition()).getIP());
                    item4.setZhilianport("2000");
                    item4.setBendiip(datas.get(getAdapterPosition()).getIP());
                    item4.setBendiport("2000");
                    item4.setWaiwangport("8102");
                    item4.setWaiwangip("ys.suntrans.net");

                    Intent intent5 = new Intent(ScanDevices_Activity.this, Sixsensor_detail_activity.class);
                    intent5.putExtra("source", "juyuwang");

                    intent5.putExtra("item", item4);
                    startActivity(intent5);

                }
                return true;
            }
        }
    }


    boolean isStarted = false;

    private void startNetConfig() throws UnsupportedEncodingException {
        if (isStarted) {
            return;
        }
        textView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        button.setBackgroundResource(R.color.colorPrimary);
        button.setText("开始");
        isStarted = true;
//        WebView view;
        EasyLinkParams params = new EasyLinkParams();
        link.getSSID();
        button.setBackgroundResource(R.color.colorAccent);
        button.setText("停止");

        final String ssid1 = new String(ssid.getText().toString().trim().getBytes("utf-8"), "utf-8");
        final String key1 = new String(password.getText().toString().trim().getBytes("utf-8"), "utf-8");
//        LogUtil.i(TAG,"SSID="+params.ssid+",password="+params.password+",extradata="+params.extraData+",sleeptime="+params.sleeptime);
//        EasyLink_v3.getInstence().transmitSettings("HEHE".getBytes("utf-8"),"luping123".getBytes("utf-8"),"my extra".getBytes("utf-8"),"",50);

        EasyLink_v2.getInstence().transmitSettings(ssid1.getBytes("utf-8"), key1.getBytes("utf-8"), "EWM3165".getBytes("utf-8"), 50);
        EasyLink_v3.getInstence().transmitSettings(ssid1.getBytes("utf-8"), key1.getBytes("utf-8"), "EWM3165".getBytes("utf-8"), "", 50);


    }


}

package net.suntrans.engineering;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pgyersdk.update.PgyUpdateManager;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.activity.AutoLinkActivity;
import net.suntrans.engineering.activity.HostActivity;
import net.suntrans.engineering.activity.ParamActivity;
import net.suntrans.engineering.activity.SLC6ControlActivity;
import net.suntrans.engineering.activity.SensusActivity;
import net.suntrans.engineering.databinding.ActivityMainBinding;
import net.suntrans.engineering.easylink.DeviceInfo;
import net.suntrans.engineering.mdns.api.MDNS;
import net.suntrans.engineering.mdns.helper.SearchDeviceCallBack;
import net.suntrans.engineering.utils.LogUtil;
import net.suntrans.engineering.utils.RxTimerUtil;
import net.suntrans.engineering.utils.UiUtils;
import net.suntrans.looney.widgets.IosAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

import static net.suntrans.engineering.Config.EWM_SERVICE;
import static net.suntrans.engineering.Config.ST_SLC_10;
import static net.suntrans.engineering.Config.ST_SLC_6;
import static net.suntrans.engineering.Config.ST_SRD;
import static net.suntrans.engineering.Config.ST_Sensus;

public class MainActivity extends BasedActivity {

    private MDNS mdns;
    public static WifiManager.MulticastLock lock;
    public static WifiManager wm = null;
    public static ServiceInfo info;

    private List<DeviceInfo> datas;
    private ActivityMainBinding binding;
    private MyAdapter adapter;

    private InetAddress intf;
    private JmDNS jmDNS;

    static {
        lock = null;
        info = null;
    }

    public static Map<String, DeviceInfo> findDeviceMap = new ConcurrentHashMap();
    private InetAddress localIpAddress;
    private ServiceInfo serviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fixStatusBar();
        setUpRecyclerView();
        searchThreadStart();
        updateUI();

        binding.refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refreshlayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
//        startJmdns();
        PgyUpdateManager.register(this, Config.FILE_PROVIDER);
        checkWritePermission();
    }

    private Handler handler = new Handler();

    private void startJmdns() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (; ; ) {
                    if (isStarted)
                        return;
                    if (intf != null && jmDNS != null) {
                        search();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    if (intf == null) {
                        try {
                            intf = getLocalIpAddress();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            jmDNS = JmDNS.create(intf);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();

    }

    private void search() {
        try {
            if (wm == null)
                wm = (WifiManager) getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            lock = wm.createMulticastLock(getClass().getSimpleName());
            lock.setReferenceCounted(true);
            lock.acquire();//to receive multicast packets
            listener = new SimpleListener();
            jmDNS.addServiceListener(Config.EWM_SERVICE, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SimpleListener listener;

    public class SimpleListener implements ServiceListener {

        public void serviceResolved(ServiceEvent event) {
            LogUtil.i("serviceResolved");
            serviceInfo = jmDNS.getServiceInfo(Config.EWM_SERVICE, event.getName());
            LogUtil.i(event.getName());
            if (serviceInfo != null) {
                LogUtil.e("serviceINf!=null");

                if (findDeviceMap.containsKey(serviceInfo.getName())) {
//                    DeviceInfo info = new DeviceInfo();
//                    info.Name = serviceInfo.getName();
//                    info.IP = MDNS.setDeviceIP(new StringBuilder().append((serviceInfo).getAddress()).toString());
//                    info.MAC = MDNS.setDeviceIP(new StringBuilder().append((serviceInfo).getTextString()).toString());
//                    findDeviceMap.put(serviceInfo.getName(),info);

                }
            } else {
                LogUtil.e("serviceINfo==null");
            }

        }

        public void serviceRemoved(ServiceEvent event) {


        }

        public void serviceAdded(ServiceEvent event) {
            jmDNS.requestServiceInfo(event.getType(), event.getName());
        }
    }

    private void checkWritePermission() {
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
        final List<PermissonItem> permissionItems = new ArrayList<PermissonItem>();
        permissionItems.add(new PermissonItem(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE, getString(R.string.tips_get_network_state), R.drawable.permission_ic_memory));
        permissionItems.add(new PermissonItem(Manifest.permission.ACCESS_WIFI_STATE, getString(R.string.tips_get_network_state), R.drawable.permission_ic_memory));
        permissionItems.add(new PermissonItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.tips_get_write_storage), R.drawable.permission_ic_memory));
        permissionItems.add(new PermissonItem(Manifest.permission.CHANGE_WIFI_STATE, getString(R.string.tips_change_wifi_state), R.drawable.permission_ic_memory));

        HiPermission.create(this)
                .checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permisson, int position) {

                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {

                    }
                });
    }


    private void setUpRecyclerView() {
        datas = new ArrayList<>();
        adapter = new MyAdapter(R.layout.item_devices, datas);
//        DeviceInfo info = new DeviceInfo();
//        info.Name = "ST-SLC-6_00000000";
//        info.IP = "192.168.191.5";
//        info.Port = 8899;
//        info.MAC = "C8:93:46:84:29:C4";
//        findDeviceMap.put(info.MAC,info);
//
//        DeviceInfo info2 = new DeviceInfo();
//        info2.Name = "ST-SLC-10_00003039";
//        info2.IP = "192.168.191.2";
//        info2.Port = 8899;
//        info2.MAC = "C8:93:46:84:2E:BF";
//        findDeviceMap.put(info2.MAC,info2);

        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        binding.autoLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AutoLinkActivity.class));
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == -1) {
                    UiUtils.showToast(getString(R.string.tips_waiting));
                    return;
                }
                if (view.getId() == R.id.host) {
                    Intent intent = new Intent(MainActivity.this, HostActivity.class);
                    intent.putExtra("title", datas.get(position).Name);
                    intent.putExtra("ip", datas.get(position).IP);
                    intent.putExtra("port", datas.get(position).Port);
                    if (datas.get(position).Name.contains(Config.ST_SLC_6)) {
                        intent.putExtra("type", Config.ST_SLC_6);
                        intent.putExtra("code", Config.CODE_ST_SLC_6);
                    } else if (datas.get(position).Name.contains(Config.ST_SLC_10)) {
                        intent.putExtra("type", Config.ST_SLC_10);
                        intent.putExtra("code", Config.CODE_ST_SLC_10);
                    } else if (datas.get(position).Name.contains(Config.ST_Sensus)) {
                        intent.putExtra("type", Config.ST_Sensus);
                        intent.putExtra("code", Config.CODE_SENSUS);
                    } else if (datas.get(position).Name.contains(Config.ST_SLC_3_2)) {
                        intent.putExtra("type", Config.ST_SLC_3_2);
                        intent.putExtra("code", Config.CODE_ST_SLC_3_2);
                    } else if (datas.get(position).Name.contains(Config.ST_SRD)) {
                        intent.putExtra("type", Config.ST_SRD);
                        intent.putExtra("code", Config.CODE_ST_SRD);
                    } else if (datas.get(position).Name.contains(Config.ST_ITL)) {
                        intent.putExtra("type", Config.ST_ITL);

                    } else if (datas.get(position).Name.contains(Config.ST_SECC)) {
                        intent.putExtra("type", Config.ST_SECC);
                        intent.putExtra("code", Config.CODE_ST_SECC);
                    }else if (datas.get(position).Name.contains(Config.ST_SECC)) {
                        intent.putExtra("type", Config.ST_SLC_2);
                        intent.putExtra("code", Config.CODE_ST_SLC_2);
                    }else if (datas.get(position).Name.contains(Config.ST_SLC_2PLUS)) {
                        intent.putExtra("type", Config.ST_SLC_2PLUS);
                        intent.putExtra("code", Config.CODE_ST_SLC_2PLUS);
                    } else {
                        return;
                    }
                    startActivity(intent);
                } else if (view.getId() == R.id.canshu) {
                    Intent intent = new Intent(MainActivity.this, ParamActivity.class);
                    intent.putExtra("title", datas.get(position).Name);
                    intent.putExtra("ip", datas.get(position).IP);
                    intent.putExtra("port", datas.get(position).Port);
                    if (datas.get(position).Name.contains(Config.ST_SLC_6)) {
                        intent.putExtra("type", Config.ST_SLC_6);
                    } else if (datas.get(position).Name.contains(Config.ST_SLC_10)) {
                        intent.putExtra("type", Config.ST_SLC_10);
                    }
                    startActivity(intent);
                } else {
                    UiUtils.showToast(getString(R.string.tips_not_sup_device));
                }

            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == -1) {
                    UiUtils.showToast("请稍后再试!");
                    return;
                }
                if (datas.get(position).Name.contains(ST_SLC_6)) {

                    Intent intent = new Intent(MainActivity.this, SLC6ControlActivity.class);
                    intent.putExtra("type", Config.CODE_ST_SLC_6);
                    intent.putExtra("ip", datas.get(position).IP);
                    intent.putExtra("port", datas.get(position).Port);
                    intent.putExtra("title", datas.get(position).Name);
                    startActivity(intent);

                } else if (datas.get(position).Name.contains(ST_SLC_10)) {

                    Intent intent = new Intent(MainActivity.this, SLC6ControlActivity.class);
                    intent.putExtra("type", Config.CODE_ST_SLC_10);
                    intent.putExtra("ip", datas.get(position).IP);
                    intent.putExtra("port", datas.get(position).Port);
                    intent.putExtra("title", datas.get(position).Name);
                    startActivity(intent);

                } else if (datas.get(position).Name.contains(ST_Sensus)) {
                    Intent intent = new Intent(MainActivity.this, SensusActivity.class);
                    intent.putExtra("type", Config.CODE_SENSUS);
                    intent.putExtra("ip", datas.get(position).IP);
                    intent.putExtra("port", datas.get(position).Port);
                    intent.putExtra("title", datas.get(position).Name);
                    startActivity(intent);
                } else if (datas.get(position).Name.contains(Config.ST_SLC_3_2)) {
                    Intent intent = new Intent(MainActivity.this, SLC6ControlActivity.class);
                    intent.putExtra("type", Config.CODE_ST_SLC_3_2);
                    intent.putExtra("ip", datas.get(position).IP);
                    intent.putExtra("port", datas.get(position).Port);
                    intent.putExtra("title", datas.get(position).Name);
                    startActivity(intent);
                } else if (datas.get(position).Name.contains(Config.ST_SLC_2PLUS)) {
                    Intent intent = new Intent(MainActivity.this, SLC6ControlActivity.class);
                    intent.putExtra("type", Config.CODE_ST_SLC_2PLUS);
                    intent.putExtra("ip", datas.get(position).IP);
                    intent.putExtra("port", datas.get(position).Port);
                    intent.putExtra("title", datas.get(position).Name);
                    startActivity(intent);
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    boolean isStarted = false;
    boolean stoped = false;

    //发现设备搜索线程
    private void searchThreadStart() {

        mdns = new MDNS(this.getApplicationContext());
        mdns.startSearchDevices(EWM_SERVICE, new SearchDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                super.onSuccess(code, message);
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
            }

            @Override
            public void onDevicesFind(int code, JSONArray deviceStatus) {
                super.onDevicesFind(code, deviceStatus);
                LogUtil.i(deviceStatus.toString());
                for (int i = 0; i < deviceStatus.length(); i++) {
                    try {
                        String s = deviceStatus.get(i).toString();
                        DeviceInfo deviceInfo = JSON.parseObject(s, DeviceInfo.class);
                        if (!findDeviceMap.containsKey(deviceInfo.MAC))
                            findDeviceMap.put(deviceInfo.MAC, deviceInfo);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    //开始搜索
    private void startDeviceSearch() {

        if (wm == null) {
            wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        lock = wm.createMulticastLock("mylock");
        lock.setReferenceCounted(true);
        lock.acquire();

    }

    //更新UI
    private void updateUI() {
        RxTimerUtil.interval(2000, new RxTimerUtil.IRxNext() {
            @Override
            public void doNext(long number) {
                datas.clear();
                Iterator localIterator = findDeviceMap.entrySet().iterator();
                while (localIterator.hasNext()) {
                    Map.Entry<String, DeviceInfo> next = (Map.Entry<String, DeviceInfo>) localIterator.next();
                    DeviceInfo value = next.getValue();
                    if (value.Name != null) {
                        if (value.Name.contains("#")) {
                            value.Name = value.Name.split("#")[0];
                        }
                    }
                    datas.add(value);
                }

                Collections.sort(datas);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    class MyAdapter extends BaseQuickAdapter<DeviceInfo, BaseViewHolder> {
        public int size = UiUtils.dip2px(48);

        public MyAdapter(int layoutResId, @Nullable List<DeviceInfo> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DeviceInfo item) {
            ImageView imageView = helper.getView(R.id.imageView);
            String name = item.Name;

            helper.setText(R.id.name, name)
                    .setText(R.id.ip, "IP:" + item.IP)
                    .setText(R.id.mac, "MAC:" + item.MAC);
            int resID = R.mipmap.ic_launcher;
            if (item.Name.contains(ST_SLC_10)) {
                resID = R.drawable.ic_shitongdao;
            } else if (item.Name.contains(ST_SLC_6)) {
                resID = R.drawable.ic_liutongdao;
            } else if (item.Name.contains(ST_Sensus)) {
                resID = R.drawable.diliugan;
            } else if (item.Name.contains(ST_SRD)) {
                resID = R.drawable.srd;

            } else if (item.Name.contains(Config.ST_SLC_3_2)) {
                resID = R.drawable.ic_32;

            }

            helper.addOnClickListener(R.id.host)
                    .addOnClickListener(R.id.canshu);
            Glide.with(MainActivity.this)
                    .load(resID)
                    .dontTransform()
                    .crossFade()
                    .override(size, size)
                    .into(imageView);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    public InetAddress getLocalIpAddress()
            throws Exception {
        if (wm == null) {
            wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        int i = wm.getConnectionInfo().getIpAddress();
        return InetAddress.getByAddress(new byte[]{(byte) (i & 0xFF), (byte) (i >> 8 & 0xFF), (byte) (i >> 16 & 0xFF), (byte) (i >> 24 & 0xFF)});
    }

    private long[] mHits = new long[2];

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            new IosAlertDialog(MainActivity.this)
                    .builder()
                    .setTitle(getString(R.string.tips_is_exit))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Process.killProcess(Process.myPid());
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null).show();
//            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
//            if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {
//
//            } else {
//                Toast.makeText(this.getApplicationContext(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();
//            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        stoped = true;
        handler.removeCallbacksAndMessages(null);
        RxTimerUtil.cancel();
        mdns.stopSearchDevices(new SearchDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                super.onSuccess(code, message);
            }
        });

//        jmDNS.removeServiceListener(Config.EWM_SERVICE, listener);
//        jmDNS.unregisterAllServices();
//        try {
//            jmDNS.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        lock.release();
        super.onDestroy();
    }
}

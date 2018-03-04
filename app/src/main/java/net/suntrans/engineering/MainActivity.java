package net.suntrans.engineering;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.activity.AutoLinkActivity;
import net.suntrans.engineering.activity.SLC6ControlActivity;
import net.suntrans.engineering.databinding.ActivityMainBinding;
import net.suntrans.engineering.easylink.DeviceInfo;
import net.suntrans.engineering.utils.RxTimerUtil;
import net.suntrans.engineering.utils.UiUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;


import android.content.Context;
import android.widget.ImageView;

import io.fogcloud.fog_mdns.api.MDNS;
import io.fogcloud.fog_mdns.helper.SearchDeviceCallBack;
import okhttp3.internal.http.HttpHeaders;

import static net.suntrans.engineering.Config.EWM_SERVICE;
import static net.suntrans.engineering.Config.SENSUS;
import static net.suntrans.engineering.Config.ST_SLC_10;
import static net.suntrans.engineering.Config.ST_SLC_6;

public class MainActivity extends BasedActivity {

    private MDNS mdns;

    private static JmDNS jmDNS;
    public static WifiManager.MulticastLock lock;
    public static ServiceListener listener;
    public static WifiManager wm = null;
    public static ServiceInfo info;


    private List<DeviceInfo> datas;
    private ActivityMainBinding binding;
    private MyAdapter adapter;


    static {
        lock = null;
        listener = null;
        info = null;
    }

    public static Map<String, DeviceInfo> findDeviceMap = new ConcurrentHashMap();
    private InetAddress localIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fixStatusBar();
        setUpRecyclerView();
        searchThreadStart();
        updateUI();

    }


    private void setUpRecyclerView() {
        datas = new ArrayList<>();
        adapter = new MyAdapter(R.layout.item_devices, datas);

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


            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (datas.get(position).Name.contains(ST_SLC_6)){
                    Intent intent = new Intent(MainActivity.this, SLC6ControlActivity.class);
                    intent.putExtra("type","4300");
                    intent.putExtra("ip",datas.get(position).IP);
                    intent.putExtra("port",datas.get(position).Port);
                    startActivity(intent);
                }else if (datas.get(position).Name.contains(ST_SLC_10)){
                    Intent intent = new Intent(MainActivity.this, SLC6ControlActivity.class);
                    intent.putExtra("type","4100");
                    intent.putExtra("ip",datas.get(position).IP);
                    intent.putExtra("port",datas.get(position).Port);
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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!stoped) {
//                    try {
//                        if (isStarted)
//                            return;
//                        if (jmDNS != null && localIpAddress != null) {
//                            startDeviceSearch();
//                            Thread.sleep(3000L);
//                            continue;
//                        }
//                        localIpAddress = getLocalIpAddress();
//                        if (jmDNS == null)
//                            jmDNS = JmDNS.create(localIpAddress);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        })
//                .start();

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

        listener = new SampleListener();
        jmDNS.addServiceListener("_easylink._tcp.local.", listener);
    }

    //更新UI
    private void updateUI() {
        RxTimerUtil.interval(3000, new RxTimerUtil.IRxNext() {
            @Override
            public void doNext(long number) {
               datas.clear();
                Iterator localIterator = findDeviceMap.entrySet().iterator();
                while (localIterator.hasNext()){
                    Map.Entry<String,DeviceInfo> next = (Map.Entry<String,DeviceInfo>)localIterator.next();
                    DeviceInfo value = next.getValue();
                    datas.add(value);
                }

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
           ImageView imageView =  helper.getView(R.id.imageView);
            helper.setText(R.id.name, item.Name)
                    .setText(R.id.ip, "IP:"+item.IP)
                    .setText(R.id.mac, "MAC:"+item.MAC);
            int resID = R.mipmap.ic_launcher;
            if (item.Name.contains(ST_SLC_10)){
                resID = R.drawable.ic_shitongdao;
            }else if (item.Name.contains(ST_SLC_6)){
                resID = R.drawable.ic_liutongdao;
            }else if (item.Name.contains(SENSUS)){
                resID  = R.drawable.diliugan;
            }

            Glide.with(MainActivity.this)
                    .load(resID)
                    .dontTransform()
                    .crossFade()
                    .override(size, size)
                    .into(imageView);
        }
    }


    class SampleListener
            implements ServiceListener, ServiceTypeListener {
        SampleListener() {
        }

        public void serviceAdded(ServiceEvent paramServiceEvent) {
//            info = jmDNS.getServiceInfo(EWM_SERVICE, paramServiceEvent.getName());
//            if (info != null) {
//                if (!findDeviceMap.containsKey(paramServiceEvent.getName()))
//                    findDeviceMap.put(paramServiceEvent.getName(), info);
//
//            }
        }

        public void serviceRemoved(ServiceEvent paramServiceEvent) {

        }

        public void serviceResolved(ServiceEvent paramServiceEvent) {
        }

        public void serviceTypeAdded(ServiceEvent paramServiceEvent) {
        }

        @Override
        public void subTypeForServiceTypeAdded(ServiceEvent event) {

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


    @Override
    protected void onDestroy() {
        stoped = true;
        RxTimerUtil.cancel();
        mdns.stopSearchDevices(new SearchDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                super.onSuccess(code, message);
            }
        });
        super.onDestroy();
    }
}

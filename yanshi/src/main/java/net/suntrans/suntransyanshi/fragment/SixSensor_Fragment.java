package net.suntrans.suntransyanshi.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import net.suntrans.suntransyanshi.YanshiApp;
import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.adapter.Decoration_env;
import net.suntrans.suntransyanshi.bean.SixSensorItem;
import net.suntrans.suntransyanshi.bean.SwitchItem;
import net.suntrans.suntransyanshi.sensus.Sixsensor_detail_activity;
import net.suntrans.suntransyanshi.tcp.ComTcpManager;
import net.suntrans.suntransyanshi.utils.Converts;
import net.suntrans.suntransyanshi.utils.LogUtil;
import net.suntrans.suntransyanshi.utils.SixSensorUtils;
import net.suntrans.suntransyanshi.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Looney on 2017/2/28.
 */

public class SixSensor_Fragment extends Fragment implements ComTcpManager.TcpListener {

    private static final String TAG = "SixSensor_Fragment";
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout refreshLayout;
    protected MyAdapter adapter;
    private ComTcpManager manager;

    private String addr;
    private String ip;
    private int port;
    SwitchItem item;

    public static SixSensor_Fragment newInstance(SwitchItem item) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", item);
        SixSensor_Fragment fragment = new SixSensor_Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ip = "";
//        Config config = Config.getInstance();
//        if (MainActivity.isWaiwang) {
//            ip = config.waiwangIp;
//            port = config.waiwangPort;
//        } else {
//            ip = config.sixsensorIp;
//            port = config.sixsensorPort;
//        }
//        addr = config.sixsensorAddr;
        item = getArguments().getParcelable("item");
        addr = item.getRSaddr();
        LogUtil.i(TAG, "oncreatview");
        return view;
    }

    private ProgressDialog connectDialog;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        connected(item.getBendiip(), Integer.valueOf(item.getBendiport()));
    }

    public void connected(String ip, int port) {
        connectDialog.show();
        manager = new ComTcpManager(ip, port);
        manager.connect();
        manager.setListener(this);
    }

    public void disconnected() {

        handler.removeCallbacksAndMessages(null);
        connectDialog.dismiss();
        manager.disConnect();
        manager = null;
    }

    private void initView(View view) {
        connectDialog = new ProgressDialog(getActivity());
        connectDialog.setMessage("正在连接...");
        connectDialog.setCancelable(false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        adapter = new MyAdapter(R.layout.meter_listview, getListDataSet());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new Decoration_env(getContext().getApplicationContext()));

        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

    }


    List<SixSensorItem> datas;

    private List<SixSensorItem> getListDataSet() {
        datas = new ArrayList<>();
        SixSensorItem item0 = new SixSensorItem();
        item0.setName("时间:");
        item0.setValue("null");
        item0.setImgRes(R.drawable.ic_time);
        datas.add(item0);


        SixSensorItem item1 = new SixSensorItem();
        item1.setName("PM1:");
        item1.setValue("null");
        item1.setImgRes(R.drawable.pm);
        datas.add(item1);

        SixSensorItem item2 = new SixSensorItem();
        item2.setName("PM10:");
        item2.setImgRes(R.drawable.pm);
        item2.setValue("null");
        datas.add(item2);
        SixSensorItem item3 = new SixSensorItem();
        item3.setName("PM2.5:");
        item3.setValue("null");
        item3.setImgRes(R.drawable.pm);
        datas.add(item3);

        SixSensorItem item4 = new SixSensorItem();
        item4.setName("甲醛:");
        item4.setValue("null");
        item4.setImgRes(R.drawable.smoke);
        datas.add(item4);

        SixSensorItem item5 = new SixSensorItem();
        item5.setName("烟雾:");
        item5.setValue("null");
        item5.setImgRes(R.drawable.smoke);
        datas.add(item5);

        SixSensorItem item6 = new SixSensorItem();
        item6.setName("温度:");
        item6.setValue("null");
        item6.setImgRes(R.drawable.tmp);
        datas.add(item6);

        SixSensorItem item7 = new SixSensorItem();
        item7.setName("湿度:");
        item7.setValue("null");
        item7.setImgRes(R.drawable.humidity);
        datas.add(item7);

        SixSensorItem item8 = new SixSensorItem();
        item8.setName("气压:");
        item8.setValue("null");
        item8.setImgRes(R.drawable.atm);
        datas.add(item8);

        SixSensorItem item9 = new SixSensorItem();
        item9.setName("光线强度:");
        item9.setImgRes(R.drawable.light);
        item9.setValue("null");
        datas.add(item9);

        SixSensorItem item10 = new SixSensorItem();
        item10.setName("人员信息:");
        item10.setImgRes(R.drawable.people);
        item10.setValue("null");
        datas.add(item10);


        SixSensorItem item11 = new SixSensorItem();
        item11.setName("x轴倾斜角:");
        item11.setImgRes(R.drawable.angle);
        item11.setValue("null");
        datas.add(item11);

        SixSensorItem item12 = new SixSensorItem();
        item12.setName("y轴倾斜角:");
        item12.setImgRes(R.drawable.angle);
        item12.setValue("null");
        datas.add(item12);

        SixSensorItem item13 = new SixSensorItem();
        item13.setName("z轴倾斜角:");
        item13.setValue("null");
        item13.setImgRes(R.drawable.angle);
        datas.add(item13);


        SixSensorItem item14 = new SixSensorItem();
        item14.setName("振动强度:");
        item14.setValue("null");
        item14.setImgRes(R.drawable.shake);
        datas.add(item14);

        return datas;
    }

    @Override
    public void onReceive(String result) {
        LogUtil.i(TAG, result);
        result = result.toLowerCase();
        if (((Sixsensor_detail_activity) getActivity()).flag.equals("bendi")) {
            if (!result.substring(0, 8).equals("ab68" + addr)) {
                return;
            }
        }

        handler.sendMessage(Message.obtain(handler, MSG_STATE, result));
    }

    @Override
    public void onError(int code, final String msg) {
        LogUtil.i(TAG, "code=" + code + ",msg=" + msg);

        handler.post(new Runnable() {
            @Override
            public void run() {
                connectDialog.dismiss();
                UiUtils.showToast(msg);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onconnected() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                connectDialog.dismiss();

            }
        });
        LogUtil.i(TAG, "已建立链接");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                getData();
            }
        }, 500);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    class MyAdapter extends BaseQuickAdapter<SixSensorItem, BaseViewHolder> {

        public MyAdapter(int layoutResId, List<SixSensorItem> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SixSensorItem item) {
            ImageView imageView = helper.getView(R.id.image);
            if (item.getEvaluate() != null) {
                helper.setText(R.id.value, item.getValue() + "(" + item.getEvaluate() + ")");
            } else {
                helper.setText(R.id.value, item.getValue());
            }
            helper.setText(R.id.name, item.getName());

            ((ImageView) helper.getView(R.id.image)).setImageResource(item.getImgRes());
            ((ImageView) helper.getView(R.id.image)).setColorFilter(YanshiApp.getApplication().getResources().getColor(R.color.colorPrimary));
        }

    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        manager.disConnect();
        super.onDestroyView();
    }

    private void getData() {
        handler.sendEmptyMessage(MSG_START_REFRESH);
        handler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 2000);
        String order = "ab68" + addr + "f003 0100 0011";
        order = getOrder(order);
        manager.send(order);

    }

    @NonNull
    private String getOrder(String order) {
        order = order.replace(" ", "");
        byte[] bytes = Converts.HexString2Bytes(order);
        String crc = Converts.GetCRC(bytes, 4, bytes.length);
        order = order + crc + "0d0a";
        return order;
    }


    private final int MSG_STATE = 1;
    public final int MSG_START_REFRESH = 2;
    public final int MSG_STOP_REFRESH = 3;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_STATE) {
                SixSensorUtils.parseSixSensorData(datas, (String) msg.obj);
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
            if (msg.what == MSG_START_REFRESH) {
                refreshLayout.setRefreshing(true);
            }
            if (msg.what == MSG_STOP_REFRESH) {
                refreshLayout.setRefreshing(false);
            }
        }
    };

    public void stopRfresh() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }
}

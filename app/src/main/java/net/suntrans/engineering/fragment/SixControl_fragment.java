package net.suntrans.engineering.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import net.suntrans.engineering.R;
import net.suntrans.engineering.bean.SixSwitchItem;
import net.suntrans.engineering.tcp.TcpHelper;
import net.suntrans.engineering.utils.Converts;
import net.suntrans.engineering.utils.LogUtil;
import net.suntrans.engineering.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Looney on 2017/2/28.
 */

public class SixControl_fragment extends Fragment implements TcpHelper.OnReceivedListener {
    private List<AsyncTask> tasks = new ArrayList<>();
    private static final String TAG = "SwitchControl_fragment";
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout refreshLayout;
    protected MyAdapter adapter;
    private List<String> addrs;


    private TcpHelper helper;

    private int port;
    private String ip;
    private ProgressDialog connectDialog;

    public static SixControl_fragment newInstance(ArrayList<SixSwitchItem> datas, String ip, int port) {
        SixControl_fragment fragment = new SixControl_fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("datas", datas);
        bundle.putString("ip", ip);
        bundle.putInt("port", port);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        connectDialog = new ProgressDialog(getActivity());
        connectDialog.setMessage("正在连接...");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        adapter = new MyAdapter(R.layout.item_devicelist, getListDataSet());
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                showFailedDialog();
                if (datas.get(position).getState().equals("0")) {
                    helper.binder.sendOrder(datas.get(position).getOpenCmd());
                } else {
                    helper.binder.sendOrder(datas.get(position).getCloseCmd());
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwitchState();
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        shutdownDialog = new ProgressDialog(getActivity());


        ip = getArguments().getString("ip");
        port = getArguments().getInt("port");
        connectToServer(ip, port);
    }


    List<SixSwitchItem> datas;

    private List<SixSwitchItem> getListDataSet() {
        datas = getArguments().getParcelableArrayList("datas");
        this.ip = getArguments().getString("ip");
        this.port = getArguments().getInt("port");
        addrs = new ArrayList<>();
        for (SixSwitchItem item :
                datas) {
            if (!addrs.contains(item.getRSaddr())) {
                addrs.add(item.getRSaddr());
            }
        }

        return datas;
    }

    @Override
    public void onReceive(String content) {
        LogUtil.i(TAG, content);
        if (content.equals("与服务器连接失败,重连中...") || content.equals("发送失败") || content.equals("连接中断")) {
            UiUtils.showToast(content);
            return;
        }
        parseSwitchData(content);
        handler.sendEmptyMessage(MSG_STOP_REFRESH);
    }

    public void disconnect() {
        dialog.dismiss();
        if (helper != null)
            helper.unRegister();
        helper = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected() {
        connectDialog.dismiss();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getSwitchState();
            }
        }, 300);
    }

    private void getSwitchState() {
        handler.sendEmptyMessage(MSG_START_REFRESH);
        handler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 2000);
        tasks.add(new GetDataTask().execute());
    }


    class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < addrs.size(); i++) {
                String addr = addrs.get(i);
                String order = "ab 68" + "43 00" + addr + "01" + "97 00" + "00 00 00 00";
//                String order = "aa68" + addr + "03 0100" + "0007";
                order = getOrder(order);
                helper.binder.sendOrder(order);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            handler.sendEmptyMessageDelayed(MSG_STOP_REFRESH, 2000);
            super.onPostExecute(s);
        }
    }

    private ProgressDialog shutdownDialog;

    class CloseAllTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < datas.size(); i++) {
                helper.binder.sendOrder(datas.get(i).getCloseCmd());
                publishProgress((i + 1) + "/" + datas.size());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String progress = values[0];
            super.onProgressUpdate(values);
            shutdownDialog.setMessage("发送命令(" + progress + ")");

        }

        @Override
        protected void onPostExecute(String s) {
            shutdownDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    class MyAdapter extends BaseQuickAdapter<SixSwitchItem, BaseViewHolder> {

        public MyAdapter(int layoutResId, List<SixSwitchItem> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SixSwitchItem item) {
            ImageView imageView = helper.getView(R.id.image);
            imageView.setImageResource(item.getState().equals("1") ? item.getOpImageId() : item.getCloseImageId());
            helper.setText(R.id.name, "通道" + item.getChannel() + "");
        }

    }

    private static final int MSG_STOP_REFRESH = 10;
    private static final int MSG_START_REFRESH = 11;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_STOP_REFRESH) {
                refreshLayout.setRefreshing(false);
            }
            if (msg.what == MSG_START_REFRESH) {
                refreshLayout.setRefreshing(true);
            }
        }
    };

    public void connectToServer(String ip, int port) {
        connectDialog.show();
        helper = new TcpHelper((AppCompatActivity) getActivity(), ip, port, null);
        helper.setOnReceivedListener(this);
    }

    @NonNull
    private String getOrder(String order) {
        order = order.replace(" ", "");
        byte[] bytes = Converts.HexString2Bytes(order);
        String crc = Converts.GetCRC(bytes, 2, bytes.length);
        order = order + crc + "0d0a";
        return order;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        helper.unRegister();
        handler.removeCallbacksAndMessages(null);
        for (AsyncTask task :
                tasks) {
            task.cancel(true);
        }
        tasks.clear();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close) {
            shutdownDialog.show();
            tasks.add(new CloseAllTask().execute());
        }
        return super.onOptionsItemSelected(item);
    }

    private int which1 = 100;//1表示成功 100表示成功界面显示完毕
    Handler handler2 = new Handler();
    ProgressDialog dialog;

    // 显示成功发送命令时候的dialog
    private void showSuccessDialog() {
        handler.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
        which1 = 1;
        dialog.setMessage("成功");
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing())
                    dialog.dismiss();
                which1 = 100;
            }
        }, 500);
    }

    // 显示点击按钮发送命令时候的dialog，2s后无回应则认为执行失败
    private void showFailedDialog() {
        dialog.setMessage("发送命令...");
        dialog.show();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage("失败,请检查网络");
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        which1 = 100;
                        adapter.notifyDataSetChanged();
                    }
                }, 500);
            }
        }, 2000);
    }

    private byte[] bits = {(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80};     //从1到8只有一位是1，用于按位与计算，获取某一位的值

    private void parseSwitchData(String s) {

        if (s.length() < 24)
            return;
        if (!s.substring(4, 8).equals("4300")) {
            return;
        }
        if (!s.substring(s.length()-4,s.length()).equals("0d0a")){
            return;
        }
        String s1 = s.substring(22, 24);
        String[] states = {"0", "0", "0", "0", "0", "0"};   //6个通道的状态，state[0]对应1通道
        byte[] a = Converts.HexString2Bytes(s1);
        for (int i = 0; i < 6; i++) {
            states[i] = ((a[0] & bits[i]) == bits[i]) ? "1" : "0";
        }
        for (int i = 0; i < datas.size(); i++) {
            int channel = Integer.valueOf(datas.get(i).getChannel());
            if (channel != 0) {
                datas.get(i).setState(states[channel - 1]);
            }
        }
        adapter.notifyDataSetChanged();
    }
}

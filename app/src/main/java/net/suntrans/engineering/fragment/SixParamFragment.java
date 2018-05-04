package net.suntrans.engineering.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import net.suntrans.engineering.R;
import net.suntrans.engineering.databinding.FragmentSixParamBinding;
import net.suntrans.engineering.tcp.TcpHelper;
import net.suntrans.engineering.utils.Converts;
import net.suntrans.engineering.utils.LogUtil;
import net.suntrans.engineering.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Looney on 2018/3/7.
 * Des:
 */

public class SixParamFragment extends Fragment implements TcpHelper.OnReceivedListener, View.OnClickListener {

    private FragmentSixParamBinding binding;

    public static SixParamFragment newInstance(String ip, int port) {

        Bundle args = new Bundle();
        args.putString("ip", ip);
        args.putInt("port", port);
        SixParamFragment fragment = new SixParamFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final String TAG = this.getClass().getSimpleName();

    private String openAllOrder = "AB 68 43 00 00 00 00 00 03 D2 00 3f 00 3f 00  c4 7e 0D 0A";
    private String closeAllOrder = "AB 68 43 00 00 00 00 00 03 D2 00 3f 00 00 00 d5 8e 0D 0A";


    private String readAllStateOrder = "43 00 " +
            "00 00 00 00 " +
            "01 " +
            "da 00" +
            "00000000" +
            "03 01" +
            "00 00 00 00" +
            "04 01" +
            "00 00 00 00" +
            "05 01" +
            "00 00 00 00" +
            "06 01" +
            "00 00 00 00" +
            "07 01" +
            "00 00 00 00" + "" +
            "08 01" +
            "00 00 00 00" +
            "d9 00" +
            "00000000" +
            "d8 00" +
            "00 00 00 00 " +
            "d5 00" +
            "00000000";
    String shinengOrder = "";
    private Map<String,String> maps = new HashMap<>();
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_six_param, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        String ip = getArguments().getString("ip");
        int port = getArguments().getInt("port");
        connectToServer(ip, port);

        binding.quanguangshineng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    helper.binder.sendOrder(openAllOrder);
                } else {
                    helper.binder.sendOrder(closeAllOrder);

                }
            }
        });

        binding.refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refreshlayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });


        binding.anjianshineng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shinengOrder = "4300 00000000" + "03" + "d500" + "3f00 0000";
                    shinengOrder = shinengOrder.replace(" ", "");
                    byte[] bytes = Converts.HexString2Bytes(shinengOrder);
                    String crc = Converts.GetCRC(bytes, 0, bytes.length);
                    shinengOrder = "ab68" + shinengOrder + crc + "0d0a";
                    helper.binder.sendOrder(shinengOrder);
                } else {
                    shinengOrder = "4300 00000000" + "03" + "d500" + "0000 0000";
                    shinengOrder = shinengOrder.replace(" ", "");
                    byte[] bytes = Converts.HexString2Bytes(shinengOrder);
                    String crc = Converts.GetCRC(bytes, 0, bytes.length);
                    shinengOrder = "ab68" + shinengOrder + crc + "0d0a";
                    helper.binder.sendOrder(shinengOrder);
                }
            }
        });

        binding.guoliu.setOnClickListener(this);
        binding.guoliu1.setOnClickListener(this);
        binding.guoliu2.setOnClickListener(this);
        binding.guoliu3.setOnClickListener(this);
        binding.guoliu4.setOnClickListener(this);
        binding.guoliu5.setOnClickListener(this);
        binding.guoliu6.setOnClickListener(this);
        binding.guoya.setOnClickListener(this);
        binding.qianya.setOnClickListener(this);

    }

    private void initData() {
        params.put("da00",0);
        params.put("0301",0);
        params.put("0401",0);
        params.put("0501",0);
        params.put("0601",0);
        params.put("0701",0);
        params.put("0801",0);
        params.put("d900",0);
        params.put("d800",0);
        params.put("d500",0);
    }

    private void getData() {
        readAllStateOrder = readAllStateOrder.replace(" ","");
        byte[] bytes = Converts.HexString2Bytes(readAllStateOrder);
        String crc = Converts.GetCRC(bytes, 0, bytes.length);

        helper.binder.sendOrder("ab68" + readAllStateOrder + crc + "0d0a");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disconnect();
    }

    private TcpHelper helper;

    public void connectToServer(String ip, int port) {
        helper = new TcpHelper((AppCompatActivity) getActivity(), ip, port, null);
        helper.setOnReceivedListener(this);
    }

    public void disconnect() {
        if (helper != null)
            helper.unRegister();
        helper = null;
    }


    private String formatValue(String value) {
        value = Integer.toHexString(Integer.valueOf(value));
        value = Converts.paddingHexString(value, 4);
        value = reserverHexString(value);
        return value;
    }

    public String reserverHexString(String hexString) {
        if (hexString.length() != 4) {
            return null;

        }
        return hexString.substring(2, 4) + hexString.substring(0, 2);
    }


    private Map<String,Integer> params = new HashMap<>();

    @Override
    public void onReceive(String content) {


        try {
            if (content.equals("与服务器连接失败,重连中...") || content.equals("发送失败") || content.equals("连接中断")) {
                UiUtils.showToast(content);
                return;
            }

            if (!content.substring(16, 18).equals("02") && !content.substring(16, 18).equals("04")) {
                return;
            }
            binding.refreshlayout.setRefreshing(false);
            String s = content.substring(18, content.length());

            int index = 0;
            for (int i = 0; i < 10; i++) {
                String key =s.substring(index,index+4);
                String param = s.substring(index + 4, index + 12);
                index += 12;
                String s1 = Converts.reverse32HexString(param);
                params.put(key,Integer.parseInt(s1, 16));
            }

            updateUI();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateUI() {
        binding.guoliu.setText(params.get("da00") / 100 + "");
        binding.guoliu1.setText(params.get("0301") / 100 + "");
        binding.guoliu2.setText(params.get("0401") / 100 + "");
        binding.guoliu3.setText(params.get("0501") / 100 + "");
        binding.guoliu4.setText(params.get("0601") / 100 + "");
        binding.guoliu5.setText(params.get("0701") / 100 + "");
        binding.guoliu6.setText(params.get("0801") / 100 + "");
        binding.guoya.setText(params.get("d900") / 10 + "");
        binding.qianya.setText(params.get("d800") / 10 + "");
        if (params.get("d500") == 63) {
            binding.anjianshineng.setChecked(true);
        } else {
            binding.anjianshineng.setChecked(false);

        }

    }

    @Override
    public void onConnected() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();

            }
        }, 500);
    }

    private String currentModifyAddr="";
    @Override
    public void onClick(final View v) {
        QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getContext());
        final EditText editText = builder.getEditText();
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        String order = "";
        switch (v.getId()) {
            case R.id.guoliu:
                builder.setTitle("总过流");
                editText.setText(binding.guoliu.getText().toString());
                currentModifyAddr="da00";
                break;
            case R.id.guoliu1:
                builder.setTitle("通道1过流");
                editText.setText(binding.guoliu1.getText().toString());
                currentModifyAddr="0301";
                break;
            case R.id.guoliu2:
                builder.setTitle("通道2过流");
                editText.setText(binding.guoliu2.getText().toString());
                currentModifyAddr="0401";

                break;
            case R.id.guoliu3:
                builder.setTitle("通道3过流");
                editText.setText(binding.guoliu3.getText().toString());
                currentModifyAddr="0501";

                break;
            case R.id.guoliu4:
                builder.setTitle("通道4过流");
                editText.setText(binding.guoliu4.getText().toString());
                currentModifyAddr="0601";

                break;
            case R.id.guoliu5:
                builder.setTitle("通道5过流");
                editText.setText(binding.guoliu5.getText().toString());
                currentModifyAddr="0701";

                break;
            case R.id.guoliu6:
                builder.setTitle("通道6过流");
                editText.setText(binding.guoliu6.getText().toString());
                currentModifyAddr="0801";

                break;
            case R.id.guoya:
                builder.setTitle("过压");
                editText.setText(binding.guoya.getText().toString());
                currentModifyAddr="d900";

                break;
            case R.id.qianya:
                builder.setTitle("欠压");
                editText.setText(binding.qianya.getText().toString());
                currentModifyAddr="d800";
                break;
        }
        builder.addAction(R.string.cancel, new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });

        builder.addAction(R.string.ok, new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                String s = editText.getText().toString();
                if (!s.matches("[0-9]+")) {
                    UiUtils.showToast("请输入数字");
                    return;
                }
                dialog.dismiss();
                switch (v.getId()) {
                    case R.id.guoliu:
                        params.put("da00",Integer.valueOf(s)*100);
                        break;
                    case R.id.guoliu1:
                        params.put("0301",Integer.valueOf(s)*100);
                        break;
                    case R.id.guoliu2:
                        params.put("0401",Integer.valueOf(s)*100);

                        break;
                    case R.id.guoliu3:
                        params.put("0501",Integer.valueOf(s)*100);

                        break;
                    case R.id.guoliu4:
                        params.put("0601",Integer.valueOf(s)*100);

                        break;
                    case R.id.guoliu5:
                        params.put("0701",Integer.valueOf(s)*100);

                        break;
                    case R.id.guoliu6:
                        params.put("0801",Integer.valueOf(s)*100);

                        break;
                    case R.id.guoya:
                        params.put("d900",Integer.valueOf(s)*10);

                        break;
                    case R.id.qianya:
                        params.put("d800",Integer.valueOf(s)*10);
                        break;
                }
                updateUI();
                sendParam();
            }
        });

        builder.create().show();
    }

    private void sendParam() {

//
//        String guoliu = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("da00")), 8));
//        String guoliu1 = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("0301")), 8));
//        String guoliu2 = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("0401")), 8));
//        String guoliu3 = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("0501")), 8));
//        String guoliu4 = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("0601")), 8));
//        String guoliu5 = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("0701")), 8));
//        String guoliu6 = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("0801")), 8));
//        String guoya = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("d900")), 8));
//        String qianya = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("d800")), 8));
//        String shineng = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get("d500")), 8));
//
        StringBuilder sb = new StringBuilder();
//        sb.append("4300")
//                .append("00 00 00 00")
//                .append("03")
//                .append("da00")
//                .append(guoliu)
//                .append("0301")
//                .append(guoliu1)
//                .append("0401")
//                .append(guoliu2)
//                .append("0501")
//                .append(guoliu3)
//                .append("0601")
//                .append(guoliu4)
//                .append("0701")
//                .append(guoliu5)
//                .append("0801")
//                .append(guoliu6)
//                .append("d900")
//                .append(guoya)
//                .append("d800")
//                .append(qianya);
//                .append("d500")
//                .append(shineng);

        String param = Converts.reverse32HexString(Converts.paddingHexString(Integer.toHexString(params.get(currentModifyAddr)), 8));
        sb.append("4300")
                .append("00 00 00 00")
                .append("03")
                .append(currentModifyAddr)
                .append(param);

        String order = Converts.getOrderWithCrc(sb.toString());
        helper.binder.sendOrder(order);

    }
}

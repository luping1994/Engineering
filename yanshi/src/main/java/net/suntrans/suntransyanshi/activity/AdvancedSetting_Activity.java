package net.suntrans.suntransyanshi.activity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.api.RetrofitHelper;
import net.suntrans.suntransyanshi.bean.Config_EWM;
import net.suntrans.suntransyanshi.databinding.ActivityAdvancedsettingBinding;
import net.suntrans.suntransyanshi.utils.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;


/**
 * Created by Looney on 2017/3/30.
 */

public class AdvancedSetting_Activity extends AppCompatActivity {
    private static final String TAG = "WebViewTest";
    WebView webView;
    final String js = "<script language=\"JavaScript\">"
            + "function getdata() {"
            + "var x = document.getElementsByTagName('form')[0];"
            + "var s;"
            + "for (var i = 0; i < x.length; i++) {"
            + "s +='\"'+ x.elements[i].name +'\":\"' + x.elements[i].value+'\",'"
            + "}"
            + "s='{'+s+'}';"
            + "return s;"
            + "}"
            + "</script>";
    private ActivityAdvancedsettingBinding binding;
    private Spinner spinner;
    private String ip;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(AdvancedSetting_Activity.this, R.layout.activity_advancedsetting);
        setUpToolBar();
        spinner = (Spinner) findViewById(R.id.wifimode);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (item != null) {
                    item.wifi_mode = (position + 1) + "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer YWRtaW46YWRtaW4=");
        String s = "";
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

        });
        errorpage = (RelativeLayout) findViewById(R.id.errorpage);
        loadingpage= (RelativeLayout) findViewById(R.id.loadingpage);
        content = (NestedScrollView) findViewById(R.id.content);
        btError  = (Button) findViewById(R.id.bt_error);
        btError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        getData();
    }

    public void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.advancedsetting);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    final class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.loadUrl("javascript:window.java_obj.getSource(getdata())");
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
    }

    Config_EWM item;

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void getSource(String html) {
            String data = html.replace("undefined", "");
            data = data.substring(0, data.length() - 2);
            data += "}";
            try {
                LogUtil.i(data);
                item = new Gson().fromJson(data, Config_EWM.class);
                LogUtil.e(item.toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        binding.setConfig(item);
                        setSuccessState();
                        try {
                            spinner.setSelection(Integer.valueOf(item.wifi_mode) - 1);
                        } catch (Exception IOe) {
                            IOe.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



    @Override
    protected void onDestroy() {
        webView.destroy();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diveceset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.commit) {
            new AlertDialog.Builder(AdvancedSetting_Activity.this)
                    .setMessage(getResources().getString(R.string.alert_isconfig))
                    .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            postTo();
                        }
                    }).setNegativeButton(getString(R.string.cancel),null).create().show();
            return true;
        }
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postTo() {
        LogUtil.i(item.wifi_ssid);
        Map<String, String> map = null;
        map = new LinkedHashMap<>();
        map.put("wifi_mode",item.wifi_mode);
        map.put("wifi_ssid",item.wifi_ssid);
        map.put("security_mode",item.security_mode);
        map.put("wifi_key",item.wifi_key);
        map.put("wifi_ssid1",item.wifi_ssid1);
        map.put("security_mode1",item.security_mode1);
        map.put("wifi_key1",item.wifi_key1);
        map.put("wifi_ssid2",item.wifi_ssid2);
        map.put("security_mode2",item.security_mode2);
        map.put("wifi_key2",item.wifi_key2);
        map.put("wifi_ssid3",item.wifi_ssid3);
        map.put("security_mode3",item.security_mode3);
        map.put("wifi_key3",item.wifi_key3);
        map.put("wifi_ssid4",item.wifi_ssid4);
        map.put("security_mode4",item.security_mode4);
        map.put("wifi_key4",item.wifi_key4);
        map.put("uap_ssid",item.uap_ssid);
        map.put("uap_secmode",item.uap_secmode);
        map.put("uap_key",item.uap_key);
        map.put("dhcp_enalbe",item.dhcp_enalbe);
        map.put("local_ip_addr",item.local_ip_addr);
        map.put("netmask",item.netmask);
        map.put("gateway_ip_addr",item.gateway_ip_addr);
        map.put("dns_server",item.dns_server);
        map.put("mstype",item.mstype);
        map.put("remote_server_mode",item.remote_server_mode);
        map.put("remote_dns",item.remote_dns);
        map.put("rport",item.rport);
        map.put("lport",item.lport);
        map.put("estype",item.estype);
        map.put("esaddr",item.esaddr);
        map.put("esrport",item.esrport);
        map.put("eslport",item.eslport);
        map.put("baudrate",item.baudrate);
        map.put("parity",item.parity);
        map.put("data_length",item.data_length);
        map.put("stop_bits",item.stop_bits);
        map.put("cts_rts_enalbe",item.cts_rts_enalbe);
        map.put("dma_buffer_size",item.dma_buffer_size);
        map.put("uart_trans_mode",item.uart_trans_mode);
        map.put("device_num",item.device_num);
        map.put("ps_mode",item.ps_mode);
        map.put("tx_power",item.tx_power);
        map.put("keepalive_num",item.keepalive_num);
        map.put("keepalive_time",item.keepalive_time);
        map.put("socks_type",item.socks_type);
        map.put("socks_addr",item.socks_addr);
        map.put("socks_port",item.socks_port);
        map.put("socks_user",item.socks_user);
        map.put("socks_pass",item.socks_pass);
        map.put("socks_1",item.socks_1);
        map.put("socks_2",item.socks_2);
        map.put("web_user",item.web_user);
        map.put("web_pass",item.web_pass);
        map.put("cld_id",item.cld_id);
        map.put("cld_key",item.cld_key);
        map.put("cld_apikey",item.cld_apikey);
        map.put("device_name",item.device_name);
        map.put("roam_val",item.roam_val);
        map.put("udp_enable",item.udp_enable);
        map.put("ccode",item.ccode);
        map.put("save","Save");

        if (map == null) {
            return;
        }
        RetrofitHelper.getApi("http://" + ip)
                .etEwmSetting(map)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LogUtil.i(response.message());
                        try {
                            String body = response.body().string();
                            if (body != null) {
                                LogUtil.i(body);
                                if (body.contains("Save Config Done!")){
                                    Message ms = new Message();
                                    ms.obj="Save Config Done!";
                                    ms.what=CONFIG_SUCCESS;
                                    handler.sendMessage(ms);
                                }else {
                                    Message ms = new Message();
                                    ms.obj="Failed!";
                                    ms.what=CONFIG_ERROR;
                                    handler.sendMessage(ms);
                                }
                            } else {
                                Message ms = new Message();
                                ms.obj="Failed!";
                                ms.what=CONFIG_ERROR;
                                handler.sendMessage(ms);
                                int code = response.code();
                                if (code == 401) {
                                    //身份验证错误
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Message ms = new Message();
                        ms.obj="Failed!";
                        ms.what=CONFIG_ERROR;
                        handler.sendMessage(ms);
                    }
                });


    }


    private void getData() {
        setLoadingState();
        ip = getIntent().getStringExtra("ip");
        RetrofitHelper.getApi("http://" + ip)
                .getEwmSetting()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LogUtil.i(response.message());
                        try {
                            String body = response.body().string();
                            if (body != null) {
//                                LogUtil.i(response.body().string());
                                displayHtml(body);
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setErrorPage();
                                    }
                                });
                                int code = response.code();
                                if (code == 401) {
                                    //身份验证错误
                                }
                            }
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setErrorPage();
                                }
                            });
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setErrorPage();
                            }
                        });
                        if (e instanceof HttpException) {
                            ResponseBody body = ((HttpException) e).response().errorBody();
                            try {
                                LogUtil.e(TAG, body.string());
                                String errorMsg = body.string();

                                if (errorMsg != null) {
                                }
                            } catch (IOException IOe) {
                                IOe.printStackTrace();
                            }
                        }
                    }
                });

    }

    private void displayHtml(String body) {
        Document document = Jsoup.parse(body);

//        document.getElementsByTag("head").get(0).append("<link rel=\"stylesheet\" href=\"file:///android_asset/emconfig.css\"/>");
//        document.getElementsByTag("head").get(0).append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />");
        document.getElementsByTag("head").get(0).append(js);

        document.getElementsByTag("form").get(0).attr("class", "item-content");
        body = document.outerHtml();

//        Document document1 =Jsoup.parse(body);
//
//        Element element =  document1.getElementsByTag("form").get(0);
        webView.loadDataWithBaseURL(null, body, "text/html", "utf-8",
                null);
//        webView.loadData(body,"text/html","gb2312");
    }

    public static Map<String, String> toMap(Object javaBean) {
        Map<String, String> result = new LinkedHashMap<>();
        Method[] methods = javaBean.getClass().getDeclaredMethods();

        for (Method method : methods) {
            try {
                if (method.getName().startsWith("get")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);

                    Object value = method.invoke(javaBean, (Object[]) null);
                    result.put(field, null == value ? "" : value.toString());
                }
            } catch (Exception e) {
            }
        }

        return result;
    }

    private final int CONFIG_SUCCESS=1;
    private final int CONFIG_ERROR=0;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONFIG_SUCCESS:
                    String message = (String) msg.obj;
                    new AlertDialog.Builder(AdvancedSetting_Activity.this)
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).create().show();
                    break;
                case CONFIG_ERROR:
                    String message1= (String) msg.obj;

                    new AlertDialog.Builder(AdvancedSetting_Activity.this)
                            .setMessage(message1)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                    break;
            }
        }
    };



    private void setLoadingState() {
        loadingpage.setVisibility(View.VISIBLE);
        content.setVisibility(View.INVISIBLE);
        errorpage.setVisibility(View.INVISIBLE);
    }

    private void setSuccessState() {
        loadingpage.setVisibility(View.INVISIBLE);
        content.setVisibility(View.VISIBLE);
        errorpage.setVisibility(View.INVISIBLE);
    }

    private void setErrorPage() {
        loadingpage.setVisibility(View.INVISIBLE);
        content.setVisibility(View.INVISIBLE);
        errorpage.setVisibility(View.VISIBLE);
    }
    RelativeLayout errorpage;
    RelativeLayout loadingpage;
    NestedScrollView content;
    Button btError;
}

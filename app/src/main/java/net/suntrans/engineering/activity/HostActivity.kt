package net.suntrans.engineering.activity

import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import net.suntrans.building.BasedActivity
import net.suntrans.engineering.R
import net.suntrans.engineering.api.RetrofitHelper
import net.suntrans.engineering.bean.ProConfig
import net.suntrans.engineering.tcp.TcpHelper
import net.suntrans.engineering.utils.Converts
import net.suntrans.engineering.utils.LogUtil
import net.suntrans.engineering.utils.UiUtils
import net.suntrans.looney.widgets.LoadingDialog
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by Looney on 2018/3/2.
 * Des:
 */

class HostActivity : BasedActivity(), TcpHelper.OnReceivedListener {
    private var titleTx: TextView? = null
    private var subscribe: Subscription? = null
    private var adapter: ArrayAdapter<String>? = null
    private var hostName: MutableList<String>? = null
    private var spinner: Spinner? = null
    private var data: List<ProConfig.DataBean>? = null
    private var helper: TcpHelper? = null
    private var type: String? = null


    private var dialog: LoadingDialog? = null

    //wifi模块重启命令
    private val rebootOrder = "AB 68 43 00 00 00 00 00 03 03 00 04 00 00 00 CA EB 0D 0A"
    private var order: String? = null


    private var refreshLayout: SwipeRefreshLayout? = null;
    private var selectedPos = 0

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        fixStatusBar()
        findViewById<View>(R.id.back)
                .setOnClickListener { finish() }

        titleTx = findViewById(R.id.title)
        val title = intent.getStringExtra("title")
        type = intent.getStringExtra("type")
        titleTx!!.text = title

        spinner = findViewById(R.id.spinner)
        refreshLayout = findViewById(R.id.refreshlayout)
        hostName = ArrayList()
        adapter = ArrayAdapter(this, R.layout.item_spinner, hostName!!)
        val ip = intent.getStringExtra("ip")
        val port = intent.getIntExtra("port", -1)
        connectToServer(ip, port)
        spinner!!.adapter = adapter
        findViewById<View>(R.id.save)
                .setOnClickListener(View.OnClickListener {
                    if (selectedPos == 0) {
                        UiUtils.showToast(getString(R.string.title_choose_project))
                        return@OnClickListener
                    }
                    AlertDialog.Builder(this@HostActivity)
                            .setMessage(String.format(getString(R.string.tips_host_setting), hostName!![selectedPos]))
                            .setPositiveButton(getString(R.string.ok)) { dialog1, which ->
                                if (dialog == null) {
                                    dialog = LoadingDialog(this@HostActivity)
                                    dialog!!.setWaitText(getString(R.string.tips_setting))
                                    dialog!!.setCancelable(true)
                                    setRemoteIpPort()
                                }
                                dialog!!.show()
                            }
                            .setNegativeButton(getString(R.string.cancel), null)
                            .create().show()
                })

        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedPos = position
                println(selectedPos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        refreshLayout!!.setOnRefreshListener {
            getData()
        }
    }

    //发送设置远程服务器socket的命令
    private fun setRemoteIpPort() {
        order = "43 00 00 00 00 00 03"
        var ipHex = ""
        var portHex = ""
        var ipLength = ""

        var ip = ""

        var deviceBean = data!![selectedPos - 1].sub.filter {
            it.dev_code == type
        }
        if (deviceBean.isNotEmpty()) {
            ip = deviceBean[0].host
            portHex = Integer.toHexString(deviceBean[0].port)
        }else{
            UiUtils.showToast("无法获取远程服务器地址")
            return
        }

//        ip = "192.168.0.127"
//        portHex = Integer.toHexString(9001)

        LogUtil.i("IP:" + ip)
        portHex = getStringFormat(portHex, 8)
        ipHex = Converts.strToASCIIHex(ip)

        ipLength = getStringFormat(Integer.toHexString(ip.length), 2)


        order = order!! + "09 00"//09 00  socket1   // 0a 00  socket2
        order += ipLength//域名长度
        order += ipHex
        order += portHex

        order = Converts.getOrderWithCrc(order)

        order = order!!.toLowerCase()
        if (helper!!.binder != null)
            helper!!.binder.sendOrder(order)

    }

    private fun getStringFormat(str: String, targetLength: Int): String {
        val sb = StringBuilder()
        for (i in 0 until targetLength - str.length) {
            sb.append("0")
        }
        sb.append(str)
        return sb.toString()
    }

    fun connectToServer(ip: String, port: Int) {

        helper = TcpHelper(this, ip, port, null)
        helper!!.setOnReceivedListener(this)
    }


    override fun onResume() {
        super.onResume()
        getData()
    }


    private fun getData() {
        if (subscribe != null) {
            if (!subscribe!!.isUnsubscribed) {
                subscribe!!.unsubscribe()
            }
        }
        subscribe = RetrofitHelper.getApi()
                .proConfig
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ProConfig>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        refreshLayout!!.isRefreshing = false
                        if (e.localizedMessage != null) {
                            UiUtils.showToast(e.localizedMessage)
                        }

                    }

                    override fun onNext(proConfig: ProConfig) {
                        refreshLayout!!.isRefreshing = false

                        data = proConfig.data
                        hostName!!.clear()
                        hostName!!.add("请选择工程")
                        for (su in data!!) {
                            hostName!!.add(su.name)
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                })
    }


    fun disconnect() {
        if (helper != null)
            helper!!.unRegister()
        helper = null
    }

    override fun onDestroy() {
        if (!subscribe!!.isUnsubscribed) {
            subscribe!!.unsubscribe()
        }
        handler.removeCallbacksAndMessages(null)
        disconnect()
        super.onDestroy()
    }

    override fun onReceive(content: String?) {
        if (content == null) {
            return
        }
        LogUtil.i(TAG, content)

        //ab6843000000000003090018b339b15ebe3dfb47ab3de61b1f2d63a51bb4702e63bb900004651f9ad0d0a
        //ab6843000000000003090018b339b15ebe3dfb47ab3de61b1f2d63a51bb4702e63bb900004651f9ad0d00d0a
        //ab6843000000000003090018b339b15ebe3dfb47ab3de61b1f2d63a51bb4702e63bb900004651f9ad0d00d0a
        //        if (Config.CODE_ST_SLC_10.equals(type)){
        //            if (content==null){
        //                return;
        //            }
        //            if (content.length()==(order.length()+3)){
        //                if (dialog!=null){
        //                    dialog.dismiss();
        //                }
        //                UiUtils.showToast("命令已发送");
        //
        //            }
        //        }else {
        //            if (content.equals(order)){
        //                if (dialog!=null){
        //                    dialog.dismiss();
        //                }
        //                handler.postDelayed(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        helper.binder.sendOrder(rebootOrder);
        //
        //                    }
        //                },500);
        //            }
        //        }

        try {
            if (dialog != null)
                dialog!!.dismiss()
            if (content != null && order!!.length == content.length
                    && order!!.substring(order!!.length - 4, order!!.length) == content.substring(content.length - 4, content.length)) {
                handler.postDelayed({
                    if (helper!!.binder != null)
                        helper!!.binder.sendOrder(rebootOrder)
                }, 500)
                UiUtils.showToast(getString(R.string.tips_cmd_sent))

            }

            //ab684300000000000309000e3138332e3233362e32352e3139300000238df2a20d0a
            //ab684300000000000309000e3138332e3233362e32352e3139300000238df2a20d0a
            if (content == "与服务器连接失败,重连中..." || content == "发送失败" || content == "连接中断") {
                UiUtils.showToast(content)
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnected() {}

    companion object {

        private val TAG = "HostActivity"
    }
}

package net.suntrans.suntransyanshi.tcp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Looney on 2017/1/23.
 */

public class TcpHelper {
    private final AppCompatActivity activity;
    public   TcpService.ibinder binder;
     ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (TcpService.ibinder) service;
            onReceivedListener.onConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public TcpHelper(AppCompatActivity activity, String ip, int port, String checkCode){
        this.activity = activity;
        IntentFilter filter = new IntentFilter("net.suntrans.www");
        this.activity.registerReceiver(broadcastReceiver, filter);


        Intent intent = new Intent(activity.getApplicationContext(), TcpService.class);    //指定要绑定的service
        intent.putExtra("ip", ip);
        intent.putExtra("port", port);
        if (checkCode!=null){
            intent.putExtra("checkCode",checkCode);
        }
        this. activity.bindService(intent, connection, Context.BIND_AUTO_CREATE);   //绑定主service


    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra("content");
            onReceivedListener.onReceive(content);
        }
    };

    public void unRegister(){
        this.activity.unregisterReceiver(broadcastReceiver);
        this.activity.unbindService(connection);
    }


    public void setOnReceivedListener(OnReceivedListener onReceivedListener) {
        this.onReceivedListener = onReceivedListener;
    }

    OnReceivedListener onReceivedListener;
    public interface OnReceivedListener{
        void onReceive(String content);
        void onConnected();

    }
}

package net.suntrans.suntransyanshi.tcp;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.suntrans.suntransyanshi.utils.LogUtil;


/**
 * Created by Looney on 2017/1/20.
 */
public class TcpService extends RxService {
    private ibinder binder;
    private String ip;
    private int port;
    private TcpManager manager;
    private String checkCode;
    private static final String TAG = "TcpService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG,"绑定服务");
        ip = intent.getStringExtra("ip");
        port = intent.getIntExtra("port",-1);
        checkCode = intent.getStringExtra("checkCode");
        LogUtil.i("ip="+ip+",port="+port);
        manager = new TcpManager(ip,port,this);
        if (checkCode!=null)
                manager.setCheckCode(checkCode);
      new Thread(){
          @Override
          public void run() {
              manager.connect();
          }
      }.start();
        manager.setListener(new TCPListener());
        binder = new ibinder() {
            @Override
            public boolean sendOrder(String order) {
                manager.send(order);
                return true;
            }
        };
        return binder;
    }
    Intent intent = new Intent();
    class TCPListener implements TcpManager.TcpListener{
        @Override
        public void onReceive(String result) {
            intent.putExtra("content",result);
            intent.setAction("net.suntrans.www");
            sendBroadcast(intent);
        }

        @Override
        public void onError(int code, String msg) {
            intent.putExtra("content",msg);
            intent.setAction("net.suntrans.www");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.disConnect();
        LogUtil.i(TAG+"服务销毁");
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    public abstract class ibinder extends Binder {
        /****
         * 发送命令
         * @param order    控制命令内容，从控制子地址开始，到校验码之前
         * @return  发送成功返回true，失败返回false
         */
        public abstract boolean sendOrder(String order);
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
}

package net.suntrans.engineering.usbTranslate;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.content.Context;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Looney on 2018/8/7.
 * Des:
 */
public class UsbTranslateService extends Service {
    private static final String TAG = "UsbTranslateService";
    private IBinder binder;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private HashMap<String, UsbDevice> deviceList;  //设备列表
    private UsbManager usbManager;  //USB管理器:负责管理USB设备的类
    private UsbDevice usbDevice;   //找到的USB设备
    private UsbInterface usbInterface;  //代表USB设备的一个接口
    private UsbDeviceConnection deviceConnection;  //USB连接的一个类。用此连接可以向USB设备发送和接收数据，这里我们使用这个类下面的块传输方式
    private UsbEndpoint usbEpIn;  //代表一个接口的某个节点的类:读数据节点
    private UsbEndpoint usbEpOut;  //代表一个接口的某个节点的类:写数据节点
    private PendingIntent intent; //意图

    private byte[] sendBytes;  //要发送信息字节
    private byte[] receiveBytes;  //接收到的信息字节

    @Override
    public void onCreate() {
        initUsbDevice();
        intent = PendingIntent.getBroadcast(this,0,new Intent(ACTION_USB_PERMISSION),0);
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        super.onCreate();
    }

    //取消广播
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        binder = new UsbBinder(){
            @Override
            public boolean send(String msg) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Log.e(TAG, usbDevice.getDeviceName());
//                        Log.e(TAG, usbEpIn.getEndpointNumber() + "------" + usbEpOut.getEndpointNumber());

                        Log.e(TAG, deviceConnection == null ? "true" : "false");
                        byte[] sendData = new byte[4];
                        sendData[0] = (byte) 0x31;
                        sendData[1] = (byte) 0x32;
                        sendData[2] = (byte) 0x33;
                        sendData[3] = (byte) 0x34;

                        Log.e(TAG, sendData[0] + "");
                        int result = deviceConnection.bulkTransfer(usbEpOut, sendData, sendData.length, 3000);
                        Log.e(TAG, "发送状态码：" + result);

                        receiveBytes = new byte[1024];
                        result = deviceConnection.bulkTransfer(usbEpIn, receiveBytes, receiveBytes.length, 3000);
                        Log.e(TAG, "接受状态码：" + result);
                        Log.e(TAG, receiveBytes[0] + "" );

                        deviceConnection.releaseInterface(usbInterface);
                    }
                }).start();

                return true;
            }
        };
        return binder;
    }

  public class UsbBinder extends Binder{
        public boolean send(String msg){
            return  true;
        }

    }

    //初始化USB设备
    private void initUsbDevice() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            //找到指定的设备
            if (device.getVendorId() == 8584 && device.getProductId() == 2785) {
                usbDevice = device;
                Log.e(TAG, "找到设备"+device.getDeviceName());
            }
        }
        findInterface();
    }
    private void findInterface() {

        if (usbDevice == null) {
            Log.e(TAG, "没有找到设备");
            return;
        }

        for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            //一个设备上面一般只有一个接口，有两个端点，分别接受和发送数据
            UsbInterface uInterface = usbDevice.getInterface(i);
            usbInterface = uInterface;
            Log.e(TAG, usbInterface.toString());
            break;
        }

        getEndpoint(usbInterface);

        if (usbInterface != null) {
            UsbDeviceConnection connection = null;
            //判断是否有权限
            if (usbManager.hasPermission(usbDevice)) {
                Log.e(TAG, "已经获得权限");
                connection = usbManager.openDevice(usbDevice);
                Log.e(TAG, connection == null ? "true" : "false");
                if (connection == null) {
                    Log.e(TAG, "设备连接为空");
                    return;
                }
                if (connection != null && connection.claimInterface(usbInterface, true)) {
                    deviceConnection = connection;
                    Log.e(TAG, deviceConnection == null ? "true" : "false");
                } else {
                    connection.close();
                }

            } else {
                Log.e(TAG, "正在获取权限...");
                usbManager.requestPermission(usbDevice, intent);
                if (usbManager.hasPermission(usbDevice)) {
                    Log.e(TAG, "获取权限");
                } else {
                    Log.e(TAG, "没有权限");
                }

            }
        } else {
            Log.e(TAG, "没有找到接口");
        }
    }
    //获取端点
    private void getEndpoint (UsbInterface usbInterface) {
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    usbEpOut = ep;
                    Log.e(TAG, "获取发送数据的端点");
                } else {
                    usbEpIn = ep;
                    Log.e(TAG, "获取接受数据的端点");
                }
            }
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, intent.getAction());
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                Log.e("granted", granted + "");
            }
        }
    };

}

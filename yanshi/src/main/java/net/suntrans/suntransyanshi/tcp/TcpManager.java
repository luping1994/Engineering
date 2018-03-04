package net.suntrans.suntransyanshi.tcp;


import android.os.Handler;

import net.suntrans.suntransyanshi.utils.Converts;
import net.suntrans.suntransyanshi.utils.LogUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.client.TcpClient;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Looney on 2017/1/20.
 */

public class TcpManager {
    private final String TAG = "TcpManager";
    private final RxService service;
    private Connection<byte[], byte[]> mConnection;
    private int spacingTime =5;
    private String ip;
    private int port;
    private boolean isExit = false;
    private Subscription sp;
    private Subscription spClient;
    private Subscription spInput;


    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public TcpManager(String ip, int port, RxService service) {
        this.port = port;
        this.ip = ip;
//        try {
//            InetAddress inetAddress = InetAddress.getByName(ip);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        this.service = service;
    }

    private String checkCode;


    public void connect() {
        if (mConnection != null)
            return;
        spClient = TcpClient.newClient(ip, port)
                .<byte[], byte[]>pipelineConfigurator(new Action1<ChannelPipeline>() {
                    @Override
                    public void call(ChannelPipeline entries) {
                        entries.addLast(new ByteArrayEncoder());
                        entries.addLast(new ByteArrayDecoder());
                    }
                })
                .channelOption(ChannelOption.SO_KEEPALIVE, true)
                .readTimeOut(3000, TimeUnit.SECONDS)
                .createConnectionRequest()
                .compose(service.<Connection<byte[],byte[]>>bindUntilEvent(ServiceEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Connection<byte[], byte[]>>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("连接onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtil.i(TAG + "连接失败!");
//                        listener.onError(CONNECT_FAILED,"连接失败!");
                        reconnect();
                    }

                    @Override
                    public void onNext(Connection<byte[], byte[]> connection) {
                        LogUtil.i(TAG, "TCP连接成功!==>(ip=" + ip + ",port=" + port + ")");
                        mConnection = connection;
                        if (checkCode != null)
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    send(checkCode);
                                }
                            }, 300);
                        if (spInput != null) {
                            System.out.println("spinput是否解除订阅:" + spInput.isUnsubscribed());
                            if (!spInput.isUnsubscribed())
                                spInput.unsubscribe();
                            System.out.println("spinput是否解除订阅:" + spInput.isUnsubscribed());
                            spInput = null;
                        }
                        spInput = mConnection.getInput()
                                .compose(service.<byte[]>bindUntilEvent(ServiceEvent.DESTROY))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<byte[]>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        LogUtil.i(TAG, "连接中断");
                                        listener.onError(CONNECT_BREAK, "连接中断");
                                        mConnection.closeNow();
                                        mConnection = null;
                                        reconnect();
                                    }

                                    @Override
                                    public void onNext(byte[] bytes) {

                                        String s = Converts.Bytes2HexString(bytes);
                                        s=s.toLowerCase();
                                        String[] ss = s.split("0d0a");
                                        for (int i=0;i<ss.length;i++){
                                            listener.onReceive(ss[i]+"0d0a");

                                        }
                                    }
                                });
                    }
                });

    }


    private void reconnect() {
        if (!isExit) {
            sp = Observable.timer(spacingTime, TimeUnit.SECONDS)
                    .compose(service.<Long>bindUntilEvent(ServiceEvent.DESTROY))
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Long aLong) {
                            if (mConnection != null) {
                                mConnection.closeNow();
                                mConnection = null;
                            }
                            LogUtil.i(TAG, "reconnect..");
                            connect();
                        }
                    });
        }
    }

    Handler handler = new Handler();

    public void send( String order) {
        byte[] bytes = Converts.HexString2Bytes(order);

        if (mConnection != null) {
            mConnection.writeBytes(Observable.just(bytes))
//                    .compose(service.<Void>bindUntilEvent(ServiceEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Void>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            listener.onError(SEND_FAILED, "发送失败");
                        }

                        @Override
                        public void onNext(Void aVoid) {
                        }
                    });
            LogUtil.i(TAG + "发送数据->" + order);

        } else {
            listener.onError(RECONNECT_FAILED, "与服务器连接失败,重连中...");
        }
    }


    private static final int SEND_FAILED = 0;
    private static final int RECONNECT_FAILED = 1;
    private static final int CONNECT_BREAK = 2;
    private static final int CONNECT_FAILED = 3;

    /**
     * 停止服务
     */
    public void disConnect() {
        isExit = true;
        handler.removeCallbacksAndMessages(null);
        if (sp != null) {

            try {
                sp.unsubscribe();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (spInput != null) {
            if (!spInput.isUnsubscribed())
                spInput.unsubscribe();
        }
        if (spClient != null) {
            try {
                spClient.unsubscribe();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mConnection != null) {
            mConnection.closeNow();
            mConnection = null;
            LogUtil.i(TAG + "关闭tcp");
        }
    }

    public void setListener(TcpListener listener) {
        this.listener = listener;
    }

    private TcpListener listener;

    public interface TcpListener {
        void onReceive(String result);
        void onError(int code, String msg);
    }


}

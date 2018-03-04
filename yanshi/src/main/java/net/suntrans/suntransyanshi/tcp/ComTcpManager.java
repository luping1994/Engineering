package net.suntrans.suntransyanshi.tcp;


import android.os.Handler;

import net.suntrans.suntransyanshi.utils.Converts;
import net.suntrans.suntransyanshi.utils.LogUtil;

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

public class ComTcpManager {
    private static final String TAG = "TcpManager";
    private Connection<byte[], byte[]> mConnection;
    private int spacingTime = 5;
    private String ip;
    private int port;
    private boolean isExit = false;
    private Subscription sp;
    private Subscription spClient;
    private Subscription spInput;


    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public ComTcpManager(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    private String checkCode;


    public void connect() {
        if (mConnection != null)
            return;
        new Thread(){
            @Override
            public void run() {

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
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Connection<byte[], byte[]>>() {
                            @Override
                            public void onCompleted() {
                                System.out.println("连接onCompleted");
                                listener.onconnected();
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                LogUtil.i(TAG + "连接失败!");
                                listener.onError(CONNECT_FIELD, "连接服务器失败,请检查你的网络");
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
//                            System.out.println("spinput是否解除订阅:" + spInput.isUnsubscribed());
//                            if (!spInput.isUnsubscribed())
//                                spInput.unsubscribe();
//                            System.out.println("spinput是否解除订阅:" + spInput.isUnsubscribed());
                                    spInput = null;
                                }
                                spInput = mConnection.getInput()
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<byte[]>() {
                                            @Override
                                            public void onCompleted() {
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                e.printStackTrace();
                                                LogUtil.i(TAG, "连接中断");
                                                listener.onError(CONNECT_BREAK, "服务器连接中断");
                                                mConnection.closeNow();
                                                mConnection = null;
                                                reconnect();
                                            }

                                            @Override
                                            public void onNext(byte[] bytes) {
                                                listener.onReceive(Converts.Bytes2HexString(bytes));
                                            }
                                        });
                            }
                        });
            }
        }.start();


    }


    private void reconnect() {
        if (!isExit) {
            sp = Observable.timer(spacingTime, TimeUnit.SECONDS)
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

    public void send(final String order) {
        LogUtil.i(TAG,"send:"+order);
        byte[] bytes = Converts.HexString2Bytes(order);

        if (mConnection != null) {
            mConnection.writeBytes(Observable.just(bytes))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Void>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            listener.onError(SEND_FAILED, "发送命令失败");
                        }

                        @Override
                        public void onNext(Void aVoid) {
                            LogUtil.i(TAG + "发送成功->" + order);
                        }
                    });

        } else {
            listener.onError(RECONNECT_FAILED, "发送命令失败,请检查你的网络");
        }
    }


    public static final int SEND_FAILED = 0;
    public static final int RECONNECT_FAILED = 1;
    public static final int CONNECT_BREAK = 2;
    public static final int CONNECT_FIELD = 3;

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
        listener=null;
    }

    public void setListener(TcpListener listener) {
        this.listener = listener;
    }


    private TcpListener listener;


    public interface TcpListener {
        void onReceive(String result);

        void onError(int code, String msg);

        void onconnected();
    }


}

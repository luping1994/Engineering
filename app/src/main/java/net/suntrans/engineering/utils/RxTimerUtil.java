package net.suntrans.engineering.utils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by Looney on 2018/3/3.
 * Des:
 */

public class RxTimerUtil {
    private static Subscription subscription;

    /** milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    public static void timer(long milliseconds,final IRxNext next) {
        Subscription subscribe = Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        cancel();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (next != null) {
                            next.doNext(aLong);
                        }
                    }
                });
    }


    /** 每隔milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    public static void interval(long milliseconds,final IRxNext next){
        subscription = Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        cancel();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if(next!=null){
                            next.doNext(aLong);
                        }
                    }
                });
    }


    /**
     * 取消订阅
     */
    public static void cancel(){
        if(subscription!=null&&!subscription.isUnsubscribed()){
            subscription.unsubscribe();
            LogUtil.e("====定时器取消======");
        }
    }

    public interface IRxNext{
        void doNext(long number);
    }
}

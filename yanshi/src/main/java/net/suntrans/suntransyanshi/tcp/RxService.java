package net.suntrans.suntransyanshi.tcp;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by Looney on 2017/2/20.
 */

public class RxService extends Service implements LifecycleProvider<ServiceEvent> {

    private final BehaviorSubject<ServiceEvent> lifecycleSubject = BehaviorSubject.create();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nonnull
    @Override
    public Observable<ServiceEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ServiceEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);

    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        lifecycleSubject.onNext(ServiceEvent.UNBINDSERVICE);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(ServiceEvent.DESTROY);
    }
}


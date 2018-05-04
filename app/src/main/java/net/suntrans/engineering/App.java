package net.suntrans.engineering;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;


/**
 * Created by Looney on 2017/12/20.
 * Des:
 */

public class App extends MultiDexApplication {

    public static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences =  application.getSharedPreferences("st_building",Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static Application getApplication() {
        return application;
    }

    private static Application application;

    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
//        YanshiApp.init(this);

        Bugly.init(this,"3be76da92b",false);
    }
    /**
     * 分割 Dex 支持 * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

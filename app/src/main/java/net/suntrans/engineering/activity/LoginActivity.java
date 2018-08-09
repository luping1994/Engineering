package net.suntrans.engineering.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import com.pgyersdk.update.PgyUpdateManager;

import net.suntrans.building.BasedActivity;
import net.suntrans.engineering.App;
import net.suntrans.engineering.Config;
import net.suntrans.engineering.MainActivity;
import net.suntrans.engineering.R;
import net.suntrans.engineering.api.RetrofitHelper;
import net.suntrans.engineering.bean.LoginEntity;
import net.suntrans.engineering.utils.UiUtils;
import net.suntrans.looney.widgets.EditView;
import net.suntrans.looney.widgets.LoadingDialog;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Looney on 2017/5/26.
 */

public class LoginActivity extends BasedActivity implements View.OnClickListener {
    public static final String TRANSITION_SLIDE_BOTTOM = "SLIDE_BOTTOM";
    public static final String EXTRA_TRANSITION = "EXTRA_TRANSITION";
    private EditView mobile;
    private EditView password;
    private LoadingDialog dialog;
    private Subscription subscribe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        applyTransition();
        setContentView(R.layout.activity_login);
        init();
    }

    private void applyTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transition = getIntent().getStringExtra(EXTRA_TRANSITION);
            switch (transition) {
                case TRANSITION_SLIDE_BOTTOM:
                    Transition transitionSlideBottom =
                            TransitionInflater.from(this).inflateTransition(R.transition.slide_bottom);
                    getWindow().setEnterTransition(transitionSlideBottom);
                    break;
            }
        }
    }

    private void init() {
        mobile = (EditView) findViewById(R.id.mobile);
        password = (EditView) findViewById(R.id.password);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        String username = App.getSharedPreferences().getString("username", "");
        String passwords = App.getSharedPreferences().getString("password", "");

        mobile.setText(username);
        password.setText(passwords);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.wangjimima).setOnClickListener(this);
        findViewById(R.id.zhuce).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);

        PgyUpdateManager.register(this, Config.FILE_PROVIDER);

    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                supportFinishAfterTransition();
                break;

            case R.id.login:
                String username = "";
                String password1 = "";
                username = mobile.getText().toString();
                password1 = password.getText().toString();

                if (TextUtils.isEmpty(password1)) {
                    UiUtils.showToast("密码不能为空");
                    break;
                }
                login(username,password1);
                break;
        }
    }

    private void login(final String username, final String password1) {
        dialog.setWaitText("登陆中,请稍后");
        dialog.show();
        RetrofitHelper.getApi().login("password", "password", "password", username, password1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoginEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast("登录失败!");
                        dialog.dismiss();
                    }

                    @Override
                    public void onNext(LoginEntity msg) {
                        dialog.dismiss();
                        if (msg .code==200) {
                            String access_token = msg.token.access_token;
                            String expires_in = msg.token.expires_in;
                            App.getSharedPreferences()
                                        .edit()
                                        .putString("access_token", access_token)
                                        .putLong("expires_in", Long.valueOf(expires_in))
                                        .putBoolean("isLogin", true)
                                        .putString("username", username)
                                        .putString("password", password1)
                                        .putLong("fristLoginTime", System.currentTimeMillis() / 1000)
                                        .apply();
                            handler.sendEmptyMessage(1);

                        } else {
                            UiUtils.showToast(msg.msg);
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }
}

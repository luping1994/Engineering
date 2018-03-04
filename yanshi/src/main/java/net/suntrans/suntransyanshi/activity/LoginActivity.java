package net.suntrans.suntransyanshi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import net.suntrans.suntransyanshi.MainActivity;
import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/12.
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R2.id.edit_account)
    TextInputEditText editAccount;
    @BindView(R2.id.edit_password)
    TextInputEditText editPassword;
    @BindView(R2.id.login_text)
    RelativeLayout loginText;
    @BindView(R2.id.login)
    Button login;
    @BindView(R2.id.root)
    RelativeLayout root;

    private AppCompatCheckBox compatCheckBox;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        compatCheckBox = (AppCompatCheckBox) findViewById(R.id.remeberpassword);
        compatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @OnClick(R2.id.login)
    public void onClick() {
        String account = editAccount.getText().toString();
        String password = editPassword.getText().toString();
        if (account == null || account.equals("")) {
            editAccount.setError("帐号不能为空");
            return;
        }
        if (password == null || password.equals("")) {
            editPassword.setError("密码不能为空");
            return;
        }
        if (!account.equals("admin") || !password.equals("admin")) {
            editPassword.setError("帐号或密码错误!");
            return;
        }
        dialog = new ProgressDialog(this);
        dialog.show();
        handler.sendEmptyMessageDelayed(1, 500);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            overridePendingTransition(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
            finish();
        }
    };

}

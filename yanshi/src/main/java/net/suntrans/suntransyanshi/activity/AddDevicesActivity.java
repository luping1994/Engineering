package net.suntrans.suntransyanshi.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.suntrans.suntransyanshi.R;
import net.suntrans.suntransyanshi.R2;
import net.suntrans.suntransyanshi.utils.DbHelper;
import net.suntrans.suntransyanshi.utils.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Looney on 2017/4/13.
 */

public class AddDevicesActivity extends AppCompatActivity {

    @BindView(R2.id.RSAddr)
    EditText RSAddr;
    @BindView(R2.id.zhilianip)
    EditText zhilianip;
    @BindView(R2.id.zhilianport)
    EditText zhilianport;
    @BindView(R2.id.waiwangip)
    EditText waiwangip;
    @BindView(R2.id.waiwangport)
    EditText waiwangport;
    @BindView(R2.id.bendiip)
    EditText bendiip;
    @BindView(R2.id.bendiport)
    EditText bendiport;
    @BindView(R2.id.qvxiao)
    Button qvxiao;
    @BindView(R2.id.add)
    Button add;
    @BindView(R2.id.name)
    EditText name;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevices);
        type = getIntent().getStringExtra("type");
        ButterKnife.bind(this);
    }

    @OnClick({R2.id.qvxiao, R2.id.add})
    public void onViewClicked(View view) {


        int i = view.getId();
        if (i == R.id.qvxiao) {
            Intent intent = new Intent();
            intent.putExtra("result", "failed");
            setResult(100, intent);
            finish();
            overridePendingTransition(0, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);

        } else if (i == R.id.add) {
            upDateDataBased();

        }
    }

    private void upDateDataBased() {
        String name1 = name.getText().toString();
        String rsAddr1 = RSAddr.getText().toString();
        String zhilianip1 = zhilianip.getText().toString();
        String zhilianport1 = zhilianport.getText().toString();
        String waiwangip1 = waiwangip.getText().toString();
        String waiwangport1 = waiwangport.getText().toString();
        String bendiip1 = bendiip.getText().toString();
        String bendiport1 = bendiport.getText().toString();
        if (TextUtils.isEmpty(name1)){
            UiUtils.showToast("请输入名字");
            return;
        }
        if (TextUtils.isEmpty(rsAddr1))
        {
            UiUtils.showToast("请输入通信地址");
            return;
        }
        DbHelper helper = new DbHelper(AddDevicesActivity.this, "IBMS", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();


        if (type.equals("stslc6")){
            Cursor cursor = db.query(true, "switchs_tb", new String[]{"RSAddr"}, "RSAddr=?", new String[]{rsAddr1}, null, null, null, null);
            if (cursor.getCount() > 0) {
                UiUtils.showToast(getString(R.string.tips_addr_exist));
                cursor.close();
            }else {
                for (int i=1;i<=6;i++){
                    ContentValues cv = new ContentValues();
                    cv.put("Name", name1);
                    cv.put("RSAddr", rsAddr1);
                    cv.put("zhilianip", zhilianip1);
                    cv.put("zhilianport", zhilianport1);
                    cv.put("waiwangip", waiwangip1);
                    cv.put("waiwangport", waiwangport1);
                    cv.put("bendiip", bendiip1);
                    cv.put("bendiport", bendiport1);
                    cv.put("Type", "6");
                    cv.put("Channel",i+"");
                    db.insert("switchs_tb", null, cv);
                }
                UiUtils.showToast(getString(R.string.add_success));
            }

        }else if (type.equals("stslc10")){
            Cursor cursor = db.query(true, "switchs_tb", new String[]{"RSAddr"}, "RSAddr=?", new String[]{rsAddr1}, null, null, null, null);
            if (cursor.getCount() > 0) {
                UiUtils.showToast(getString(R.string.tips_addr_exist));
                cursor.close();
            }else {
                for (int i=1;i<=10;i++){
                    ContentValues cv = new ContentValues();

                    cv.put("Name", name1);
                    cv.put("RSAddr", rsAddr1);
                    cv.put("zhilianip", zhilianip1);
                    cv.put("zhilianport", zhilianport1);
                    cv.put("waiwangip", waiwangip1);
                    cv.put("waiwangport", waiwangport1);
                    cv.put("bendiip", bendiip1);
                    cv.put("bendiport", bendiport1);
                    cv.put("Type", "10");
                    cv.put("Channel",i+"");
                    db.insert("switchs_tb", null, cv);

                }
                UiUtils.showToast(getString(R.string.add_success));

            }

        }else if (type.equals("sixsensor")){
            Cursor cursor = db.query(true, "sixsensor_tb", new String[]{"RSAddr"}, "RSAddr=?", new String[]{rsAddr1}, null, null, null, null);
            if (cursor.getCount() > 0) {
                UiUtils.showToast(getString(R.string.tips_addr_exist));
                cursor.close();
            }else {
                ContentValues cv = new ContentValues();
                cv.put("Name", name1);
                cv.put("RSAddr", rsAddr1);
                cv.put("zhilianip", zhilianip1);
                cv.put("zhilianport", zhilianport1);
                cv.put("waiwangip", waiwangip1);
                cv.put("waiwangport", waiwangport1);
                cv.put("bendiip", bendiip1);
                cv.put("bendiport", bendiport1);
//            cv.put("Type", "10");
//            cv.put("Channel",i+"");
                db.insert("sixsensor_tb", null, cv);
                UiUtils.showToast(getString(R.string.add_success));

            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Intent intent = new Intent();
        intent.putExtra("result", "success");
        setResult(100, intent);
        finish();
        overridePendingTransition(0, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);
    }
}

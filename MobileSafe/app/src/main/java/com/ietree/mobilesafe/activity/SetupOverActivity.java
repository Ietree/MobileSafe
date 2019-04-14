package com.ietree.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.ConstantValue;
import com.ietree.mobilesafe.utils.SpUtil;

/**
 * 手机防盗界面
 */
public class SetupOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 判断之前的密码是否设置成功
        boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            // 输入密码成功，并且四个界面设置完成，停留在设置完成功能列表
            setContentView(R.layout.activity_setup_over);
        } else {
            // 输入密码成功，四个导航界面没有设置完成，跳转到导航界面第一个
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }
}

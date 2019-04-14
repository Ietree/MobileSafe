package com.ietree.mobilesafe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.ConstantValue;
import com.ietree.mobilesafe.utils.SpUtil;
import com.ietree.mobilesafe.view.SettingItemView;

/**
 * 设置界面Activity
 */
public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        initUpdateUI();
    }

    /**
     * 初始化自动更新UI
     */
    private void initUpdateUI() {
        final SettingItemView siv_update = findViewById(R.id.siv_update);
        // 记录上一次的设置状态，设置回显
        boolean openUpdate = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setCheck(openUpdate);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前状态
                boolean isCheck = siv_update.isCheck();
                // 与当前状态取反
                siv_update.setCheck(!isCheck);
                // 将设置后的值存储到SharedPreferences中
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });
    }
}

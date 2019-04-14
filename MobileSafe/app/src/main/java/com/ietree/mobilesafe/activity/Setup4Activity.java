package com.ietree.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.ConstantValue;
import com.ietree.mobilesafe.utils.SpUtil;
import com.ietree.mobilesafe.utils.ToastUtil;

public class Setup4Activity extends AppCompatActivity {

    private CheckBox cb_start_safe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        initUI();
    }

    private void initUI() {
        cb_start_safe = findViewById(R.id.cb_start_safe);
        // 设置回显数据
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        cb_start_safe.setChecked(open_security);
        // 根据选择状态设置显示文字
        if (open_security) {
            cb_start_safe.setText("安全设置已开启");
        } else {
            cb_start_safe.setText("安全设置已关闭");
        }
        // 设置点击事件切换设置状态
        cb_start_safe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 存储点击切换后的状态
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
                if (isChecked) {
                    cb_start_safe.setText("安全设置已开启");
                } else {
                    cb_start_safe.setText("安全设置已关闭");
                }
            }
        });
        cb_start_safe.setChecked(!cb_start_safe.isChecked());

    }

    /**
     * 点击下一页跳转到下一页
     */
    public void nextPage(View view) {
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);

            // 开启平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.showToast(getApplicationContext(), "请开启防盗保护");
        }
    }

    /**
     * 点击上一页跳转到上一页
     */
    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();

        // 开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }
}

package com.ietree.mobilesafe.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.ConstantValue;
import com.ietree.mobilesafe.utils.SpUtil;
import com.ietree.mobilesafe.utils.ToastUtil;
import com.ietree.mobilesafe.view.SettingItemView;

/**
 * 手机防盗第2个设置界面
 */
public class Setup2Activity extends AppCompatActivity {

    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initUI();
    }

    private void initUI() {
        siv_sim_bound = findViewById(R.id.siv_sim_bound);
        // 回显，读取已有的绑定状态作为显示
        final String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }

        // 设置siv_sim_bound的点击事件
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取原有的状态
                boolean isChecked = siv_sim_bound.isCheck();
                // 被点击之后需要状态取反
                siv_sim_bound.setCheck(!isChecked);
                if (!isChecked) {
                    // 存储(sim卡的序列号)
                    // 获取SIM卡的序列号
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    // TODO 查看授权状态怎么弄
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    String simSerialNumber = manager.getSimSerialNumber();
                    SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
                } else {
                    // 将存储的SIM卡序列卡号节点删除掉
                    SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    /**
     * 点击下一页跳转到下一页
     */
    public void nextPage(View view) {
        // 判断SIM卡序列号是否已经绑定，绑定则跳转到下一页，未绑定则提示用户需要绑定SIM卡序列号
        String simSerialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(simSerialNumber)) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            // 开启平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.showToast(this, "请先绑定SIM卡");
        }
    }

    /**
     * 点击上一页跳转到上一页
     */
    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();

        // 开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }
}

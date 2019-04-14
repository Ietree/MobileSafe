package com.ietree.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.ConstantValue;
import com.ietree.mobilesafe.utils.SpUtil;
import com.ietree.mobilesafe.utils.ToastUtil;

/**
 * 手机防盗第3个设置界面
 */
public class Setup3Activity extends AppCompatActivity {

    private Button btn_select_number;
    private EditText et_phone_number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initUI();
    }

    private void initUI() {
        et_phone_number = findViewById(R.id.et_phone_number);
        String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        et_phone_number.setText(phone);
        btn_select_number = findViewById(R.id.btn_select_number);
        btn_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            // 获取传递过来的数据
            String phone = data.getStringExtra("phone");
            // 去掉手机号之间的横线和空格
            phone = phone.replace("-", "").replace(" ", "").trim();
            et_phone_number.setText(phone);
            // 将选中的联系人号码存储在Sp中
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
        }
        // 返回到当前界面的时候，接受结果的方法
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 点击下一页跳转到下一页
     */
    public void nextPage(View view) {
        String phone = et_phone_number.getText().toString().trim();
//        String phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            // 将选中的联系人号码存储在Sp中
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);

            // 开启平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.showToast(this, "请输入电话号码");
        }
    }

    /**
     * 点击上一页跳转到上一页
     */
    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();

        // 开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }
}

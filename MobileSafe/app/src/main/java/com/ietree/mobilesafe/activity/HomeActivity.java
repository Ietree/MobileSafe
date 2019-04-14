package com.ietree.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.ConstantValue;
import com.ietree.mobilesafe.utils.Md5Util;
import com.ietree.mobilesafe.utils.SpUtil;
import com.ietree.mobilesafe.utils.ToastUtil;

/**
 * 程序主界面Activity
 */
public class HomeActivity extends AppCompatActivity {

    private GridView gv_function_list;
    private int[] mIcons;
    private String[] mFunctionItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // 初始化UI
        initUI();

        initData();
    }

    private void initData() {
        mIcons = new int[]{R.drawable.home_safe, R.drawable.home_callmsgsafe,
                R.drawable.home_apps, R.drawable.home_taskmanager,
                R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings};
        mFunctionItems = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具",
                "设置中心"};

        gv_function_list.setAdapter(new MyAdapter());

        // 设置GridView条目的监听事件
        gv_function_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显示对话框
     */
    private void showDialog() {
        // 判断本地是否存有密码
        String pwd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PWD, "");
        if (TextUtils.isEmpty(pwd)) {
            // 初始设置密码对话框
            showSetPwdDialog();
        } else {
            // 弹出确认密码对话框
            showConfirmDialog();
        }
    }

    /**
     * 初始设置密码对话框
     */
    private void showSetPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_pwd, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();

        // 给对话框设置点击事件
        Button btn_submit = view.findViewById(R.id.btn_submit);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断输入的密码是否符合格式
                EditText et_set_pwd = view.findViewById(R.id.et_set_pwd);
                EditText et_comfirm_pwd = view.findViewById(R.id.et_comfirm_pwd);
//                String pwd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD, "");
                String pwd = et_set_pwd.getText().toString().trim();
                String confirmPwd = et_comfirm_pwd.getText().toString().trim();

                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(getApplicationContext(), "请输入密码");
                } else if (TextUtils.isEmpty(confirmPwd)) {
                    ToastUtil.showToast(getApplicationContext(), "请输入确认密码");
                } else if (!pwd.equals(confirmPwd)) {
                    ToastUtil.showToast(getApplicationContext(), "密码不一致，请重新输入确认密码");
                } else if (pwd.equals(confirmPwd)) {
                    // 点击确认进入手机防盗界面
                    Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    // 将输入的密码保存到手机本地
                    SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD, Md5Util.encoder(confirmPwd));
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击取消则直接隐藏对话框
                dialog.dismiss();
            }
        });
    }

    /**
     * 弹出确认密码对话框
     */
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_confirm_pwd, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();

        // 给对话框设置点击事件
        Button btn_submit = view.findViewById(R.id.btn_submit);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取之前存储的密码
                String pwd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD, "");
                // 判断输入的密码是否符合格式
                EditText et_comfirm_pwd = view.findViewById(R.id.et_comfirm_pwd);
                String confirmPwd = et_comfirm_pwd.getText().toString().trim();

                if (TextUtils.isEmpty(confirmPwd)) {
                    ToastUtil.showToast(getApplicationContext(), "请输入确认密码");
                } else if (pwd.equals(Md5Util.encoder(confirmPwd))) {
                    // 点击确认进入手机防盗界面
                    Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                } else {
                    ToastUtil.showToast(getApplicationContext(), "密码输入错误");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击取消则直接隐藏对话框
                dialog.dismiss();
            }
        });
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        gv_function_list = findViewById(R.id.gv_function_list);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFunctionItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mFunctionItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            ImageView iv_function_icon = view.findViewById(R.id.iv_function_icon);
            TextView tv_function_des = view.findViewById(R.id.tv_function_des);
            iv_function_icon.setBackgroundResource(mIcons[position]);
            tv_function_des.setText(mFunctionItems[position]);
            return view;
        }
    }
}

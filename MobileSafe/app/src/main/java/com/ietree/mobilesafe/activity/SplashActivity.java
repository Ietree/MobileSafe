package com.ietree.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.StreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = "SplashActivity";
    private static final int UPDATE_VERSION = 100;
    // 网络请求失败
    private static final int INTERNET_REQUEST_ERROR = 101;
    private static final int IO_ERROR = 102;
    private static final int JSON_ERROR = 103;
    private static final int ENTER_HOME_PAGE = 104;
    private TextView tv_version_name;
    private int mLocalVersionCode;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    break;
                case ENTER_HOME_PAGE:
                    // 不需要更新，直接进入主界面
                    enterHomePage();
                    break;
                case INTERNET_REQUEST_ERROR:
                    break;
                case IO_ERROR:
                    break;
                case JSON_ERROR:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 进入程序主界面
     */
    private void enterHomePage() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 1、初始化UI
        initUI();

        // 2、初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 应用版本名称
        tv_version_name.setText("版本：" + getVersionName());
        // 检测是否有更新(本地版本号和服务器版本号进行对比)，如果有更新就提示用户更新
        // 本地版本号
        mLocalVersionCode = getVersionCode();
        // 从服务器上获取versionCode
        checkVersion();
    }

    /**
     * 验证版本号，检查是否需要更新
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    URL url = new URL("http://192.168.0.101:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    // 网络请求成功
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String json = StreamUtil.stream2String(is);
                        Log.i(TAG, json);

                        JSONObject object = new JSONObject(json);
                        String versionCode = object.getString("versionCode");
                        String versionName = object.getString("versionName");
                        String versionDes = object.getString("versionDes");
                        String downloadUrl = object.getString("downloadUrl");
                        // 对比版本号，当服务器上的版本号>本地版本号，则需要进行更新
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            Log.i(TAG, "需要更新最新版本...");
                            // 弹窗提示用户是否需要更新版本，消息机制
                            msg.what = UPDATE_VERSION;
                        } else {
                            // 验证不需要更新，直接进入程序主界面
                            Log.i(TAG, "当前版本不需要进行更新操作，直接进入程序主界面");
                            msg.what = ENTER_HOME_PAGE;
                        }
                    } else {
                        Log.i(TAG, "网络请求失败...");
                        msg.what = INTERNET_REQUEST_ERROR;
                    }
                } catch (IOException e) {
                    Log.i(TAG, "io解析异常...");
                    msg.what = IO_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.i(TAG, "json解析异常...");
                    msg.what = JSON_ERROR;
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 获取版本Code
     *
     * @return 返回版本Code
     */
    private int getVersionCode() {
        int versionCode = 1;

        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本名称
     *
     * @return 返回版本名称
     */
    private String getVersionName() {
        String versionName = "1.0";
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
    }
}

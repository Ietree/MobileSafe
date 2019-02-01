package com.ietree.mobilesafe.activity;

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
    private TextView tv_version_name;
    private int mLocalVersionCode;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

            }
        }
    };

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
                        if (mLocalVersionCode < Integer.parseInt(versionCode)){
                            // 弹窗提示用户是否需要更新版本，消息机制
                            msg.what = UPDATE_VERSION;
                        } else {
                            // 不需要更新时直接进入程序主页面
                        }
                    } else {
                        Log.i(TAG, "网络请求失败...");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e){
                    e.printStackTrace();
                }
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

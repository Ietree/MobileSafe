package com.ietree.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.StreamUtil;
import com.ietree.mobilesafe.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 程序启动界面
 */
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
    private String mVersionDes;
    private String mDownloadUrl;
    private ProgressDialog mProgressDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME_PAGE:
                    // 不需要更新，直接进入主界面
                    enterHomePage();
                    break;
                case INTERNET_REQUEST_ERROR:
                    ToastUtil.show(SplashActivity.this, "网络请求错误，请查看网络是否连接正常");
                    enterHomePage();
                    break;
                case IO_ERROR:
                    ToastUtil.show(SplashActivity.this, "IO解析异常...");
                    enterHomePage();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(SplashActivity.this, "Json解析异常...");
                    enterHomePage();
                    break;
                default:
                    enterHomePage();
                    break;
            }
        }
    };

    private void showUpdateDialog() {
        // 弹出升级对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setTitle("更新提示");
        dialog.setMessage(mVersionDes);
        dialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载服务器上最新的apk安装包
                downloadApkFromServer();
            }
        });
        dialog.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHomePage();
            }
        });
        dialog.show();
    }

    /**
     * 下载服务器上最新的apk安装包
     */
    private void downloadApkFromServer() {
        // 1、判断内存是否可用，是否挂在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mProgressDialog = new ProgressDialog(SplashActivity.this);
            // 获取存储路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator + "mobilesafe.apk";
            RequestParams requestParams = new RequestParams(mDownloadUrl);
            requestParams.setSaveFilePath(path);
            // TODO 这个貌似用于断点续传，待验证
            requestParams.setAutoRename(true);
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {
                    // 网络请求开始的时候调用
                    Log.i(TAG, "等待下载");
                }

                @Override
                public void onStarted() {
                    // 下载的时候不断回调的方法
                    Log.i(TAG, "开始下载");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    // 当前的下载进度和文件总大小
                    Log.i(TAG, "正在下载中...");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setMessage("正在下载中...");
                    mProgressDialog.show();
                    mProgressDialog.setMax((int) total);
                    mProgressDialog.setProgress((int) current);
                }

                @Override
                public void onSuccess(File result) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                    startActivity(intent);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(TAG, "下载失败");
                    mProgressDialog.dismiss();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i(TAG, "下载失败");
                    mProgressDialog.dismiss();
                }

                @Override
                public void onFinished() {
                    Log.i(TAG, "下载成功");
                    mProgressDialog.dismiss();
                }
            });
        }
    }

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
                long startTime = System.currentTimeMillis();
                Message msg = Message.obtain();
                try {
                    URL url = new URL("http://192.168.43.113:8080/update.json");
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
                        mVersionDes = object.getString("versionDes");
                        mDownloadUrl = object.getString("downloadUrl");
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
                } finally {
                    // 指定睡眠时间，最长时间不超过4秒，不足4秒的补齐为4秒
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
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

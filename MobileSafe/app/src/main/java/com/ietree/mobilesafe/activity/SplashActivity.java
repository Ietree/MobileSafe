package com.ietree.mobilesafe.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ietree.mobilesafe.R;
import com.ietree.mobilesafe.utils.ConstantValue;
import com.ietree.mobilesafe.utils.SpUtil;
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
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 程序启动页面
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    // 更新版本的状态码
    private static final int UPDATE_VERSION = 100;
    // 进入应用程序主界面的状态码
    private static final int ENTER_HOME = 101;

    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    private TextView tv_version_name;
    private RelativeLayout rl_splash_root;
    private int mLocalVersionCode;
    private String mVersionDes;
    private String mDownloadUrl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    // 弹出更新对话框提示
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    // 进入主页面，进行Activity页面跳转
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.showToast(getApplicationContext(), "服务器网络地址异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.showToast(getApplicationContext(), "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.showToast(getApplicationContext(), "Json解析异常");
                    enterHome();
                    break;
                default:
                    enterHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 初始化UI
        initUI();

        // 初始化数据
        initData();

        // 初始化动画
        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(3000);
        rl_splash_root.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        // 验证权限
        requestPermission();
        super.onResume();
    }

    /**
     * 弹出更新提示对话框，提示用户更新
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("应用更新提示");
        // 更新内容描述
        builder.setMessage(mVersionDes);
        // 确认立即更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 网络请求下载最新的apk进行安装
                downloadApk();
            }
        });
        // 确认不立即更新
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 取消对话框，进入主界面
                enterHome();
            }
        });

        // 点击取消时间监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 即使用户点击取消更新，也让用户进入程序主界面
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 从服务器上下载最新的Apk包
     */
    private void downloadApk() {
        // 从服务器上下载最新的Apk包，放置到手机里
        // 判断手机存储是否可用，是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获取Apk下载放置路径 /storage/emulated/0/mobilesafe.apk
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator + "mobilesafe.apk";
            // 发送请求，获取apk包，并且放置到指定的路径
            Log.i(TAG, path);
            // 使用xutils3工具类下载apk包
            RequestParams params = new RequestParams(mDownloadUrl);
            // 自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
            params.setSaveFilePath(path);
            // 自动为文件命名
            params.setAutoRename(true);
            x.http().post(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {
                    //网络请求之前回调clear
                    Log.i(TAG, "网络请求之前回调");
                }

                @Override
                public void onStarted() {
                    //网络请求开始的时候回调
                    Log.i(TAG, "网络请求开始的时候回调");
                }

                @Override
                public void onSuccess(File result) {
                    // 下载成功
                    Log.i(TAG, "下载成功");
                    // apk下载完成后，调用系统的安装方法
                    installApk(result);
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    //下载的时候不断回调的方法
                    Log.i(TAG, "下载中...");
                    //当前进度和文件总大小
                    Log.i(TAG, "current：" + current + "，total：" + total);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    // 下载失败
                    Log.i(TAG, "下载失败");
                    ex.printStackTrace();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    // 取消下载
                    Log.i(TAG, "取消下载");
                }

                @Override
                public void onFinished() {
                    // 下载完成
                    Log.i(TAG, "下载完成");
                }
            });
        }
    }

    /**
     * 安装下载成功的Apk包
     */
    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        startActivity(intent);
        startActivityForResult(intent, 0);
    }

    // 开启一个Activity后，返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        Log.i(TAG, "requestPermission");
        try {
            // checkSelfPermission检查应用是否具有某个危险权限。如果应用具有此权限，方法将返回 PackageManager.PERMISSION_GRANTED，并且应用可以继续操作。
            // 如果应用不具有此权限，方法将返回 PackageManager.PERMISSION_DENIED，且应用必须明确向用户要求权限。
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // ActivityCompat.shouldShowRequestPermissionRationale:如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                // 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。如果设备规范禁止应用具有该权限，此方法也会返回 false。
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.i(TAG, "shouldShowRequestPermissionRationale");
                    // ActivityCompat.requestPermissions 应用可以通过这个方法动态申请权限，调用后会弹出一个对话框提示用户授权所申请的权限
                    ActivityCompat.requestPermissions(this, PERMISSIONS, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    Log.i(TAG, "requestPermissions");
                    ActivityCompat.requestPermissions(this, PERMISSIONS, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入程序主页面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        // 在开启一个新界面后，将导航界面关闭（导航界面只可见一次）
        finish();
    }


    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
        rl_splash_root = findViewById(R.id.rl_splash_root);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 1、获取版本名称
        String versionName = getVersionName();
        tv_version_name.setText(versionName);
        // 2、获取版本号VersionCode，用于和服务器的版本号比对，判断是否需要更新本地APP
        // 获取本地的版本号
        mLocalVersionCode = getVersionCode();
        // 判断自动更新是否开启，开启则自动更新，否则直接进入主界面
        if (SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
            // 获取服务器上的版本号，并且进行比对
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
    }

    /**
     * 检查版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL("http://10.61.91.18:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);

                    if (connection.getResponseCode() == 200) {
                        // 请求成功，并且返回字节流
                        InputStream is = connection.getInputStream();
                        // 将字节流转换为字符串
                        String json = StreamUtil.stream2String(is);
                        Log.i(TAG, json);

                        JSONObject jsonObject = new JSONObject(json);
                        String versionCode = jsonObject.getString("versionCode");
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        // 将服务器上的版本号和本地版本号进行对比，判断是否需要更新
                        if (Integer.parseInt(versionCode) > mLocalVersionCode) {
                            // 需要进行更新操作，弹出对话框UI，提示需要更新
                            msg.what = UPDATE_VERSION;
                        } else {
                            // 请求不成功，直接进入程序主页面
                            msg.what = ENTER_HOME;
                        }
                    } else {
                        // 请求不成功，需要做请求失败处理
                        // 不需要进行更新操作，直接进入主界面
                        msg.what = ENTER_HOME;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    // 指定睡眠时间，请求网络的时长超过4秒，则不作处理
                    // 请求网络时长不满4秒，强制让其睡满4秒钟
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
     * 获取版本号
     *
     * @return 版本号
     */
    private int getVersionCode() {
        int versionCode = 1;
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本名称
     *
     * @return 版本名称
     */
    private String getVersionName() {
        String versionName = "v0.1";
        PackageManager pm = getPackageManager();
        try {
            // 传0代表获取基本信息
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}

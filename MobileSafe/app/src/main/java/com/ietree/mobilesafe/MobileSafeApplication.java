package com.ietree.mobilesafe;

import android.app.Application;

import org.xutils.x;

/**
 * 使用Utils3开源框架时需要添加的项
 */
public class MobileSafeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        // 是否输出debug日志, 开启debug会影响性能
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}

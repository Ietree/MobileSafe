package com.ietree.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtil {

    /**
     * toast提示
     *
     * @param context 上下文环境
     * @param msg 报错信息
     */
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

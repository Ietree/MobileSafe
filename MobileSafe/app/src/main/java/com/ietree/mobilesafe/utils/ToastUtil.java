package com.ietree.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtil {

    /**
     * 打印Toast
     *
     * @param ctx 上下文环境
     * @param msg 打印信息
     */
    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
    }

    private ToastUtil() {
    }
}

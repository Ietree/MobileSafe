package com.ietree.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 */
public class SpUtil {
    private static SharedPreferences sp;

    /**
     * 存储boolean类型的值到SharedPreferences
     *
     * @param ctx   上下文环境
     * @param key   存储的key
     * @param value 存储的值
     */
    public static void putBoolean(Context ctx, String key, boolean value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 从SharedPreferences读取boolean类型的值
     *
     * @param ctx      上下文环境
     * @param key      存储的key
     * @param defValue 查找不到对应的值时，使用默认值
     */
    public static boolean getBoolean(Context ctx, String key, boolean defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 存储String类型的值到SharedPreferences
     *
     * @param ctx   上下文环境
     * @param key   存储的key
     * @param value 存储的值
     */
    public static void putString(Context ctx, String key, String value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     * 从SharedPreferences读取String类型的值
     *
     * @param ctx      上下文环境
     * @param key      存储的key
     * @param defValue 查找不到对应的值时，使用默认值
     */
    public static String getString(Context ctx, String key, String defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    private SpUtil() {
    }

    /**
     * 删除卡号节点
     *
     * @param ctx 上下文环境
     * @param key 存储的key
     */
    public static void remove(Context ctx, String key) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }
}

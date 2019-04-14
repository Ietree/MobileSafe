package com.ietree.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 字符流转换工具类
 */
public class StreamUtil {

    /**
     * 将字符流转换为字符串
     *
     * @param is 需要转换的字符流
     * @return 返回转换后的字符串
     */
    public static String stream2String(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 不让外部new或者继承
    private StreamUtil() {
    }
}

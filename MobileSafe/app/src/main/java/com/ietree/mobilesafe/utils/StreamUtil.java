package com.ietree.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    /**
     * 将Stream流转换为字符串
     *
     * @param is 数据流
     * @return 转换后的字符串
     */
    public static String stream2String(InputStream is) {
        // 在读取过程中，将读取的内容存储到缓存中，然后一次性转换成字符串返回
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 读流操作，读到没有为止
        byte[] buffer = new byte[1024];
        // 记录读取内容的临时变量
        int temp = -1;
        try {
            while ((temp = is.read(buffer)) != -1) {
                baos.write(buffer, 0, temp);
            }
            // 返回读取数据
            return baos.toString();
        } catch (Exception e) {
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

}

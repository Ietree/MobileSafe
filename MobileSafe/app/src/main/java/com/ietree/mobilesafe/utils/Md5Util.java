package com.ietree.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密算法
 */
public class Md5Util {
   
    /**
     * 将密码进行MD5算法加密
     *
     * @param pwd 密码
     * @return 返回加密后的密码
     */
    public static String encoder(String pwd) {
        // 加盐处理
        pwd = pwd + "mobilesafe";
        try {
            // 1、指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 2、将需要加密的字符串转换为byte类型的数组，然后进行随机哈希过程
            byte[] bs = digest.digest(pwd.getBytes());
            // 3、循环遍历byte数组，让其生成32位字符串，固定写法
            StringBuffer sb = new StringBuffer();
            // 4、拼接字符串过程
            for (byte b : bs) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

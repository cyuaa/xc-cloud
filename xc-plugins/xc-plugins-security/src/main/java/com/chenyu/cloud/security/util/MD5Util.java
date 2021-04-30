package com.chenyu.cloud.security.util;

import java.security.MessageDigest;

/**
 * md5加密
 * Created by JackyChen on 2021/04/29.
 */
public class MD5Util {

    /**
     * md5加密
     * @param password
     * @param secretKey
     * @return
     */
    public static String encode(String password, String secretKey) {
        password = password + secretKey;
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}

package com.tappitz.app.util;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by joaosampaio on 08-03-2016.
 */
public class HashUtil {

    public static String computeSHAHash(String password)
    {
        String SHAHash = null;
        MessageDigest mdSha1 = null;
        try
        {
            mdSha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e1) {
            Log.e("myapp", "Error initializing SHA1 message digest");
        }
        try {
//            mdSha1.update(password.getBytes("ASCII"));
            mdSha1.update(password.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] data = mdSha1.digest();
        try {
            SHAHash=convertToHex(data);
//            Log.d("myapp2", "**--bytes to string:"+new String(data));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return SHAHash;
    }

    private static String convertToHex(byte[] data) throws java.io.IOException
    {
        String result =  new String(Base64.encode(data, Base64.NO_WRAP));
        result = result.replaceAll("[^a-zA-Z0-9]", "");
        return result;
//        StringBuffer sb = new StringBuffer();
//        String hex=null;
//        hex= Base64.encodeToString(data, 0, data.length, Base64.NO_PADDING);
//
//        sb.append(hex);
//
//        return sb.toString();
    }
}

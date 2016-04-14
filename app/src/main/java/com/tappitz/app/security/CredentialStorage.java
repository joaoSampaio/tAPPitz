package com.tappitz.app.security;

import android.content.Context;
import android.util.Base64;

import com.tappitz.app.Global;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sampaio on 06-11-2015.
 */
public class CredentialStorage {



    public static String encryptString(String dataToEncrypt, Context context) {

        try {
            byte[] encodedBytes = null;

            Cipher c = Cipher.getInstance("AES");
            String key = Global.KEY;
//                String key =prefs.getString("SECRET_KEY","");

            byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0,
                    encodedKey.length, "AES");
            c.init(Cipher.ENCRYPT_MODE, originalKey);
            encodedBytes = c.doFinal(dataToEncrypt.getBytes());

            return Base64.encodeToString(encodedBytes, Base64.DEFAULT);







//            SharedPreferences prefs = context.getSharedPreferences("tAPPitz", 0);
//            if (prefs.getString("SECRET_KEY","") == "") {
//                secretKeySpec = GenerateSecretKeySpecs();
//                String stringSecretKey = Base64.encodeToString(
//                        secretKeySpec.getEncoded(), Base64.DEFAULT);
//
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putString("SECRET_KEY", stringSecretKey);
//                editor.commit();
//
//            }
//            if (prefs.getString("SECRET_KEY","") != "") {
//                byte[] encodedBytes = null;
//
//                Cipher c = Cipher.getInstance("AES");
//                String key =prefs.getString("SECRET_KEY","");
//
//                byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
//                SecretKey originalKey = new SecretKeySpec(encodedKey, 0,
//                        encodedKey.length, "AES");
//                c.init(Cipher.ENCRYPT_MODE, originalKey);
//                encodedBytes = c.doFinal(dataToEncrypt.getBytes());
//
//                return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
//            } else {
//                return null;
//            }
        } catch (Exception e) {
//          Log.e(TAG, "AES encryption error");
            return null;
        }
    }


    public static String decryptString(String dataToDecrypt, Context context) {
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            String key = Global.KEY;
            byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0,
                    encodedKey.length, "AES");
            c.init(Cipher.DECRYPT_MODE, originalKey);

            byte[] dataInBytes = Base64.decode(dataToDecrypt,
                    Base64.DEFAULT);

            decodedBytes = c.doFinal(dataInBytes);
            return new String(decodedBytes);
            } catch (Exception e) {
    //              Log.e(TAG, "AES decryption error");
                e.printStackTrace();
                return null;
            }

//        SharedPreferences prefs= context.getSharedPreferences("tAPPitz", 0);
//        if (prefs.getString("SECRET_KEY","") != "") {
//            byte[] decodedBytes = null;
//            try {
//                Cipher c = Cipher.getInstance("AES");
//
//                String key = prefs.getString("SECRET_KEY","");
//                byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
//                SecretKey originalKey = new SecretKeySpec(encodedKey, 0,
//                        encodedKey.length, "AES");
//                c.init(Cipher.DECRYPT_MODE, originalKey);
//
//                byte[] dataInBytes = Base64.decode(dataToDecrypt,
//                        Base64.DEFAULT);
//
//                decodedBytes = c.doFinal(dataInBytes);
//                return new String(decodedBytes);
//            } catch (Exception e) {
////              Log.e(TAG, "AES decryption error");
//                e.printStackTrace();
//                return null;
//            }

//        } else
//            return null;

    }







}

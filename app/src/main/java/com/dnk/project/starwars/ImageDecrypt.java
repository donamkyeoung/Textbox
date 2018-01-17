package com.dnk.project.starwars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by 도남경 on 2018-01-16.
 */

public class ImageDecrypt {
    private static final String algorithm = "AES";
    private static final String transformation = algorithm + "/ECB/PKCS5Padding";
    final String ENCRYPT_KEY = "1111111111111111";
    Key key;
    public static byte[] toBytes(String digits, int radix) throws IllegalArgumentException, NumberFormatException {
        if (digits == null) {
            return null;
        }
        if (radix != 16 && radix != 10 && radix != 8) {
            throw new IllegalArgumentException("For input radix: \"" + radix + "\"");
        }
        int divLen = (radix == 16) ? 2 : 3;
        int length = digits.length();
        if (length % divLen == 1) {
            throw new IllegalArgumentException("For input string: \"" + digits + "\"");
        }
        length = length / divLen;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int index = i * divLen;
            bytes[i] = (byte)(Short.parseShort(digits.substring(index, index+divLen), radix));
        }
        return bytes;
    }
    public Bitmap decrypt(File source) throws Exception {
        SecretKeySpec key = new SecretKeySpec(ENCRYPT_KEY.getBytes(),algorithm);
        this.key = key;
        Bitmap bitmap = crypt1(Cipher.DECRYPT_MODE, source);
        return bitmap;
    }
    private Bitmap crypt1(int mode, File source) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(mode, key);
        InputStream input = null;
        byte[] aa = new byte[5000*5000];

        int i=0;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            byte[] buffer = new byte[5000];
            int read = -1;
            while ((read = input.read(buffer)) != -1) {
                byte[] vbuffer = cipher.update(buffer, 0, read);
                for(int j=0 ; j<vbuffer.length; j++){
                    aa[i] = vbuffer[j];
                    i++;
                }

            }
            byte[] vbuffer =  cipher.doFinal();
            for(int j=0 ; j<vbuffer.length; j++){
                aa[i] = vbuffer[j];
                i++;
            }
            i=0;
            while(buffer[i] != -1){
                System.out.println(aa[i]);
                i++;
            }
            InputStream is = new ByteArrayInputStream(aa);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } finally {

            if (input != null) {
                try { input.close(); } catch(IOException ie) {}
            }
        }

    }
}

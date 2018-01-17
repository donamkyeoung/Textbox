package com.dnk.project.starwars;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by 도남경 on 2018-01-15.
 */

public class ImageEncrypt {
    File input, output;
    private static final String algorithm = "AES";
    private static final String transformation = algorithm + "/ECB/PKCS5Padding";
    final String ENCRYPT_KEY = "1111111111111111";
    Key key;

    public ImageEncrypt(File input, File output) throws Exception {
        this.input = input;
        this.output= output;

        SecretKeySpec key = new SecretKeySpec(ENCRYPT_KEY.getBytes(), algorithm);
        this.key = key;
        encrypt(input, output);
    }
    public void encrypt(File source, File dest) throws Exception {
        crypt(Cipher.ENCRYPT_MODE, source, dest);
    }
    private void crypt(int mode, File source, File dest) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(mode, key);
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            output = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] buffer = new byte[1024];
            int read = -1;
            while ((read = input.read(buffer)) != -1) {
                output.write(cipher.update(buffer, 0, read));
            }
            output.write(cipher.doFinal());
        } finally {
            if (output != null) {
                try { output.close(); } catch(IOException ie) {}
            }
            if (input != null) {
                try { input.close(); } catch(IOException ie) {}
            }
        }
    }

}

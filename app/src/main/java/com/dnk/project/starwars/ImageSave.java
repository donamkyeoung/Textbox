package com.dnk.project.starwars;

import android.os.Environment;

import java.io.File;

/**
 * Created by 도남경 on 2018-01-15.
 */

public class ImageSave {
    final String ENCRYPT_KEY = "1111111111111111";
    File file, e_file;
    String title, e_title;
    Encrypt_class encrypt_class;
    String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();

    public ImageSave(File file, String title){
        this.file = file;
        this.title = title;
        try {
            encrypt_class = new Encrypt_class(ENCRYPT_KEY);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void Save(){
        EncryptImage();
        e_file = new File(sdcard_path+"/TextBox/"+e_title+".imgbox");
    }
    public boolean isFileExists(){
        boolean isfileexists;
        try {
            e_title = encrypt_class.encrypt(title).replace("/", "-");
        }catch (Exception e){
            e.printStackTrace();
        }
        File file = new File(sdcard_path+"/TextBox/"+e_title+".imgbox");
        isfileexists = file.exists();
        return isfileexists;
    }
    public void EncryptImage(){
        try {
            e_file = new File(sdcard_path+"/TextBox/"+e_title+".imgbox");
            ImageEncrypt imageEncrypt = new ImageEncrypt(file, e_file);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

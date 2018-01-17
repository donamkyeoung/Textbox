package com.dnk.project.starwars;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by 도남경 on 2018-01-08.
 */

public class Savefile {
    String title, content, e_title, e_content;
    String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();

    public Savefile(String title, String content) {
        this.title = title;
        this.content = content;

        encryptcontent();
    }
    public void encryptcontent(){
        try {
            Encrypt_class encrypt_class = new Encrypt_class("1111111111111111");
            e_title = encrypt_class.encrypt(title).replace("/", "-");
            e_content = encrypt_class.encrypt(content);
            Log.d("Savefile", "e_title="+e_title+" e_content="+e_content);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void tofile(){
        File dir = new File(sdcard_path+"/TextBox");
        File file = new File(sdcard_path+"/TextBox/"+e_title+".textbox");
        Log.d("SaveFile", dir+"+"+file);
        try{
            if(!dir.exists()){
                file.createNewFile();
            }
            if(!file.exists()) {
                FileOutputStream os = new FileOutputStream(file, true);
                byte[] data = e_content.getBytes();
                os.write(data);
                os.flush();
                os.close();
                varFlush();
            }
        }catch (IOException e){
            Log.w("savefile", "Method called on the UI thread", new Exception("STACK TRACE"));
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsStrting = sw.toString();
            Log.e("StackTraceExam", exceptionAsStrting);
            Log.d("SaveFile", "file save fail");
        }
    }

    public boolean isFileExists(){
        boolean isfileexists;
        File file = new File(sdcard_path+"/TextBox/"+e_title+".textbox");
        isfileexists = file.exists();
        return isfileexists;
    }
    public void varFlush(){
        title = null;
        content = null;
        e_title = null;
        e_content = null;
    }
}

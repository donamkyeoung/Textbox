package com.dnk.project.starwars;

import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 도남경 on 2018-01-09.
 */

public class FileListAdapter extends BaseAdapter{
    final String ENCRYPT_KEY = "1111111111111111";
    String filename[];
    String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();
    File [] files;
    @Override
    public int getCount() {
        File file = new File(sdcard_path+"/TextBox/");
        files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".textbox") || file.getName().endsWith(".imgbox");
            }
        });

        return files.length;
    }

    @Override
    public Object getItem(int i) {
        return filename[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  a  HH:mm");
        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        String de_name="1";
        String pure_name=null;
        try {
            Encrypt_class encrypt_class = new Encrypt_class(ENCRYPT_KEY);
            int index = files[i].getName().indexOf(".");
            pure_name = files[i].getName().substring(0, index).replace("-", "/");
            de_name = encrypt_class.decrypt(pure_name);
        }catch (Exception e){
            e.printStackTrace();
        }

        TextView title_view = new TextView(viewGroup.getContext());
        TextView lastmodtime = new TextView(viewGroup.getContext());
        TextView imgortxt = new TextView(viewGroup.getContext());
        title_view.setTextSize(25);
        title_view.setPadding(3,3,3,3);

        lastmodtime.setTextSize(15);
        lastmodtime.setPadding(3,3,3,3);

        imgortxt.setTextSize(15);
        imgortxt.setPadding(3,3,3,3);

        lastmodtime.setText("마지막 수정 시간: "+sdf.format(new Date(files[i].lastModified())));
        if(files[i].getName().endsWith(".imgbox")){
            int index = de_name.indexOf(".");
            title_view.setText(de_name.substring(0, index));
            imgortxt.setText("Image");
        }else if(files[i].getName().endsWith(".textbox")){
            title_view.setText(de_name);
            imgortxt.setText("Text");
        }
        linearLayout.addView(title_view);
        linearLayout.addView(lastmodtime);
        linearLayout.addView(imgortxt);
        return linearLayout;
    }
    public void research(){
        File file = new File(sdcard_path+"/TextBox/");
        files = file.listFiles();
    }
    public int getFilesLen(){
        return files.length;
    }
}

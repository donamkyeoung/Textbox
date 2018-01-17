package com.dnk.project.starwars;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ImageViewActivity extends AppCompatActivity {
    String filepath;
    final String ENCRYPT_KEY = "1111111111111111";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);

        Intent intent = getIntent();
        filepath = intent.getStringExtra("file_path");
        Log.d("ImageViewActivity", "filepath:"+filepath);
        File file = new File(filepath);
        if(file.exists()){
            try {
                Encrypt_class encrypt_class = new Encrypt_class(ENCRYPT_KEY);
                String filename = encrypt_class.decrypt(file.getName().substring(0, file.getName().indexOf(".")).replace("-", "/"));
                setTitle(filename);
                ImageDecrypt imageDecrypt = new ImageDecrypt();
                imageView.setImageBitmap(imageDecrypt.decrypt(new File(filepath)));
            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(!file.exists()){
            finish();
        }else if(TextUtils.isEmpty(filepath)){
            Toast.makeText(this, "파일이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_image_delete:
                File file = new File(filepath);
                try {
                    file.delete();
                    Toast.makeText(ImageViewActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

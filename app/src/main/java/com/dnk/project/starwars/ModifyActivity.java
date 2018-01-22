package com.dnk.project.starwars;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class ModifyActivity extends AppCompatActivity {
    final String ENCRYPT_KEY = "1111111111111111";
    EditText editText;
    String file_path;
    File file2;
    Encrypt_class encrypt_class;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        file_path = intent.getStringExtra("file_path");
        file2 = new File(file_path);
        editText = (EditText) findViewById(R.id.editText3);
        try {
            encrypt_class = new Encrypt_class(ENCRYPT_KEY);
            setTitle("수정-"+encrypt_class.decrypt(file2.getName().substring(0, file2.getName().indexOf(".")).replace("-", "/")));
        }catch (Exception e){
            e.printStackTrace();
        }

        file_path = intent.getStringExtra("file_path");
        if(!TextUtils.isEmpty(file_path)){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file_path));
                String data, de_data;
                while((data = bufferedReader.readLine())!=null){
                    de_data = encrypt_class.decrypt(data);
                    editText.setText(de_data);
                }
                bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "파일이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onRestart() {
        if(!file2.exists()){
            finish();
        }else {
            Toast.makeText(this, "파일이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.modify_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                String content = editText.getText().toString();
                try {
                    byte[] e_content = encrypt_class.encrypt(content).getBytes();
                    FileOutputStream outputStream = new FileOutputStream(file_path);
                    outputStream.write(e_content);
                    outputStream.flush();
                    outputStream.close();
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.action_cacel:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("종료하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("종료하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}

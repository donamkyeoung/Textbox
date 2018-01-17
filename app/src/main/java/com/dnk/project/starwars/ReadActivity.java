package com.dnk.project.starwars;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadActivity extends AppCompatActivity {
    final String ENCRYPT_KEY = "1111111111111111";
    String file_path;
    File file, file2;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Intent intent = getIntent();
        file_path = intent.getStringExtra("file_path");
        file2 = new File(file_path);
        setTitle(file2.getName().substring(0, file2.getName().indexOf(".")));
        textView = (TextView) findViewById(R.id.show_text);

        if(file2.exists()){
            try {
                Encrypt_class encrypt_class = new Encrypt_class(ENCRYPT_KEY);
                setTitle(encrypt_class.decrypt(file2.getName().substring(0, file2.getName().indexOf(".")).replace("-", "/")));
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file_path));
                String data, de_data;
                while((data = bufferedReader.readLine())!=null){
                    de_data = encrypt_class.decrypt(data);
                    textView.append(de_data);
                }
                bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(!file2.exists()){
            finish();
        }else if(file_path.isEmpty()){
            Toast.makeText(this, "파일이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onRestart() {
        if(!file2.exists()){
            finish();
        }else if(file_path.isEmpty()){
            Toast.makeText(this, "파일이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.modify_menu:
                Intent intent = new Intent(ReadActivity.this, ModifyActivity.class);
                intent.putExtra("file_path", file_path);
                startActivity(intent);
                break;
            case R.id.delete_menu:
                file = new File(file_path);
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                file.delete();
                                Toast.makeText(getApplicationContext(), "삭제되었습니다.",Toast.LENGTH_SHORT);
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
            case R.id.share:
                Intent intent3 = new Intent(android.content.Intent.ACTION_SEND);
                intent3.setType("text/plain");
                intent3.putExtra(Intent.EXTRA_TEXT, file2);
                Intent chooser = Intent.createChooser(intent3, "친구에게 공유하기");
                startActivity(chooser);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(file_path)){
            try {
                Encrypt_class encrypt_class = new Encrypt_class(ENCRYPT_KEY);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file_path));
                String data, de_data;
                while((data = bufferedReader.readLine())!=null){
                    de_data = encrypt_class.decrypt(data);
                    textView.setText(de_data);
                }
                bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "파일이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }
}

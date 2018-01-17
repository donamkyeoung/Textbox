package com.dnk.project.starwars;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Intent intent1;
    FileListAdapter filelistadapter;
    String title, content;
    String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();
    String default_directory = sdcard_path+"/TextBox/", backup_file = sdcard_path+"/textbox_backup/";
    TextView textView;
    int filelength;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean lock = sharedPreferences.getBoolean("lock", false);

        int permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionAccount = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS);
        if (permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED||permissionAccount==PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS}, 1);
        }
        listView = (ListView) findViewById(R.id.listview);
        textView = findViewById(R.id.textView2);
        filelistadapter = new FileListAdapter();
        if(permissionReadStorage == PackageManager.PERMISSION_GRANTED || permissionWriteStorage == PackageManager.PERMISSION_GRANTED){
            makefile();
            listView.setVisibility(View.VISIBLE);
            filelistadapter.research();
            textView.setVisibility(View.GONE);
            filelistadapter.notifyDataSetChanged();
            listView.setAdapter(filelistadapter);
            filelength = filelistadapter.getFilesLen();
            if(filelistadapter.getCount()==0){
                listView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("메모를 추가해주세요");
            }
        }

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.setVisibility(View.VISIBLE);
                filelistadapter.research();
                textView.setVisibility(View.GONE);
                filelistadapter.notifyDataSetChanged();
                listView.setAdapter(filelistadapter);
                if(filelistadapter.getCount()==0){
                    listView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("메모를 추가해주세요");
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File path = new File(sdcard_path + "/TextBox/");
                File[] files = path.listFiles();
                String file_path = files[i].getAbsolutePath();
                Log.d("MainActivity", file_path);
                if(files[i].getName().endsWith(".textbox")) {
                    Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                    intent.putExtra("file_path", file_path);
                    startActivity(intent);
                    Log.d("MainActivity", "액티비티 시작");
                }else if (files[i].getName().endsWith(".imgbox")){
                    Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
                    intent.putExtra("file_path", file_path);
                    startActivity(intent);
                    Log.d("MainActivity", "액티비티 시작");
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("추가할 메모의 종류를 선택해주세요");
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("이미지");
        ListItems.add("텍스트");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        Intent intent = new Intent(MainActivity.this, ImageChooseActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MainActivity. this, MemoWriteActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        //fab.attachToListView(listView);
        fab.setSize(FloatingActionButton.SIZE_NORMAL);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        intent1 = getIntent();
        Boolean isChecked = intent1.getBooleanExtra("isChecked", false);
        if (!isChecked&&lock) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        } else {
            intent1.removeExtra("isChecked");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean lock = sharedPreferences.getBoolean("lock", false);
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        filelistadapter.notifyDataSetChanged();
        intent1 = getIntent();
        Boolean isChecked = intent1.getBooleanExtra("isChecked", false);
        if(filelistadapter.getCount()==0){
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("메모를 추가해주세요");
        }
        if (!isChecked&&lock) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        } else {
            intent1.removeExtra("isChecked");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Compress_file compress_file = new Compress_file();
        switch (item.getItemId()) {
            case R.id.setting_menu:
                Intent intent = new Intent(MainActivity.this, setting_main.class);
                startActivity(intent);
                break;
            case R.id.info_menu:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("정보");
                builder.setMessage("TextBox 1.0 \nBy Dnk 2017");
                builder.show();
                break;
            case R.id.action_export:
                try {
                    String output = compress_file.zip(default_directory, backup_file);
                    Toast.makeText(MainActivity.this, "내보내기 작업이 완료되었습니다.\n생성된 파일 경로: "+output, Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.action_import:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("경고");
                builder1.setMessage("가져오기 작업을 수행하면 기존에 존재하던 파일과 제목이 같은 파일은 덮어쓰기 됩니다. 계속하시겠습니까?");
                builder1.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showFileChooser();
                    }
                });
                builder1.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder1.show();
                break;
        }
        return true;
    }
    private void showFileChooser() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("application/zip");
        startActivityForResult(Intent.createChooser(i, "파일선택..."), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(data!=null) {
                Uri url = data.getData();
                Toast.makeText(this, url.getPath(), Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", url.getPath());
                String filepath = url.getPath();
                if (filepath.contains(":")) {
                    String tmp[] = filepath.split(":");
                    filepath = tmp[1];
                }
                if (filepath.startsWith("//")) {
                    filepath.substring(0, 1);
                }
                try {
                    Compress_file.unzip(filepath, default_directory, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];
            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "저장 권한이 허용되었습니다.", Toast.LENGTH_SHORT);
                    filelistadapter.research();
                    textView.setVisibility(View.GONE);
                    filelistadapter.notifyDataSetChanged();
                    listView.setAdapter(filelistadapter);
                    if(filelistadapter.getCount()==0){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("메모를 추가해주세요");
                    }
                } else {
                    Toast.makeText(this, "저장 권한이 거부되었습니다.", Toast.LENGTH_SHORT);
                    finish();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void makefile() {
        File dir = new File(sdcard_path + "/TextBox");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

}

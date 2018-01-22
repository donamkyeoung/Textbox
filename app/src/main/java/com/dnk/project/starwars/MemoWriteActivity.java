package com.dnk.project.starwars;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MemoWriteActivity extends AppCompatActivity {
    String title, content;
    EditText title_edit, content_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);

        title_edit = (EditText) findViewById(R.id.text_title);
        content_edit = (EditText) findViewById(R.id.content);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

    }

    public void save(View view) {
        title = title_edit.getText().toString();
        content = content_edit.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(getApplicationContext(), "모든 입력값을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Savefile savefile = new Savefile(title, content);
            if (savefile.isFileExists()) {
                Toast.makeText(getApplicationContext(), "이미 존재하는 제목입니다.", Toast.LENGTH_SHORT).show();
            } else {
                savefile.tofile();
                finish();
            }
        }
    }

    public void cancel(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MemoWriteActivity.this);
        builder.setTitle("취소하시겠습니까?");
        builder.setMessage("작성한 내용이 저장되지 않습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
}

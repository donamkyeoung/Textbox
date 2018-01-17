package com.dnk.project.starwars;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ImageChooseActivity extends AppCompatActivity {
    String filepath = null, title = null;
    TextView chosenImage;
    ImageView imageView;
    EditText imageTitle;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_choose);

        chosenImage = (TextView) findViewById(R.id.chosen_image);
        imageView = (ImageView) findViewById(R.id.preview_image);
        imageTitle = (EditText) findViewById(R.id.image_title);
    }

    public void choose(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(data!=null) {
                Uri uri = data.getData();
                filepath = getRealPathFromURI(uri);
                if (!TextUtils.isEmpty(filepath)) {
                    Log.d("ImageChooseActivity", filepath);
                    file = new File(filepath);
                    chosenImage.setText(file.getName());
                    Log.e("ImageChooseActivity", "id = " + uri.getPath());
                    imageView.setImageURI(uri);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view) {
        title = imageTitle.getText().toString();
        if((TextUtils.isEmpty(title))||(TextUtils.isEmpty(filepath))){
            Toast.makeText(ImageChooseActivity.this, "모든 입력값을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else {
            String fileExt = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
            String fileName = title + "." + fileExt;
            if (!TextUtils.isEmpty(filepath)) {
                ImageSave imageSave = new ImageSave(file, fileName);
                if(imageSave.isFileExists()){
                    Toast.makeText(ImageChooseActivity.this, "제목이 같은 파일이 존재합니다", Toast.LENGTH_SHORT).show();
                }else {
                    imageSave.Save();
                    finish();
                    Toast.makeText(ImageChooseActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void cancel(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ImageChooseActivity.this);
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
    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        Log.d("hello", "getRealPathFromURI(), path : " + uri.toString());

        cursor.close();
        return path;
    }
}

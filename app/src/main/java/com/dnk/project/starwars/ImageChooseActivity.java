package com.dnk.project.starwars;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
    TextView textView;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_choose);

        chosenImage = (TextView) findViewById(R.id.chosen_image);
        imageView = (ImageView) findViewById(R.id.preview_image);
        imageTitle = (EditText) findViewById(R.id.image_title);
        textView = new TextView(this);
        textView.setVisibility(View.GONE);
    }

    public void choose(View view) {
       FileDialog fileDialog = new FileDialog(ImageChooseActivity.this);
        fileDialog.setShowDirectoryOnly(false);
        fileDialog.setListFileFirst(true);
        fileDialog.setFileEndsWith(new String[] {".jpg", ".JPG", ".JPEG", ".jpeg", ".png", ".PNG", ".GIF", ".gif"});
        fileDialog.initDirectory(Environment.getExternalStorageDirectory().toString());
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            @Override
            public void fileSelected(File file, String[] dirs, String[] files) {
                Log.d("ImageChooseActivity", file.getPath());
                chosenImage.setText(file.getName());
                Log.e("ImageChooseActivity", "id = " + file.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imageView.setImageBitmap(bitmap);
                filepath=file.getPath();
                textView.setText(filepath);
            }
        });
        fileDialog.createFileDialog();
        if(filepath!=null){
            file = new File(filepath);
        }
    }

    public void save(View view) {
        String filepath = textView.getText().toString();
        File file = new File(filepath);
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

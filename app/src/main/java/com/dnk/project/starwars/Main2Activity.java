package com.dnk.project.starwars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;

import cat.xojan.numpad.NumPadButton;
import cat.xojan.numpad.NumPadView;
import cat.xojan.numpad.OnNumPadClickListener;

public class Main2Activity extends AppCompatActivity {
    public static Activity activity;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        activity = Main2Activity.this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this);
        final ImageView imgview = (ImageView) findViewById(R.id.imageView);
        final TextView textView = (TextView) findViewById(R.id.textView);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final EditText editText = (EditText) findViewById(R.id.editText4);

        NumPadView numPadView = (NumPadView) findViewById(R.id.custom_number_pad);
        numPadView.setNumberPadClickListener(new OnNumPadClickListener() {
            @Override
            public void onPadClicked(NumPadButton button) {
                Log.d("TAG", button.name());
                if(button.name().equals("CUSTOM_BUTTON_1")){
                    String strTemp = editText.getText().toString();
                    if(!strTemp.isEmpty()) {
                        editText.setText(strTemp.substring(0, strTemp.length() - 1));
                        editText.setSelection(editText.length());
                    }
                }else if(button.name().equals("CUSTOM_BUTTON_2")){
                    if(sharedPreferences.getString("password", "0000").equals(editText.getText().toString())) {
                        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                        intent.putExtra("isChecked", true);
                        startActivity(intent);
                        finish();
                        Reprint.cancelAuthentication();
                    }else{
                        Toast.makeText(Main2Activity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        editText.setText("");
                    }
                }else {
                    switch(button.name()){
                        case "NUM_0":
                            editText.append("0");
                            break;
                        case "NUM_1":
                            editText.append("1");
                            break;
                        case "NUM_2":
                            editText.append("2");
                            break;
                        case "NUM_3":
                            editText.append("3");
                            break;
                        case "NUM_4":
                            editText.append("4");
                            break;
                        case "NUM_5":
                            editText.append("5");
                            break;
                        case "NUM_6":
                            editText.append("6");
                            break;
                        case "NUM_7":
                            editText.append("7");
                            break;
                        case "NUM_8":
                            editText.append("8");
                            break;
                        case "NUM_9":
                            editText.append("9");
                            break;
                    }
                }
            }
        });

        Reprint.initialize(this);

        if(sharedPreferences.getBoolean("fingerprint_use", false)) {
            Reprint.authenticate(new AuthenticationListener() {
                @Override
                public void onSuccess(int moduleTag) {
                    textView.setText("올바른 지문입니다.");
                    imgview.setImageResource(R.drawable.ic_check_circle_black_48dp);
                    vibrator.vibrate(new long[]{100}, -1);
                    Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                    intent.putExtra("isChecked", true);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
                    switch (failureReason) {
                        case AUTHENTICATION_FAILED:
                            textView.setText("올바르지 않은 지문입니다.");
                            imgview.setImageResource(R.drawable.ic_close_black_48dp);
                            vibrator.vibrate(new long[]{100, 100, 100}, -1);
                            break;
                        case LOCKED_OUT:
                            textView.setText("시도 횟수가 너무 많습니다. 잠시 후 시도해주세요.");
                            break;

                    }
                    long saveTime = System.currentTimeMillis();
                    long currTime = 0;
                    while (currTime - saveTime < 3000) {
                        currTime = System.currentTimeMillis();
                    }
                    textView.setText("지문을 인식해주세요.");
                    imgview.setImageResource(R.drawable.ic_fingerprint_black_48dp);

                }
            });
        }else {
            imgview.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }
    public boolean CheckFingerprintAvailable(){
        boolean fingerprintHardware = Reprint.isHardwarePresent();
        boolean fingerprintRegistered = Reprint.hasFingerprintRegistered();
        return fingerprintHardware && fingerprintRegistered;
    }

    public void bug(View view) {
        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        intent.putExtra("isChecked", true);
        startActivity(intent);
        finish();
        Reprint.cancelAuthentication();
    }
}

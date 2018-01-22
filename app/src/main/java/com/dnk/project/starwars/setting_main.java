package com.dnk.project.starwars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.github.ajalt.reprint.core.Reprint;

/**
 * Created by 도남경 on 2018-01-10.
 */

public class setting_main extends PreferenceActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        InputFilter[] FilterArray = new InputFilter[1];

        FilterArray[0] = new InputFilter.LengthFilter(4);
        editText.setFilters(FilterArray);
        SharedPreferences preference = getSharedPreferences("setting", MODE_PRIVATE);

        Preference backup_set =  findPreference("backup_set");
        backup_set.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(setting_main.this, "기능구현중", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        Preference fingerprint_use  = findPreference("fingerprint_use");

        if(Reprint.isHardwarePresent()&&Reprint.hasFingerprintRegistered()){
            fingerprint_use.setEnabled(true);
        }else {
            fingerprint_use.setEnabled(false);
            fingerprint_use.setSummary("지문이 등록되지 않았거나 하드웨어가 지원하지 않습니다.");
        }
        Preference email = findPreference("email");
        email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse("mailto:ttt16872@gmail.com");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(it);
                return false;
            }
        });
    }
}

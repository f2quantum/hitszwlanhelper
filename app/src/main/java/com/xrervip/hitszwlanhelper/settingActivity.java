package com.xrervip.hitszwlanhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class settingActivity extends AppCompatActivity {


    private TextView curUsr;
    private SharedPreferences mSp;
    private EditText id;
    private EditText password;
    private Switch netSwitch;
    private Button save_btn;
    private Button requestPermission_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        id=findViewById(R.id.id);
        password = findViewById(R.id.password);
        netSwitch =findViewById(R.id.switch_wlan);
        curUsr = findViewById(R.id.currentUserText);
        save_btn = findViewById(R.id.button_save);
        requestPermission_btn=findViewById(R.id.button_requestPermission);
        mSp=super.getSharedPreferences("HITszWlanHelper",MODE_PRIVATE);

        reFreshCurUsr();
        netSwitch.setChecked(SharedPreferenceReader("NetWork").equals("UTSZ"));
        save_btn.setOnClickListener(new SaveBtnListener());
        requestPermission_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(settingActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        });

        netSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //控制开关字体颜色
                if (isChecked) {
                    SharedPreferenceWritter("NetWork","UTSZ");
                    Toast.makeText(settingActivity.this, "默认网络已设置为UTSZ", Toast.LENGTH_LONG).show();

                }else {
                    SharedPreferenceWritter("NetWork","HITSZ");
                    Toast.makeText(settingActivity.this, "默认网络已设置为HITSZ", Toast.LENGTH_LONG).show();
                }

            }

        });
    }

    private String SharedPreferenceReader(String key){
        return mSp.getString(key,"HITSZ");
    }

    private void SharedPreferenceWritter(String key,String value){
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private class SaveBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final String str_id = id.getText().toString();
            final String str_password = password.getText().toString();

            SharedPreferences.Editor editor = mSp.edit();
            editor.putString("str_id", str_id);
            editor.putString("str_password", str_password);
            editor.commit();
            Toast.makeText(settingActivity.this, "存储数据成功,请点击刷新", Toast.LENGTH_LONG).show();
            reFreshCurUsr();
            finish();

        }
    }

    private void reFreshCurUsr(){
        curUsr.setText("当前储存账户："+ mSp.getString("str_id","还没有设置账户"));
    }


}

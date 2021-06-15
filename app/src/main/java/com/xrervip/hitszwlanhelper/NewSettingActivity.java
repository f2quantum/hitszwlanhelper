package com.xrervip.hitszwlanhelper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class NewSettingActivity extends AppCompatActivity {

    private ListView lv;
    private TextView curUsr;
    private SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_setting);
        mSp = super.getSharedPreferences("HITszWlanHelper", MODE_PRIVATE);
        lv = (ListView) findViewById(R.id.listview);//得到ListView对象的引用

        /*定义一个动态数组*/
        ArrayList<HashMap<String, Object>> listItemData = new ArrayList<HashMap<String, Object>>();
        /*在数组中存放数据*/
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", "网络准入认证系统配置");
            map.put("ItemText", "当前账户：" + mSp.getString("str_id", "还没有设置账户"));
            listItemData.add(new HashMap<String, Object>(map));
            map.put("ItemTitle", "选择默认登录网络");
            map.put("ItemText", "当前默认网络：" + mSp.getString("NetWork", "HITSZ"));
            listItemData.add(new HashMap<String, Object>(map));
            map.put("ItemTitle", "授予定位权限");
            map.put("ItemText", "定位权限用于程序自动判断当前网络（UTSZ/HITSZ）并前往对应的登录界面");
            listItemData.add(map);
        }
        /*为ListView设置Adapter来绑定数据*/
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItemData,//需要绑定的数据
                R.layout.lv_setting_item,//每一行的布局
                //动态数组中的数据源的键对应到定义布局的View中
                new String[]{"ItemTitle", "ItemText"},
                new int[]{R.id.ItemTitle, R.id.ItemText});

        lv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.login, null);
                        final EditText username = (EditText) view.findViewById(R.id.txt_username);
                        final EditText password = (EditText) view.findViewById(R.id.txt_password);
                        AlertDialog alertDialog0 = new AlertDialog.Builder(NewSettingActivity.this)
                                .setView(view)
                                .setTitle("保存账号和密码")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferenceWritter("str_id", username.getText().toString());
                                        SharedPreferenceWritter("str_password", password.getText().toString());
                                        recreate();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .create();
                        alertDialog0.show();
                        break;
                    case 1:
                        final String[] item = new String[]{"HITSZ", "UTSZ"};
                        final String[] net = new String[1];
                        AlertDialog alertDialog1 = new AlertDialog.Builder(NewSettingActivity.this)
                                .setTitle("请选择网络")//默认为0表示选中第一个项目
                                .setSingleChoiceItems(item, SharedPreferenceReader("NetWork").equals("UTSZ") ? 1 : 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        net[0] = item[which];
                                    }
                                })
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String result = net[0] != null ? net[0] : SharedPreferenceReader("NetWork");
                                        SharedPreferenceWritter("NetWork", result);
                                        Toast.makeText(NewSettingActivity.this, "默认网络已设置为" + result, Toast.LENGTH_LONG).show();
                                        recreate();

                                    }
                                })
                                .setNegativeButton("取消", null)
                                .create();
                        alertDialog1.show();
                        break;
                    case 2:
                        ActivityCompat.requestPermissions(NewSettingActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        break;
                }
            }
        });
    }

    private String SharedPreferenceReader(String key) {
        return mSp.getString(key, "NoWord");
    }

    private void SharedPreferenceWritter(String key, String value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key, value);
        editor.commit();
    }
}


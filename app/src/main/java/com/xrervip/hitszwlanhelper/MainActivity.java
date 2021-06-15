package com.xrervip.hitszwlanhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView webView;
    private String url;
    private SharedPreferences mSp;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);  //使得toolbar支持菜单显示

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolmenu);
        toolbarTitle=findViewById(R.id.toolbar_title_main);
        //添加菜单点击事件

        //final Intent intent_setting=new Intent(this,settingActivity.class);
        final Intent intent_setting=new Intent(this,NewSettingActivity.class);

        final Intent intent_about=new Intent(this,aboutActivity.class);
        final Intent intent_hotpot = new Intent();

        System.out.println(ssid_data());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String str="";
                switch (menuItem.getItemId()){
                    case R.id.setting:
                        startActivity(intent_setting);
                        break;

                    case R.id.about:
                        startActivity(intent_about);
                        break;
                    case R.id.Refresh:
                        recreate();
                        break;

                    case R.id.hotpot:
                        intent_hotpot.addCategory(Intent.CATEGORY_DEFAULT);
                        intent_hotpot.setAction("android.intent.action.MAIN");
                        intent_hotpot.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$TetherSettingsActivity"));
                        startActivity(intent_hotpot);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
                //Toast.makeText(MainActivity.this,str, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        String wifiState= checkNetWorkState();
        Boolean isConnectCampNet = wifiState.equals("已连接HITSZ")|| wifiState.equals("已连接UTSZ")|| wifiState.contains("unknown ssid");
        if(wifiState.contains("unknown ssid")){
            url=Sp_read_network().contains("UTSZ")?"http://10.0.10.66/":"http://10.248.98.2/"; //  UTSZ:HITSZ
            toolbarTitle.setText(Sp_read_network().contains("UTSZ")?"UTSZ":"HITSZ");

        }else {
            url= wifiState.contains("UTSZ")?"http://10.0.10.66/":"http://10.248.98.2/"; //  UTSZ:HITSZ
            toolbarTitle.setText(wifiState.contains("UTSZ")?"UTSZ":"HITSZ");
        }
        if(Sp_read_id().equals("Null")){
            Toast.makeText(MainActivity.this,"首次运行前，请设置账户和密码", Toast.LENGTH_SHORT).show();
            startActivity(intent_setting);
        }
        if (!isConnectCampNet){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(wifiState)
                    .setMessage("当前SSID:"+ssid_data())
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("仍要继续", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this,"好吧", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .setNegativeButton("退出程序", new DialogInterface.OnClickListener() {//添加取消
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNeutralButton("前往WiFi设置", new DialogInterface.OnClickListener() {//添加普通按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(intent_setting);
                        }
                    })
                    .create();
            alertDialog.show();
        }
        initView();




    }

    /**
     * 检查WiFi状态
     * @return 返回当前Wifi状态的语句
     */
    private String  checkNetWorkState(){
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager == null){
            return "WIFI状态异常！";
        }
        int wifiState = wifiManager.getWifiState();
        //检查WIFI状态
        switch (wifiState){
            case  1 :
                return "WIFI网卡不可用";
            case 2:
                return "WIFI正在关闭";
            case 4:
                return "WIFI网卡正在打开";
            case 5:
                return "未知网卡状态";
            default:
                break;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String SSID = wifiInfo.getSSID();
        if (SSID.contains("HITSZ"))
            return "已连接HITSZ";
        else if(SSID.contains("UTSZ"))
            return "已连接UTSZ";
        else if(SSID.contains("<unknown ssid>"))
            return "unknown ssid 请给予定位权限";
        else
            return "未连接到校园网";
    }

    /**
     * 获取wifi名称ssid 和 bssid mac地址 返回wifiInfo对象
     * @return  wifiInfo
     */
    private String ssid_data(){
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    private String Sp_read_id(){
        mSp= super.getSharedPreferences("HITszWlanHelper",MODE_PRIVATE);
         return mSp.getString("str_id","Null");

    }
    private String Sp_read_psd(){
        mSp= super.getSharedPreferences("HITszWlanHelper",MODE_PRIVATE);
        return mSp.getString("str_password","");
    }
    private String Sp_read_network(){
        mSp= super.getSharedPreferences("HITszWlanHelper",MODE_PRIVATE);
        return mSp.getString("NetWork","HITSZ");
    }

    private void initView(){
        webView = (WebView)findViewById(R.id.webView);

        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);


        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                return true;
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(MainActivity.this,"页面加载已完成，", Toast.LENGTH_SHORT).show();
                view.loadUrl("javascript:document.getElementById('username').value = "+ Sp_read_id());
                view.loadUrl("javascript:(function(){document.getElementById('password').value = '"+Sp_read_psd()+"';})() " ) ;
                view.loadUrl("javascript:document.getElementById('login').click()");
                view.loadUrl("javascript:document.getElementById('login-account').click()");

                return;
            }
        });
        //Toast.makeText(MainActivity.this,"首次加载可能需要一段时间，长时间无响应请点击刷新按钮，", Toast.LENGTH_SHORT).show();

        webView.loadUrl(url);
    }


}

package com.example.mengnanchen.mylayouttest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnStart;
    private Button btnEnd;

    private TextView tvStatus;

    private SmsContentObserver smsContentObserver;
    private MissedCallContentObserver missedCallContentObserver;

    public static StringBuilder sbStatus=new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Copyright MengnanChen", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnStart=(Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnEnd=(Button)findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(this);

        tvStatus=(TextView)findViewById(R.id.tvStatus);

        if(checkStatus()){
            btnStart.setEnabled(true);
            btnEnd.setEnabled(true);
        }
        else{
            btnStart.setEnabled(false);
            btnEnd.setEnabled(false);
        }

        smsContentObserver = new SmsContentObserver(MainActivity.this,new Handler());
        missedCallContentObserver=new MissedCallContentObserver(MainActivity.this,new Handler());

        btnEnd.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);
                this.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,true,missedCallContentObserver);

                Toast.makeText(MainActivity.this,"监听中...",Toast.LENGTH_SHORT).show();
                sbStatus.append("状态正常，监听中...\n");
                tvStatus.setText(sbStatus.toString());

                btnStart.setEnabled(false);
                btnEnd.setEnabled(true);
                break;
            case R.id.btnEnd:
                this.getContentResolver().unregisterContentObserver(smsContentObserver);
                this.getContentResolver().unregisterContentObserver(missedCallContentObserver);

                Toast.makeText(MainActivity.this,"已取消监听",Toast.LENGTH_SHORT).show();
                sbStatus.append("状态正常，已取消监听\n");
                tvStatus.setText(sbStatus.toString());

                btnStart.setEnabled(true);
                btnEnd.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private boolean initMailConfig(){
        SharedReferenceHelper sharedReferenceHelper=
                new SharedReferenceHelper(MainActivity.this,Constants.CONFIG_NAME);
        String strMailHost=sharedReferenceHelper
                .getSharedReference(Constants.KEY_MAIL_SEND_HOST);
        String strSendAccount=sharedReferenceHelper
                .getSharedReference(Constants.KEY_MAIL_SEND_ACCOUNT);
        String strSendPassword=sharedReferenceHelper
                .getSharedReference(Constants.KEY_MAIL_SEND_PASSWORD);
        String strReceiveAccount=sharedReferenceHelper
                .getSharedReference(Constants.KEY_MAIL_RECEIVE_ACCOUNT);
        if(strMailHost==null||
                strSendAccount==null||
                strSendPassword==null||
                strReceiveAccount==null||
                strMailHost.trim().equals("")||
                strSendAccount.trim().equals("")||
                strSendPassword.trim().equals("")||
                strReceiveAccount.trim().equals("")){
            Toast.makeText(MainActivity.this,"邮箱配置信息不完整",Toast.LENGTH_SHORT).show();
            sbStatus.append("邮箱配置信息不完整，请到Setting中设置邮箱\n");
            tvStatus.setText(sbStatus.toString());

            return false;
        }
        else{
            Constants.mailHost=strMailHost;
            Constants.sendAccount=strSendAccount;
            Constants.sendPassword=strSendPassword;
            Constants.receiveAccount=strReceiveAccount;

            Toast.makeText(MainActivity.this,"邮箱配置加载完成",Toast.LENGTH_SHORT).show();
            sbStatus.append("邮箱配置加载完成\n");
            tvStatus.setText(sbStatus.toString());

            return true;
        }
    }

    private boolean checkStatus(){
        if(!initMailConfig()){
            return false;
        }else if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            sbStatus.append("状态异常，原因：没有读取通话记录的权限\n");
            tvStatus.setText(sbStatus.toString());
            return false;
        }else if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            sbStatus.append("状态异常，原因：没有读取短信的权限\n");
            tvStatus.setText(sbStatus.toString());
            return false;
        }else if(!isNetworkAvailable()){
            sbStatus.append("状态异常，原因：网络不正常\n");
            tvStatus.setText(sbStatus.toString());
            return false;
        }
        return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) MainActivity.this
                .getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}

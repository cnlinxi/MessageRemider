package com.example.mengnanchen.mylayouttest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by MengnanChen on 2017/9/5.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnSaveConfig;

    private EditText etSendMailHost;
    private EditText etSendAccount;
    private EditText etSendPassword;
    private EditText etReceiveAccount;

    private TextView tvPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_page);

        btnSaveConfig=(Button)findViewById(R.id.btnSaveConfig);
        btnSaveConfig.setOnClickListener(this);

        etSendMailHost=(EditText)findViewById(R.id.etSendMailHost);
        etSendAccount=(EditText)findViewById(R.id.etSendAccount);
        etSendPassword=(EditText)findViewById(R.id.etSendPassword);
        etReceiveAccount=(EditText)findViewById(R.id.etReceiveAccount);

        tvPassword=(TextView)findViewById(R.id.tvPassword);
        tvPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSaveConfig:
                String sendMailHost=etSendMailHost.getText().toString();
                String sendAccount=etSendAccount.getText().toString();
                String sendPassword=etSendPassword.getText().toString();
                String receiveAccount=etReceiveAccount.getText().toString();
                if(sendMailHost.trim().equals("")||
                        sendAccount.trim().equals("")||
                        sendPassword.trim().equals("")||
                        receiveAccount.trim().equals("")){
                    Toast.makeText(SettingActivity.this,"以上信息全部必填",Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedReferenceHelper sharedReferenceHelper=
                        new SharedReferenceHelper(SettingActivity.this,Constants.CONFIG_NAME);
                sharedReferenceHelper.
                        setSharedReference(Constants.KEY_MAIL_SEND_HOST,sendMailHost);
                sharedReferenceHelper.
                        setSharedReference(Constants.KEY_MAIL_SEND_ACCOUNT,sendAccount);
                sharedReferenceHelper.
                        setSharedReference(Constants.KEY_MAIL_SEND_PASSWORD,sendPassword);
                sharedReferenceHelper.
                        setSharedReference(Constants.KEY_MAIL_RECEIVE_ACCOUNT,receiveAccount);

                Toast.makeText(SettingActivity.this,"保存成功",Toast.LENGTH_SHORT).show();

                Intent intent=new Intent();
                intent.setClass(SettingActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.tvPassword:
                if(etSendPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    etSendPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                else{
                    etSendPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;
            default:
                break;
        }
    }
}

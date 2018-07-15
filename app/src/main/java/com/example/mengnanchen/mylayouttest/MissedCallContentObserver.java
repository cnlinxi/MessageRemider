package com.example.mengnanchen.mylayouttest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import javax.mail.MessagingException;

/**
 * Created by MengnanChen on 2017/9/5.
 */

public class MissedCallContentObserver extends ContentObserver {
    private Context mContext;
    private Cursor cursor = null;

    private String strMailHost;
    private String strSendAccount;
    private String strSendPassword;
    private String strReceiveAccount;

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case Constants.ID_MSG_SUCCESS:
                    Bundle data = msg.getData();
                    String messageContent = data.getString(Constants.MSG_SUCCESS);
                    Toast.makeText(mContext, messageContent, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.ID_MSG_FAILED:
                    Bundle data_Failed=msg.getData();
                    String messageContent_Failed=data_Failed.getString(Constants.MSG_FAILED);
                    Toast.makeText(mContext,messageContent_Failed,Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public MissedCallContentObserver(Context context,Handler handler) {
        super(handler);

        this.mContext=context;
    }

    @Override
    public void onChange(boolean selfChange) {
        try{
            super.onChange(selfChange);

            if (ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                Log.e("missed call error","missed call error:permission defined");
                return;
            }

            StringBuilder sb=new StringBuilder();
            cursor = mContext.getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE, CallLog.Calls.NEW,CallLog.Calls.DATE}, null, null,CallLog.Calls.DEFAULT_SORT_ORDER);

            if(cursor!=null&&cursor.getCount()>0){
                while(cursor.moveToNext()){
                    if(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))==CallLog.Calls.MISSED_TYPE) {
                        if (cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW)) == 1) {
                            sb.append("MissedCall|PhoneNumber="+cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))+
                                    "|Time="+GetDateStringFromString(cursor.getString(cursor.getColumnIndex("date")))
                                    +"<br>--------------------------------------------------------------------<br>");
                        }
                    }
                }
                cursor.close();
                Log.i("missed call","missed call onChange:"+sb.toString());
                SendMail(sb.toString());
            }
            else{
                Log.i("missed call","missed call onChange: failed");
            }
        }
        catch (Exception e){}

    }

    public static String GetDateStringFromString(String strEpoch){
        String strSubEpoch=strEpoch.substring(0,10);
        long epoch=Long.parseLong(strSubEpoch);
        return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(new java.util.Date (epoch*1000));
    }

    public void SendMail(final String content){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initMailSenderConfig();

                    MailSender sender = new MailSender();
                    sender.setProperties(strMailHost, "25");
                    sender.setContent(strSendAccount,"我的未接来电", content);
                    sender.setReceiver(new String[]{strReceiveAccount});
                    sender.sendMail(strMailHost, strSendAccount, strSendPassword);

                    Message msg = new Message();
                    msg.what = Constants.ID_MSG_SUCCESS;
                    Bundle data = new Bundle();
                    data.putString(Constants.MSG_SUCCESS, "Missed Call|邮件已发送");
                    msg.setData(data);

                    mHandler.sendMessage(msg);
                } catch (MessagingException e) {
                    e.printStackTrace();

                    Message msg=new Message();
                    msg.what = Constants.ID_MSG_FAILED;
                    Bundle data = new Bundle();
                    data.putString(Constants.MSG_FAILED, e.getLocalizedMessage());
                    msg.setData(data);

                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private boolean initMailSenderConfig() {
        strMailHost=Constants.mailHost;
        strSendAccount=Constants.sendAccount;
        strSendPassword=Constants.sendPassword;
        strReceiveAccount=Constants.receiveAccount;

        return true;
    }
}

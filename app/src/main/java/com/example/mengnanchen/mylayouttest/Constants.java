package com.example.mengnanchen.mylayouttest;

/**
 * Created by MengnanChen on 2017/9/5.
 */

public class Constants {
    public static String mailHost;
    public static String sendAccount;
    public static String sendPassword;
    public static String receiveAccount;

    /*public String getMailHost(){
        return mailHost;
    }
    public void setMailHost(String mailHost){
        this.mailHost=mailHost;
    }

    public String getSendAccount(){
        return sendAccount;
    }
    public void setSendAccount(String sendAccount){
        this.sendAccount=sendAccount;
    }

    public String getSendPassword(){
        return sendPassword;
    }
    public void setSendPassword(String sendPassword){
        this.sendPassword=sendPassword;
    }

    public String getReceiveAccount(){
        return receiveAccount;
    }
    public void setReceiveAccount(String receiveAccount){
        this.receiveAccount=receiveAccount;
    }*/

    public static final String CONFIG_NAME="config";
    public static final String KEY_MAIL_SEND_HOST ="MailSendHost";
    public static final String KEY_MAIL_SEND_ACCOUNT ="MailSendAccount";
    public static final String KEY_MAIL_SEND_PASSWORD ="MailSendPassword";
    public static final String KEY_MAIL_RECEIVE_ACCOUNT ="MailReceiveAccount";

    public static final String MSG_SUCCESS = "MSG_SUCCESS";
    public static final int ID_MSG_SUCCESS = 1;
    public static final String MSG_FAILED="MSG_FAILED";
    public static final int ID_MSG_FAILED=2;
}

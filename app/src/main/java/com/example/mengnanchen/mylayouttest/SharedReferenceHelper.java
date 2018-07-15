package com.example.mengnanchen.mylayouttest;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MengnanChen on 2017/9/5.
 */

public class SharedReferenceHelper {
    private Context mContext;
    private String mConfigName;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public SharedReferenceHelper(Context context, String ConfigName) {
        mContext=context;
        mConfigName=ConfigName;

        sharedPreferences=mContext.getSharedPreferences(mConfigName,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void setSharedReference(String strKey, String strValue){
        editor.putString(strKey,strValue);
        editor.commit();
    }

    public void removeSharedReference(String strKey){
        editor.remove(strKey);
        editor.commit();
    }

    public String getSharedReference(String strKey){
        return sharedPreferences.getString(strKey,null);
    }
}

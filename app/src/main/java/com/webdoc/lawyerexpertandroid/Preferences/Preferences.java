package com.webdoc.lawyerexpertandroid.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by WaleedPCC on 4/29/2019.
 */

public class Preferences
{
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    private static Boolean KEY_LOGIN = false;
    private static String KEY_EMAIL="email";
    private static String KEY_PASSWORD="password";
    private static String KEY_APPLICATION_USER_ID="ApplicationUserId";
    //private static LoginResponse KEY_LOGIN_RESPONSE = null;


    public Preferences(Context cont)
    {
        this.ctx=cont;
        prefs=ctx.getSharedPreferences("Lawyer-Expert", Context.MODE_PRIVATE);
        editor=prefs.edit();
    }

    public Boolean getKeyLogin() {
        return(prefs.getBoolean(String.valueOf(KEY_LOGIN), false));
    }

    public void setKeyLogin(Boolean keyLogin) {

        editor.putBoolean(String.valueOf(KEY_LOGIN), keyLogin);
        editor.commit();
    }

    public String getKeyEmail() {
        return(prefs.getString(KEY_EMAIL, null));
    }

    public void setKeyEmail(String keyEmail) {
        editor.putString(KEY_EMAIL, keyEmail);
        editor.commit();
    }


    public String getKeyPassword() {
        return(prefs.getString(KEY_PASSWORD, null));

    }

    public void setKeyPassword(String keyPassword) {
        editor.putString(KEY_PASSWORD, keyPassword);
        editor.commit();
    }

    public String getKeyApplicationUserId() {
        return(prefs.getString(KEY_APPLICATION_USER_ID, null));

    }

    public void setKeyApplicationUserId(String keyApplicationUserId) {
        editor.putString(KEY_APPLICATION_USER_ID, keyApplicationUserId);
        editor.commit();
    }

    public void clearSharedPreferences() {
        editor.clear();
        editor.commit();
    }
}

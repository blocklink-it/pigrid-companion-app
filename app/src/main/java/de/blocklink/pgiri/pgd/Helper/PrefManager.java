package de.blocklink.pgiri.pgd.Helper;

import android.content.Context;
import android.content.SharedPreferences;



public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_HOME_BACK_BTN_PRESSED = "IsHomeBackBtnPressed";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setHomeBackBtnPressed(boolean pressed) {
        editor.putBoolean(IS_HOME_BACK_BTN_PRESSED, pressed);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isHomeBackBtnPressed() {
        return pref.getBoolean(IS_HOME_BACK_BTN_PRESSED, false);
    }

    public void clearPref() {
        pref.edit().clear().commit();

    }
}

package tech.qeedji.url_webview;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {
    public static final String PREF_URL = "url";
    public static final String PREF_START_AFTER_BOOT_COMPLETED = "start_after_boot_completed";
    public static final String PREF_AUTOREFRESH_URL_ENABLED = "autorefresh_url_enabled";
    public static final String PREF_AUTOREFRESH_URL_INTERVAL = "autorefresh_url_interval";
    public static final String PREF_CREDENTIAL = "credential";

    public static final String DEFAULT_URL = "";
    public static final Boolean DEFAULT_START_AFTER_BOOT_COMPLETED = true;
    public static final Boolean DEFAULT_AUTOREFRESH_URL_ENABLED = false;
    public static final long DEFAULT_AUTOREFRESH_URL_INTERVAL = 60;
    public static final String DEFAULT_CREDENTIAL = "";

    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;

    public AppSharedPreferences(Context c) {
        mContext = c;
        mSharedPreferences = mContext.getSharedPreferences(
                mContext.getPackageName() + ".prefs", Context.MODE_PRIVATE);
    }

    public void init() {
        if (!mSharedPreferences.contains(PREF_START_AFTER_BOOT_COMPLETED)) {
            android.content.SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(PREF_START_AFTER_BOOT_COMPLETED, DEFAULT_START_AFTER_BOOT_COMPLETED);
            editor.commit();
        }
        if (!mSharedPreferences.contains(PREF_AUTOREFRESH_URL_ENABLED)) {
            android.content.SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(PREF_AUTOREFRESH_URL_ENABLED, DEFAULT_AUTOREFRESH_URL_ENABLED);
            editor.commit();
        }
        if (!mSharedPreferences.contains(PREF_AUTOREFRESH_URL_INTERVAL)) {
            android.content.SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong(PREF_AUTOREFRESH_URL_INTERVAL, DEFAULT_AUTOREFRESH_URL_INTERVAL);
            editor.commit();
        }
        if (!mSharedPreferences.contains(PREF_URL)) {
            android.content.SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(PREF_URL, DEFAULT_URL);
            editor.commit();
        }
        if (!mSharedPreferences.contains(PREF_CREDENTIAL)) {
            android.content.SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(PREF_CREDENTIAL, DEFAULT_CREDENTIAL);
            editor.commit();
        }
    }

    public String getString(String key, String def) {
        return mSharedPreferences.getString(key, def);
    }

    public Long getLong(String key, Long def) {
        return mSharedPreferences.getLong(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return mSharedPreferences.getBoolean(key, def);
    }
}

package tech.qeedji.url_webview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static tech.qeedji.url_webview.AppSharedPreferences.DEFAULT_START_AFTER_BOOT_COMPLETED;
import static tech.qeedji.url_webview.AppSharedPreferences.PREF_START_AFTER_BOOT_COMPLETED;

public class StartActivityAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            AppSharedPreferences appSharedPreferences = new AppSharedPreferences(context);
            appSharedPreferences.init();
            if (appSharedPreferences.getBoolean(PREF_START_AFTER_BOOT_COMPLETED,
                    DEFAULT_START_AFTER_BOOT_COMPLETED) == true) {
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}

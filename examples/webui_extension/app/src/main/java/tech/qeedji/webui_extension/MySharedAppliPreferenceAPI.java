package tech.qeedji.webui_extension;

import static tech.qeedji.webui_extension.MainActivity.DEFAULT_DISPLAY_TEXT;
import static tech.qeedji.webui_extension.MainActivity.DISPLAY_TEXT_KEY;
import static tech.qeedji.webui_extension.MainActivity.PREFERENCES_FILE;

import android.content.Context;
import android.content.Intent;

import tech.qeedji.system.lib.SharedPreferenceAPI;

public class MySharedAppliPreferenceAPI extends SharedPreferenceAPI {

    @Override
    public String getPreferenceAuthority() {
        return "tech.qeedji.webui_extension";
    }

    @Override
    public Object[][] initPreferences() {
        Object[][] preferences = {
            // filename, key, access, type, default_value
            {PREFERENCES_FILE, DISPLAY_TEXT_KEY, "rw", String.class, DEFAULT_DISPLAY_TEXT},
        };
        return preferences;
    }

    @Override
    protected void prefModified(String filename, String key, String type) {
        super.prefModified(filename, key, type);
        // Create an intent for the main activity to update the text when the preference is modified.
        Context context = this.getContext();
        final Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}

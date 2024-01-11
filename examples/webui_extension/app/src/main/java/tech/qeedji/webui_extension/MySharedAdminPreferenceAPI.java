package tech.qeedji.webui_extension;

import static tech.qeedji.webui_extension.MainActivity.BACKGROUND_COLOR_KEY;
import static tech.qeedji.webui_extension.MainActivity.DEFAULT_BACKGROUND_COLOR;
import static tech.qeedji.webui_extension.MainActivity.PREFERENCES_FILE;

import tech.qeedji.system.lib.SharedPreferenceAPI;

public class MySharedAdminPreferenceAPI extends SharedPreferenceAPI {

    @Override
    public String getPreferenceAuthority() {
        return "tech.qeedji.webui_extension.admin";
    }

    @Override
    public Object[][] initPreferences() {
        Object[][] preferences = {
            // filename, key, access, type, default_value
            {PREFERENCES_FILE, BACKGROUND_COLOR_KEY, "rw", String.class, DEFAULT_BACKGROUND_COLOR},
        };
        return preferences;
    }
}

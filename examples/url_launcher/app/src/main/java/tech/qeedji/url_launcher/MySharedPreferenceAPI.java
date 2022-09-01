package tech.qeedji.url_launcher;

import tech.qeedji.system.lib.SharedPreferenceAPI;

public class MySharedPreferenceAPI extends SharedPreferenceAPI {

    @Override
    public String getPreferenceAuthority() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    public Object[][] initPreferences() {
        Object[][] preferences = {
            // filename, key, access, type, default_value
            {"tech.qeedji.url_launcher.prefs", "start_after_boot_completed", "rw", boolean.class, true},
            {"tech.qeedji.url_launcher.prefs", "autorefresh_url_enabled", "rw", boolean.class, false},
            {"tech.qeedji.url_launcher.prefs", "autorefresh_url_interval", "rw", long.class, 10L},
            {"tech.qeedji.url_launcher.prefs", "url", "rw", String.class, ""},
            {"tech.qeedji.url_launcher.prefs", "credential", "rw", String.class, ""},
            {"tech.qeedji.url_launcher.credential.*.prefs", "type", "rw", String.class, "HttpAuth"},
            {"tech.qeedji.url_launcher.credential.*.prefs", "username", "rw", String.class, ""},
            {"tech.qeedji.url_launcher.credential.*.prefs", "password", "wo", String.class, ""},
        };
        return preferences;
    }
}

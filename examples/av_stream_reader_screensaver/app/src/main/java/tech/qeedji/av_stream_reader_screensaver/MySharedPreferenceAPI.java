package tech.qeedji.av_stream_reader_screensaver;

import static tech.qeedji.av_stream_reader_screensaver.Screensaver.DEFAULT_URL;
import static tech.qeedji.av_stream_reader_screensaver.Screensaver.PREFERENCES_FILE;

import tech.qeedji.system.lib.SharedPreferenceAPI;

public class MySharedPreferenceAPI extends SharedPreferenceAPI {

    @Override
    public String getPreferenceAuthority() {
        return "tech.qeedji.av_stream_reader_screensaver";
    }

    @Override
    public Object[][] initPreferences() {
        Object[][] preferences = {
                // filename, key, access, type, default_value
                {PREFERENCES_FILE, "url", "rw", String.class, DEFAULT_URL},
        };
        return preferences;
    }
}

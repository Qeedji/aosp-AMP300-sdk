package tech.qeedji.url_launcher;

import android.content.Context;
import android.content.Intent;

public class CustomExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private final Context aContext;
    private final Class<?> aActivityClass;

    public CustomExceptionHandler(Context context, Class<?> c) {
        aContext = context;
        aActivityClass = c;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        Intent intent = new Intent(aContext, aActivityClass);
        aContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

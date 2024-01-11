package tech.qeedji.power_manager_calendar;

import android.content.Context;
import android.util.Log;

import tech.qeedji.system.lib.PowerManager;
import tech.qeedji.system.lib.PowerManagerCalendar;
import tech.qeedji.system.lib.PowerManagerCalendarListener;

public class PowerManagerTask implements Runnable {
    private PowerManagerCalendar powerManagerCalendar = null;
    private PowerManager powerManager = null;
    private Context mContext = null;
    private Boolean mIsForWakeUp = false;
    public PowerManagerTask(Context context, boolean isForWakeUp) {
        mContext = context;
        mIsForWakeUp = isForWakeUp;
        powerManager = new PowerManager(context);
        powerManagerCalendar = new PowerManagerCalendar(mContext);
        // Deactivate other potential calendars like the OS tasks.
        if (powerManagerCalendar.isActivated()) {
            Log.d("PowerManagerCalendar", "PowerManagerCalendar deactivate.");
            powerManagerCalendar.deactivate();
        }
        // Create a listener to deactivate other potential calendars from other applications.
        PowerManagerCalendarListener listener = new PowerManagerCalendarListener() {
            @Override
            public void onActivate() {
                Log.d("PowerManagerCalendar", "PowerManagerCalendar onActivate received.");
                powerManagerCalendar.deactivate();
            }

            @Override
            public void onDeactivate() {
                Log.d("PowerManagerCalendar", "PowerManagerCalendar onDeactivate received.");
            }
        };
        powerManagerCalendar.registerListener(listener);
    }

    @Override
    public void run() {
        if (mIsForWakeUp) {
            powerManager.wakeUp();
        } else {
            powerManager.goToSleep();
        }
    }
}

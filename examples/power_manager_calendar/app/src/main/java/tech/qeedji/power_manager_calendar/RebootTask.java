package tech.qeedji.power_manager_calendar;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import tech.qeedji.system.lib.RebootCalendar;
import tech.qeedji.system.lib.RebootCalendarListener;

public class RebootTask implements Runnable {
    private RebootCalendar mRebootCalendar = null;
    private Context mContext = null;
    private RebootCalendarListener mListener = null;
    private PowerManager mOSPowerManager;

    public RebootTask(Context context) {
        mContext = context;
        mOSPowerManager = (android.os.PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mRebootCalendar = new RebootCalendar(mContext);
        // Deactivate other potential calendars like the OS tasks.
        if (mRebootCalendar.isActivated()) {
            Log.d("RebootCalendar", "RebootCalendar deactivate.");
            mRebootCalendar.deactivate();
        }
        mListener = new RebootCalendarListener() {
            @Override
            public void onActivate() {
                Log.d("RebootCalendar", "RebootCalendar onActivate received.");
                mRebootCalendar.deactivate();
            }

            @Override
            public void onDeactivate() {
                Log.d("RebootCalendar", "RebootCalendar onDeactivate received.");
            }
        };
        mRebootCalendar.registerListener(mListener);
    }

    @Override
    public void run() {
        mOSPowerManager.reboot("Reboot App");
    }
}

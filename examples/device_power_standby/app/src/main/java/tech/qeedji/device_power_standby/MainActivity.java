package tech.qeedji.device_power_standby;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DevicePowerStandby";
    private static final int TIMEOUT = 10000;

    private Button mButton = null;
    private PowerManager.WakeLock mWakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(onClick);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DevicePowerStandby::WakeLock");
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        public void onClick(View v) {
            goToSleep();
            new Thread("Thread Device Power Standby") {
                @Override
                public void run() {
                    Log.d(TAG, "Thread run");
                    try {
                        sleep(TIMEOUT);
                        wakeUp();
                    } catch (Exception e) {
                        Log.d(TAG, "Thread exiting");
                    }
                    Log.d(TAG, "Thread exit");
                }
            }.start();
        }
    };

    private void goToSleep(){
        try{
            Class c = Class.forName("android.os.PowerManager");
            PowerManager mPowerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            for(Method m : c.getDeclaredMethods()){
                if(m.getName().equals("goToSleep")){
                    m.setAccessible(true);
                    if(m.getParameterTypes().length == 1){
                        mWakeLock.acquire();
                        m.invoke(mPowerManager, SystemClock.uptimeMillis());
                        Log.d(TAG, "goToSleep ok");
                    }
                }
            }
        } catch (Exception e){
            Log.e(TAG, "goToSleep error " + e.getMessage());
        }
    }
    public void wakeUp(){
        try{
            Class c = Class.forName("android.os.PowerManager");
            PowerManager  mPowerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            for(Method m : c.getDeclaredMethods()){
                if(m.getName().equals("wakeUp")){
                    m.setAccessible(true);
                    if(m.getParameterTypes().length == 1){
                        m.invoke(mPowerManager, SystemClock.uptimeMillis());
                        mWakeLock.release();
                        Log.d(TAG, "wakeUp ok");
                    }
                }
            }
        } catch (Exception e){
            Log.e(TAG, "wakeUp error " + e.getMessage());
        }
    }
}

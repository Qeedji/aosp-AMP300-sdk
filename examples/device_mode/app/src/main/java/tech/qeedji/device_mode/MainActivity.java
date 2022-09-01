package tech.qeedji.device_mode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tech.qeedji.system.lib.DeviceMode;
import tech.qeedji.system.lib.DeviceModeListener;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_SYSTEM_BUTTON = "android.intent.action.SYSTEM_BUTTON";

    private Activity mActivity;
    private TextView mTextView;
    private DeviceMode mDeviceMode;
    private DeviceModeListener mDeviceModeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mDeviceMode = new DeviceMode(this);
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText(intToStringDeviceMode(mDeviceMode.getValue()));
        mDeviceModeListener = new DeviceModeListener() {
            @Override
            public void onDeviceModeChanged(int oldDeviceMode, int newDeviceMode) {
                final String text = intToStringDeviceMode(newDeviceMode);
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(text);
                    }
                });
            }
        };
        mDeviceMode.registerListener(mDeviceModeListener);
        Button buttonNative = (Button) findViewById(R.id.buttonNative);
        buttonNative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeviceMode.setValue(DeviceMode.NATIVE);
            }
        });
        Button buttonKiosk = (Button) findViewById(R.id.buttonKiosk);
        buttonKiosk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeviceMode.setValue(DeviceMode.KIOSK);
            }
        });
        Button buttonEnd = (Button) findViewById(R.id.buttonEnd);
        buttonEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeviceMode.unregisterListener(mDeviceModeListener);
                mActivity.finish();
            }
        });
        // Listen system button
        BroadcastReceiver br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ACTION_SYSTEM_BUTTON);
        this.registerReceiver(br, filter);
    }

    private String intToStringDeviceMode(int value) {
        if (value == DeviceMode.NATIVE) {
            return "Native";
        } else if (value == DeviceMode.KIOSK) {
            return "Kiosk";
        }
        return "Invalid";
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SYSTEM_BUTTON.equals(intent.getAction())) {
                DeviceMode deviceMode = new DeviceMode(getApplicationContext());
                if (deviceMode.getValue() == deviceMode.KIOSK) {
                    deviceMode.setValue(deviceMode.NATIVE);
                    sleep(2);
                } else {
                    deviceMode.setValue(deviceMode.KIOSK);
                    sleep(2);
                }
            }
        }
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

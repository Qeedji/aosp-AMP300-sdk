package tech.qeedji.tablet.system_button;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "system_button";
    private static final String ACTION_SYSTEM_BUTTON = "android.intent.action.SYSTEM_BUTTON";

    private TextView mTextView = null;
    private Long mCount = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);
        mCount = 0l;
        BroadcastReceiver br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ACTION_SYSTEM_BUTTON);
        this.registerReceiver(br, filter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SYSTEM_BUTTON.equals(intent.getAction())) {
                mCount++;
                Log.d(TAG, "System button detected");
                Toast.makeText(context, "System button detected", Toast.LENGTH_SHORT).show();
                mTextView.setText(mCount.toString());
            }
        }
    }
}

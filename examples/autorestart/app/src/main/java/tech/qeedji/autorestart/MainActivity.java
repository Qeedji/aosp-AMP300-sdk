package tech.qeedji.autorestart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(
                new CustomExceptionHandler(this, MainActivity.class));
        Button button = findViewById(R.id.button);
        button.setOnClickListener(onClick);
        toast("Create MainActivity");
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        public void onClick(View v) {
            // crash
            PowerManager powerManager = null;
            powerManager.reboot("reboot");
        }
    };

    /* Show an android message */
    private final Handler mHandler = new Handler();
    private void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }
}

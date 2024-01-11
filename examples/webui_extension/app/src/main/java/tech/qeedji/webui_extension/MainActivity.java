package tech.qeedji.webui_extension;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String PREFERENCES_FILE = "tech.qeedji.webui_extension.prefs";
    public static final String DISPLAY_TEXT_KEY = "display_text";
    public static final String DEFAULT_DISPLAY_TEXT = "This is my default text.";
    public static final String BACKGROUND_COLOR_KEY = "background_color";
    public static final String DEFAULT_BACKGROUND_COLOR = "#0000ff";
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        updateBackgroundColor();
        updateText();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateText();
    }

    private void updateText() {
        TextView mainTextView = findViewById(R.id.mainText);
        String newText = getText();
        mainTextView.setText(newText);
    }

    private String getText() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DISPLAY_TEXT_KEY, DEFAULT_DISPLAY_TEXT);
    }

    private void updateBackgroundColor() {
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        int color = getColor();
        mainLayout.setBackgroundColor(color);
    }

    private int getColor() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String color = sharedPreferences.getString(BACKGROUND_COLOR_KEY, DEFAULT_BACKGROUND_COLOR);
        return Color.parseColor(color);
    }
}
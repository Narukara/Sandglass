package com.narukara.lunadial;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    private static boolean allowNotification = false;
    private static boolean allowModifyType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.grey));

        SwitchCompat switch1 = findViewById(R.id.switch1);
        switch1.setChecked(allowNotification);
        SwitchCompat switch2 = findViewById(R.id.switch2);
        switch2.setChecked(allowModifyType);
    }

    static public void loadSettings() {
        // Pen.read will return null if there is no "key", so default value is false
        try {
            allowNotification = "yes".equals(Pen.read(Pen.cache, "allow_notification"));
        } catch (IOException e) {
            allowNotification = false;
        }
        try {
            allowModifyType = "yes".equals(Pen.read(Pen.cache, "allow_modify_type"));
        } catch (IOException e) {
            allowModifyType = false;
        }
    }

    public static boolean isAllowModifyType() {
        return allowModifyType;
    }

    public static boolean isAllowNotification() {
        return allowNotification;
    }

    public void settingOnclick(View view) {
        switch (view.getId()) {
            case R.id.switch1:
                try {
                    Pen.write(Pen.cache, "allow_notification", ((SwitchCompat) view).isChecked() ? "yes" : "no");
                } catch (IOException e) {
                    Snackbar.make(findViewById(R.id.settings_bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.switch2:
                try {
                    Pen.write(Pen.cache, "allow_modify_type", ((SwitchCompat) view).isChecked() ? "yes" : "no");
                } catch (IOException e) {
                    Snackbar.make(findViewById(R.id.settings_bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.switch3:
                Snackbar.make(findViewById(R.id.settings_bg), "这个功能还没有实现！", Snackbar.LENGTH_LONG).show();
                break;

        }
        loadSettings();
    }

    public void backOnClick(View view) {
        finish();
    }
}
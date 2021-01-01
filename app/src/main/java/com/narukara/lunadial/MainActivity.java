package com.narukara.lunadial;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private Timer timer = null;
    private static final String channelID = "Sandglass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            penInit();
            Sandglass.reload();
        } catch (IOException e) {
            Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1);
        setBackground();
        setTimer();
    }

    @Override
    protected void onStop() {
        stopTimer();
        sendNotification();
        super.onStop();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.smallicon)
                .setContentTitle(Sandglass.isRunning() ? Acts.getActName(Sandglass.getID()) : getString(R.string.app_name))
                .setContentText("~ forget-me-not ~")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setOngoing(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void penInit() throws IOException {
        Pen.cache = new File(getCacheDir(), "cache");
        Pen.fileDir = getFilesDir();
        if (!Pen.cache.exists()) {
            Pen.cache.createNewFile();
        }
    }

    private void setTimer() {
        if (Sandglass.isRunning() && timer == null) {
            timer = new Timer();
            ((TextView) findViewById(R.id.IDView)).setText(Acts.getActName(Sandglass.getID()));
            final TextView timeView = findViewById(R.id.TimeView);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timeView.post(() -> {
                        int[] duration = Sandglass.getDuration();
                        String string = duration[0] + "h " + duration[1] + "min";
                        timeView.setText(string);
                    });
                }
            }, 0, 10 * 1000);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void setBackground() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        if (time > 21 || time < 6) {
            findViewById(R.id.bg).setBackground(getDrawable(R.drawable.night));
            window.setStatusBarColor(this.getResources().getColor(R.color.nightColor));
        } else {
            findViewById(R.id.bg).setBackground(getDrawable(R.drawable.day));
            window.setStatusBarColor(this.getResources().getColor(R.color.dayColor));
        }
    }

    private void end() {
        stopTimer();
        ((TextView) findViewById(R.id.IDView)).setText(getString(R.string.app_name));
        ((TextView) findViewById(R.id.TimeView)).setText("");
        try {
            Sandglass.end();
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
    }

    public void fab(final View view) {
        if (Sandglass.isRunning()) {
            end();
        }
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.startmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int id;
            switch (item.getItemId()) {
                case R.id.sleep:
                    id = Acts.SLEEP;
                    break;
                case R.id.eat:
                    id = Acts.EAT;
                    break;
                case R.id.clean:
                    id = Acts.CLEAN;
                    break;
                case R.id.study:
                    id = Acts.STUDY;
                    break;
                case R.id.fun:
                    id = Acts.FUN;
                    break;
                case R.id.other:
                    id = Acts.OTHER;
                    break;
                default:
                    id = -1;
            }
            try {
                Sandglass.start(id);
                setTimer();
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
            }
            return false;
        });
        popupMenu.show();
    }

    private void stat() {
        File file = new File(Pen.fileDir, Tools.getYear() + ".xls");
        if (!file.exists()) {
            Snackbar.make(findViewById(R.id.bg), "暂无统计信息", Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(), "com.narukara.lunadial.fileProvider", file));
        intent.setType("application/vnd.ms-excel");
        startActivity(intent);
    }

    public void changeID(View view) {
        if (!Sandglass.isRunning()) {
            return;
        }
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.startmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int id;
            switch (item.getItemId()) {
                case R.id.sleep:
                    id = Acts.SLEEP;
                    break;
                case R.id.eat:
                    id = Acts.EAT;
                    break;
                case R.id.clean:
                    id = Acts.CLEAN;
                    break;
                case R.id.study:
                    id = Acts.STUDY;
                    break;
                case R.id.fun:
                    id = Acts.FUN;
                    break;
                case R.id.other:
                    id = Acts.OTHER;
                    break;
                default:
                    id = -1;
            }
            try {
                Sandglass.changeID(id);
                ((TextView) findViewById(R.id.IDView)).setText(Acts.getActName(Sandglass.getID()));
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
            }
            return false;
        });
        popupMenu.show();
    }

    public void dotsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.dotsmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.stat:
                    stat();
                    break;
                case R.id.about:
                    Snackbar.make(findViewById(R.id.bg), "Hello! Sandglass 1.5.0 beta", Snackbar.LENGTH_LONG).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
}
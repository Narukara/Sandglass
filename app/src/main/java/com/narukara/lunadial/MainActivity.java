package com.narukara.lunadial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private int id;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            penInit();
            Sandglass.reload();
            reloadID();
        } catch (IOException e) {
            Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
        setTimer();
    }

    private void reloadID() throws IOException {
        String string = Pen.read(Pen.cache, "id");
        if (string == null) {
            id = -1;
        } else {
            id = Integer.parseInt(string);
        }
    }

    private void penInit() throws IOException {
        Pen.cache = new File(getCacheDir(), "cache");
        Pen.fileDir = getFilesDir();
        if (!Pen.cache.exists()) {
            Pen.cache.createNewFile();
        }
    }

    private void setTimer() {
        if (id != -1 && timer == null) {
            refresh();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBackground();
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


    public void fab(final View view) {
        final Context context = this;
        PopupMenu popupMenu = new PopupMenu(this, view);
        if (id != -1) {
            //end or stat
            popupMenu.getMenuInflater().inflate(R.menu.endmenu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.end:
                        if (timer != null) {
                            timer.cancel();
                        }
                        ((TextView) findViewById(R.id.textView)).setText(getString(R.string.app_name));
                        try {
                            Pen.write(Pen.cache, "id", "-1");
                            Recorder.commit(id, Sandglass.end());
                        } catch (Exception e) {
                            Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
                        }
                        id = -1;
                        break;
                    case R.id.stat:
                        stat();
                        break;
                }
                return false;
            });
        } else {
            //start or stat
            popupMenu.getMenuInflater().inflate(R.menu.startmenu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.start:
                        //stable or act
                        PopupMenu popupMenu1 = new PopupMenu(context, view);
                        popupMenu1.getMenuInflater().inflate(R.menu.menu1, popupMenu1.getMenu());
                        popupMenu1.setOnMenuItemClickListener(item1 -> {
                            switch (item1.getItemId()) {
                                case R.id.stable:
                                    //in stable
                                    PopupMenu popupMenu2 = new PopupMenu(context, view);
                                    popupMenu2.getMenuInflater().inflate(R.menu.menus, popupMenu2.getMenu());
                                    popupMenu2.setOnMenuItemClickListener(item11 -> {
                                        try {
                                            Sandglass.start();
                                        } catch (Exception e) {
                                            Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
                                        }
                                        switch (item11.getItemId()) {
                                            case R.id.sleep:
                                                id = Acts.SLEEP;
                                                break;
                                            case R.id.eat:
                                                id = Acts.EAT;
                                                break;
                                            case R.id.clean:
                                                id = Acts.CLEAN;
                                                break;
                                        }
                                        prepare();
                                        return false;
                                    });
                                    popupMenu2.show();
                                    break;
                                case R.id.act:
                                    //in act
                                    PopupMenu popupMenu3 = new PopupMenu(context, view);
                                    popupMenu3.getMenuInflater().inflate(R.menu.menua, popupMenu3.getMenu());
                                    popupMenu3.setOnMenuItemClickListener(item112 -> {
                                        try {
                                            Sandglass.start();
                                        } catch (Exception e) {
                                            Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
                                        }
                                        switch (item112.getItemId()) {
                                            case R.id.study:
                                                id = Acts.STUDY;
                                                break;
                                            case R.id.fun:
                                                id = Acts.FUN;
                                                break;
                                            case R.id.other:
                                                id = Acts.OTHER;
                                                break;
                                        }
                                        prepare();
                                        return false;
                                    });
                                    popupMenu3.show();
                                    break;
                            }
                            return false;
                        });
                        popupMenu1.show();
                        break;
                    case R.id.stat:
                        stat();
                        break;
                }
                return false;
            });
        }
        popupMenu.show();
    }

    private void stat() {
        File file = new File(Pen.fileDir, Recorder.getYear() + ".xls");
        if (!file.exists()) {
            Snackbar.make(findViewById(R.id.bg), "暂无统计信息", Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(), "com.narukara.lunadial.fileProvider", file));
        intent.setType("application/vnd.ms-excel");
        startActivity(intent);
    }

    private void prepare() {
        try {
            Pen.write(Pen.cache, "id", String.valueOf(id));
        } catch (IOException e) {
            Snackbar.make(findViewById(R.id.bg), Tools.notNullMessage(e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
        refresh();
    }

    private void refresh() {
        timer = new Timer();
        final TextView textView = ((TextView) findViewById(R.id.textView));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                textView.post(() -> {
                    int[] duration = Sandglass.getDuration();
                    String string = Acts.getActName(id) + "\r\n" + duration[0] + "h " + duration[1] + "min";
                    textView.setText(string);
                });
            }
        }, 0, 10 * 1000);
    }
}
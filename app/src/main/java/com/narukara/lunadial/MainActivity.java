package com.narukara.lunadial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
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
        penInit();
        Sandglass.reload();
        reloadID();
        setTimer();
    }

    private void reloadID() {
        String string = Pen.read(Pen.cache, "id");
        if (string == null) {
            id = -1;
        } else {
            id = Integer.parseInt(string);
        }
    }

    private void penInit() {
        Pen.cache = new File(getCacheDir(), "cache");
        Pen.fileDir = getFilesDir();
        try {
            if (!Pen.cache.exists()) {
                Pen.cache.createNewFile();
            }
        } catch (IOException e) {
            Snackbar.make(findViewById(R.id.bg), "初始化缓存失败！", Snackbar.LENGTH_LONG);
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
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.end:
                            if (timer != null) {
                                timer.cancel();
                            }
                            ((TextView) findViewById(R.id.textView)).setText(getString(R.string.app_name));
                            long time = Sandglass.end();
                            Recorder.commit(id, time);
                            id = -1;
                            Pen.write(Pen.cache, "id", "-1");
                            break;
                        case R.id.stat:
                            Snackbar.make(findViewById(R.id.bg), "开发中", Snackbar.LENGTH_LONG);
                            stat();
                            break;
                    }
                    return false;
                }
            });
        } else {
            //start or stat
            popupMenu.getMenuInflater().inflate(R.menu.startmenu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.start:
                            //stable or act
                            PopupMenu popupMenu1 = new PopupMenu(context, view);
                            popupMenu1.getMenuInflater().inflate(R.menu.menu1, popupMenu1.getMenu());
                            popupMenu1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.stable:
                                            //in stable
                                            PopupMenu popupMenu2 = new PopupMenu(context, view);
                                            popupMenu2.getMenuInflater().inflate(R.menu.menus, popupMenu2.getMenu());
                                            popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem item) {
                                                    Sandglass.start();
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
                                                    }
                                                    prepare();
                                                    return false;
                                                }
                                            });
                                            popupMenu2.show();
                                            break;
                                        case R.id.act:
                                            //in act
                                            PopupMenu popupMenu3 = new PopupMenu(context, view);
                                            popupMenu3.getMenuInflater().inflate(R.menu.menua, popupMenu3.getMenu());
                                            popupMenu3.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem item) {
                                                    Sandglass.start();
                                                    switch (item.getItemId()) {
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
                                                }
                                            });
                                            popupMenu3.show();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu1.show();
                            break;
                        case R.id.stat:
                            Snackbar.make(findViewById(R.id.bg), "开发中", Snackbar.LENGTH_LONG);
                            stat();
                            break;
                    }
                    return false;
                }
            });
        }
        popupMenu.show();
    }

    private void stat() {

    }

    private void prepare() {
        Pen.write(Pen.cache, "id", String.valueOf(id));
        refresh();
    }

    private void refresh() {
        timer = new Timer();
        final TextView textView = ((TextView) findViewById(R.id.textView));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        int[] duration = Sandglass.getDuration();
                        String string = Acts.getActName(id) + "\r\n" + duration[0] + "h " + duration[1] + "min";
                        textView.setText(string);
                    }
                });
            }
        }, 0, 10 * 1000);
    }
}
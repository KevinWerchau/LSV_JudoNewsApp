package com.nachrichten.lsv_judomvvm.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nachrichten.lsv_judomvvm.models.NotifyUtility;
import com.nachrichten.lsv_judomvvm.repositories.NewsRepository;
import com.nachrichten.lsv_judomvvm.repositories.TerminRepository;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class AlarmReceiver extends BroadcastReceiver {

    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    private TerminRepository terminRepository = TerminRepository.getInstance();
    private NewsRepository newsRepository = NewsRepository.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        Paper.init(context.getApplicationContext());
        NotifyUtility.getInstance().setContext(context);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                terminRepository.getTermineFromDatabase();
            }
        },0,1000);
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {

                newsRepository.getNewsFromDatabase();
            }
        },0,1000);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
                timer2.cancel();
            }
        });
        t.start();
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60*1000+30000, alarmIntent);
    }
}

package com.nachrichten.lsv_judomvvm.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.nachrichten.lsv_judomvvm.R;
import com.nachrichten.lsv_judomvvm.repositories.UIStateRepository;
import com.nachrichten.lsv_judomvvm.views.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotifyUtility {
    private static NotifyUtility instance;
    private Context context;
    public static CharSequence terminaddName = "LSV_TerminAdd";
    public static CharSequence termindelName = "LSV_TerminDel";
    public static CharSequence newsaddName = "LSV_NewsAdd";
    public static CharSequence newsdelName = "LSV_NewsDel";
    private SimpleDateFormat DatumsFormatierung = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
    private PendingIntent pendingIntent;

    private NotifyUtility(){}
    public static NotifyUtility getInstance(){
        if(instance == null){
            instance = new NotifyUtility();
        }
        return instance;
    }

    public void setContext(Context pContext){
        context = pContext;
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void NotificationTerminAdded(Termin pTermin,int size){
        UIStateRepository.getInstance().setBottomNavItemId(R.id.Termin);
        UIStateRepository.getInstance().setDate(new Date(pTermin.getDatum()));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = terminaddName;
        String description = "Termin Empfangen";
        String CHANNEL_ID = (String) terminaddName;
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        Date TerminDate = new Date(pTermin.getDatum());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);


                notificationManager.createNotificationChannel(channel);



                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Neuer Termin:")
                        .setContentText(pTermin.getEventName() + " am " + DatumsFormatierung.format(TerminDate))
                        .setSmallIcon(R.drawable.ic_action_termin)
                        .setContentIntent(pendingIntent)
                        .setColor(Color.RED)
                        .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(size+110,notification);
        }else{
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Neuer Termin:")
                    .setContentText(pTermin.getEventName() + " am " + DatumsFormatierung.format(TerminDate))
                    .setSmallIcon(R.drawable.ic_action_termin)
                    .setContentIntent(pendingIntent)
                    .setColor(Color.RED)
                    .setPriority(1)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(size+110,notification);
        }

    }

    public void NotificationTerminDeleted(Termin pTermin, int size){
        UIStateRepository.getInstance().setBottomNavItemId(R.id.Termin);
        UIStateRepository.getInstance().setDate(new Date(pTermin.getDatum()));
        CharSequence name = termindelName;
        String description = "Terming Gelöscht";
        String CHANNEL_ID = (String) termindelName;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Der Termin:")
                    .setContentText(pTermin.getEventName()
                            +" am "
                            +DatumsFormatierung.format(new Date(pTermin.getDatum()))
                            +" wurde gelöscht")

                    .setSmallIcon(R.drawable.ic_action_termin)
                    .setColor(Color.RED)
                    .setContentIntent(pendingIntent)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(size+220,notification);
        }else{
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Der Termin:")
                    .setContentText(pTermin.getEventName()
                            +" am "
                            +DatumsFormatierung.format(new Date(pTermin.getDatum()))
                            +" wurde gelöscht")

                    .setSmallIcon(R.drawable.ic_action_termin)
                    .setColor(Color.RED)
                    .setContentIntent(pendingIntent)
                    .setPriority(1)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(size+220,notification);
        }
    }
    public void NotificationNewsAdded(News pNews, int size){
        UIStateRepository.getInstance().setBottomNavItemId(R.id.News);
        CharSequence name = newsaddName;
        String description = "News Empfangen";
        String CHANNEL_ID = (String) newsaddName;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Neue News:")
                    .setContentText(pNews.getNews_Name())
                    .setSmallIcon(R.drawable.ic_action_news)
                    .setContentIntent(pendingIntent)
                    .setColor(Color.RED)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(size+330,notification);
        }else{
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Neue News:")
                    .setContentText(pNews.getNews_Name())
                    .setSmallIcon(R.drawable.ic_action_news)
                    .setContentIntent(pendingIntent)
                    .setColor(Color.RED)
                    .setPriority(1)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(size+330,notification);
        }
    }
    public void NotificationNewsDeleted(News pNews, int size){
        UIStateRepository.getInstance().setBottomNavItemId(R.id.News);
        CharSequence name = newsdelName;
        String description = "News Gelöscht";
        String CHANNEL_ID = (String) newsdelName;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);


            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Die News:")
                    .setContentText(pNews.getNews_Name()+" wurde gelöscht")
                    .setSmallIcon(R.drawable.ic_action_news)
                    .setContentIntent(pendingIntent)
                    .setColor(Color.RED)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(size+440,notification);
        }else{
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Die News:")
                    .setContentText(pNews.getNews_Name()+" wurde gelöscht")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_action_news)
                    .setColor(Color.RED)
                    .setPriority(1)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(size+440,notification);
        }
    }

}

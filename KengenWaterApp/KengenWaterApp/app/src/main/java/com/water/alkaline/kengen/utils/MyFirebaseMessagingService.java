package com.water.alkaline.kengen.utils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.ui.activity.FeedbackActivity;
import com.water.alkaline.kengen.ui.activity.SplashActivity;
import com.water.alkaline.kengen.utils.Constant;
import com.preference.PowerPreference;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    public static String CHANNEL_ID = "kanGen";
    public static String id = "hi";
    String title = "", text = "", url = "", icon = "";
    int nottype = 0;
    boolean checktitle, checktext, checkurl, checkicon;
    Context context;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        PowerPreference.getDefaultFile().putString(Constant.Token, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            context = this;
            title = remoteMessage.getData().get("title");
            text = remoteMessage.getData().get("text");
            url = remoteMessage.getData().get("url");
            icon = remoteMessage.getData().get("icon");

            checktitle = !title.equalsIgnoreCase("");
            checktext = !text.equalsIgnoreCase("");
            checkurl = !url.equalsIgnoreCase("");
            checkicon = !icon.equalsIgnoreCase("");

            if (remoteMessage.getData().containsKey("reply")) {
                PendingIntent contentIntent;

                if (SplashActivity.activity == null) {
                    Intent intent;
                    intent = new Intent(this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                } else {
                    Intent intent;
                    intent = new Intent(this, FeedbackActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                }

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setSound(defaultSoundUri)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true).
                        setContentIntent(contentIntent);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(channel);
                }
                manager.notify(0, builder.build());
            } else if (!checkurl && !checkicon) {

                PendingIntent contentIntent;

                if (SplashActivity.activity == null) {

                    Intent intent;
                    intent = new Intent(this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                } else {

                    contentIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            new Intent(), // add this
                            PendingIntent.FLAG_ONE_SHOT);
                }

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setSound(defaultSoundUri)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true).
                        setContentIntent(contentIntent);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(channel);
                }
                manager.notify(0, builder.build());
            } else if (checkicon) {
                startnotify();
            } else {
                Intent rIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                PendingIntent contentIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        rIntent,
                        PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setSound(defaultSoundUri).
                        setContentTitle(title)
                        .setContentText(text).setAutoCancel(true).setContentIntent(contentIntent);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(channel);
                }
                manager.notify(0, builder.build());
            }

        } catch (Exception e) {
            Constant.showLog(e.toString());
        }
    }

    public void startnotify() {
        new sendNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressLint("StaticFieldLeak")
    public class sendNotification extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                Bitmap bitmap;
                URL urll = new URL(icon);
                HttpURLConnection connection = (HttpURLConnection) urll.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Constant.showLog(e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Constant.showLog(e.toString());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);

            if (result != null) {

                NotificationCompat.Builder builder;
                PendingIntent contentIntent;

                Intent rIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                if (!checkurl && checkicon) {

                    if (SplashActivity.activity == null) {
                        Intent intent;
                        intent = new Intent(context, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    } else {
                        contentIntent = PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                new Intent(),
                                PendingIntent.FLAG_ONE_SHOT);
                    }


                    builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setLargeIcon(result)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
                            .setAutoCancel(true).setContentIntent(contentIntent);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
                        manager.createNotificationChannel(channel);
                    }
                    manager.notify(1, builder.build());

                } else if (checkurl && checkicon) {

                    contentIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            rIntent,
                            PendingIntent.FLAG_ONE_SHOT);

                    builder = new NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setLargeIcon(result)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
                            .setContentIntent(contentIntent);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
                        if (manager != null)
                            manager.createNotificationChannel(channel);
                    }
                    if (manager != null)
                        manager.notify(1, builder.build());


                }
            } else {
                Constant.showLog("Bitmap Null");
            }
        }
    }

}
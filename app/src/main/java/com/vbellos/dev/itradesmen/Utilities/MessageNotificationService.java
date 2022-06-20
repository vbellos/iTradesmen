package com.vbellos.dev.itradesmen.Utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vbellos.dev.itradesmen.Dao.ChatDao;
import com.vbellos.dev.itradesmen.Models.Message;
import com.vbellos.dev.itradesmen.R;
import com.vbellos.dev.itradesmen.User.ChatActivity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageNotificationService extends Worker {

    Context context;

    public MessageNotificationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String user_id = getInputData().getString("user_id");
        CreateNotifications(user_id);
        return null;
    }


    public static void NotificationsPeriodicRequest(Boolean enable, @Nullable String id)
    {
        if(enable) {

            Data inputData = new Data.Builder()
                    .putString("user_id", id)
                    .build();
            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                    .Builder(MessageNotificationService.class, 15, TimeUnit.MINUTES)
                    .addTag("periodic")

                    .setConstraints(setCons())
                    .setInputData(inputData).build();

            WorkManager.getInstance().enqueueUniquePeriodicWork("periodic", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
        }
        else{
            WorkManager.getInstance().cancelAllWorkByTag("periodic");
        }

    }

    public static Constraints setCons()
    {
        Constraints constraints = new Constraints.Builder().build();
        return constraints;
    }

    public void CreateNotifications(String user_id)
    {

        boolean notifications;
        NotificationManagerCompat notification_manager;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        notifications = prefs.getBoolean("notifications", false);
        notification_manager = NotificationManagerCompat.from(context);

        TinyDB tinyDB = new TinyDB(context);
        String search_key= "user_notifications_"+ user_id;



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("notifications","i-Tradesmen", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        ChatDao.UnreadMessagesRef unreadMessagesRef = new ChatDao.UnreadMessagesRef(user_id);
        unreadMessagesRef.setUnreadMessageListener(new ChatDao.UnreadMessagesRef.UnreadMessageListener() {
            @Override
            public void UnreadMessageAdded(Message message) {
                ArrayList<String> displayed_notifications = tinyDB.getListString(search_key);
                if(displayed_notifications == null){displayed_notifications = new ArrayList<String>();}

                if(notifications && !displayed_notifications.contains(message.getMessage_id())) {
                    Intent i = new Intent(context, ChatActivity.class);
                    i.putExtra("user_id",message.getSender());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notifications");
                    builder.setContentTitle("New message");
                    builder.setContentText(message.getText());
                    builder.setContentIntent(pendingIntent);
                    builder.setSmallIcon(R.drawable.ic_email);
                    builder.setAutoCancel(true);
                    notification_manager.notify(generateUniqueId(),builder.build());

                    displayed_notifications.add(message.getMessage_id());
                    tinyDB.putListString(search_key,displayed_notifications);

                }
            }
        });

    }
    public static int generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }
}

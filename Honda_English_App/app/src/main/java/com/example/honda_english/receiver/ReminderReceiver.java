package com.example.honda_english.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.honda_english.activity.HomeActivity;
import com.example.honda_english.util.PrefUtils;
import com.example.honda_english.util.enums.RepeatType;

import java.util.Objects;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "reminder_channel";
    private static final int REQUEST_CODE = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        PrefUtils prefUtils = new PrefUtils(context);
//        if (!prefUtils.isLoggedIn()) {
//            Intent loginIntent = new Intent(context, MainActivity.class);
//            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            loginIntent.putExtra("show_login_message", true);
//            context.startActivity(loginIntent);
//            return;
//        }
        showNotification(context);
        rescheduleAlarm(context, intent, prefUtils);
    }

    private void showNotification(Context context) {
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Nhắc nhở học tập")
                .setContentText("Đã đến giờ học từ vựng của bạn!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void rescheduleAlarm(Context context, Intent intent, PrefUtils prefUtils) {
        if (!prefUtils.isLoggedIn()) {
            return;
        }
        RepeatType repeatType = RepeatType.valueOf(Objects.requireNonNull(intent.getStringExtra("repeat_type")).toUpperCase());
        String repeatInterval = intent.getStringExtra("repeat_interval");
        if (repeatType == RepeatType.DAILY || repeatType == RepeatType.INTERVAL) {
            long intervalMillis = calculateIntervalMillis(repeatType, repeatInterval);
            if (intervalMillis > 0) {
                scheduleNextAlarm(context, intent, intervalMillis);
            }
        }
    }

    private long calculateIntervalMillis(RepeatType repeatType, String repeatInterval) {
        long intervalMillis = 0;
        if (repeatType == RepeatType.DAILY) {
            intervalMillis = 24 * 60 * 60 * 1000;
        } else if (repeatType == RepeatType.INTERVAL && repeatInterval != null) {
            try {
                String[] intervalParts = repeatInterval.split(":");
                int hours = Integer.parseInt(intervalParts[0]);
                int minutes = Integer.parseInt(intervalParts[1]);
                int seconds = Integer.parseInt(intervalParts[2]);
                intervalMillis = (hours * 3600L + minutes * 60L + seconds) * 1000;
            } catch (Exception e) {
                Log.e("ReminderReceiver", "Error parsing repeat interval: " + e.getMessage());
            }
        }
        return intervalMillis;
    }

    private void scheduleNextAlarm(Context context, Intent intent, long intervalMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e("ReminderReceiver", "AlarmManager is null");
            return;
        }
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        long nextTriggerTime = System.currentTimeMillis() + intervalMillis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerTime,
                        nextPendingIntent
                );
                Log.d("ReminderReceiver", "Next alarm scheduled for: " + nextTriggerTime);
            } else {
                Log.w("ReminderReceiver", "Cannot schedule exact alarms, requesting permission");
                try {
                    Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(settingsIntent);
                } catch (Exception e) {
                    Log.e("ReminderReceiver", "Error opening alarm settings: " + e.getMessage());
                }
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTriggerTime,
                    nextPendingIntent
            );
            Log.d("ReminderReceiver", "Next alarm scheduled for: " + nextTriggerTime);
        }
    }
}
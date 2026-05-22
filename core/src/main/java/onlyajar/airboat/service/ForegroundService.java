package onlyajar.airboat.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.util.LinkedHashMap;
import java.util.Map;

import onlyajar.airboat.utils.AppUtils;

public abstract class ForegroundService extends Service {

    public abstract void onServiceStartup();

    public abstract void onServiceShut();

    private class LocalBinder extends Binder {
        public Service getService() {
            return ForegroundService.this;
        }
    }

    private static class ActionInfo {
        public ActionInfo(Intent intent, int icon, String title, Runnable invoke) {
            this.intent = intent;
            this.icon = icon;
            this.title = title;
            this.invoke = invoke;
        }

        Intent intent;
        int icon;
        String title;
        Runnable invoke;
    }

    private final Binder binder = new LocalBinder();

    private final Map<String, ActionInfo> actionInfos = new LinkedHashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected int getNotificationIcon() {
        return AppUtils.getApIcon();
    }

    protected String getNotificationName() {
        return AppUtils.getAppName();
    }

    protected int getNotificationID() {
        return 99;
    }

    protected String getNotificationTitle() {
        return getNotificationName();
    }

    protected String getNotificationContext() {
        return getNotificationName() + " Running";
    }


    @Override
    public void onCreate() {
        super.onCreate();
        runStartForeground();
        onServiceStartup();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            executeAction(action);
        } else {
            runStartForeground();
        }
        return START_NOT_STICKY;
    }

    private void executeAction(String action) {
        ActionInfo actionInfo = actionInfos.get(action);
        if (actionInfo != null) {
            actionInfo.invoke.run();
        }
    }

    protected void addAction(String action, int icon, String title, Runnable invoke) {
        Intent intent = new Intent(this, this.getClass()).setAction(action);
        actionInfos.put(action, new ActionInfo(intent, icon, title, invoke));
    }

    protected void runStartForeground() {
        try {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getNotificationName())
                    .setContentTitle(getNotificationTitle())
                    .setContentText(getNotificationContext())
                    .setSmallIcon(getNotificationIcon())
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOngoing(true);
            for (Map.Entry<String, ActionInfo> entry : actionInfos.entrySet()) {
                notification.addAction(entry.getValue().icon, entry.getValue().title,
                        PendingIntent.getService(ForegroundService.this, 0, entry.getValue().intent,
                                PendingIntent.FLAG_MUTABLE));

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = "channel_" + getNotificationName();
                String channelDescription = "channel_desc_" + channelName;
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(getNotificationName(), channelName, importance);
                channel.setDescription(channelDescription);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
            startForeground(getNotificationID(), notification.build());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onServiceShut();
    }


    public static void startForegroundService(Class<? extends ForegroundService> serviceClass){
        Context context = AppUtils.getApplication();
        Intent serviceIntent = new Intent(context, serviceClass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);  // Android 8.0+
//            context.bindService(
//                    serviceIntent,
//                    new ServiceConnection() {
//                        @Override
//                        public void onServiceConnected(ComponentName name, IBinder service) {
//
//                        }
//
//                        @Override
//                        public void onServiceDisconnected(ComponentName name) {
//
//                        }
//                    },
//                    Context.BIND_AUTO_CREATE
//            );
        } else {
            context.startService(serviceIntent);
        }
    }

}

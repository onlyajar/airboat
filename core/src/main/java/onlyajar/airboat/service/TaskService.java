package onlyajar.airboat.service;

import android.util.Log;

public class TaskService extends ForegroundService{

    private static final String TAG = "TaskService";

    @Override
    protected String getNotificationName() {
        return super.getNotificationName() + " task";
    }

    @Override
    public void onServiceStartup() {
        Log.d(TAG, "onServiceStartup: ");
    }

    @Override
    public void onServiceShut() {
        Log.d(TAG, "onServiceShut: ");
    }

    public static void start(){
        ForegroundService.startForegroundService(TaskService.class);
    }
}

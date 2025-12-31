package onlyajar.airboat.utils;

import android.annotation.SuppressLint;
import android.app.AppGlobals;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public final class AppUtils {
    private AppUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T extends Application> T getApplication() {
        return (T) AppGlobals.getInitialApplication();
    }

    @SuppressLint("PrivateApi")
    public static String getAppName() {
        try {
            Context context = getApplication();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getInitialPackage() {
        return AppGlobals.getInitialPackage();
    }

    public static int getVersionCode() {
        try {
            Context context = getApplication();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName() {
        try {
            Context context = getApplication();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getIntCoreSetting(String key, int defaultValue) {
        return AppGlobals.getIntCoreSetting(key, defaultValue);
    }
}

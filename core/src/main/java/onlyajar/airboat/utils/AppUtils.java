package onlyajar.airboat.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import onlyajar.airboat.runtime.AppRuntime;

public final class AppUtils {
    private AppUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T extends Application> T getApplication() {
        return (T) AppRuntime.getApplication();
    }

    public static String getAppName() {
        try {
            Context context = AppRuntime.getApplication();
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getApIcon() {
        try {
            Context context = AppRuntime.getApplication();
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.icon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return android.R.mipmap.sym_def_app_icon;
    }

    public static int getVersionCode() {
        try {
            Context context = AppRuntime.getApplication();
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
            Context context = AppRuntime.getApplication();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

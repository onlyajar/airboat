package onlyajar.airboat.runtime;

import android.app.Application;

import onlyajar.startup.InitializationProvider;

public class AppRuntime {

    public static <T extends Application> T getApplication() {
        return (T) InitializationProvider.getApplication();
    }
}

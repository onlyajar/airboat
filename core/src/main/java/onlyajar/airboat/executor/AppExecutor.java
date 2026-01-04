package onlyajar.airboat.executor;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class AppExecutor {
    private final Object lock = new Object();
    private final Executor executor;
    private final Handler mainHandler;

    private AppExecutor() {
        executor = Executors.newCachedThreadPool();
        mainHandler = createAsync(Looper.getMainLooper());
    }

    private static class SingletonHolder {
        private static final AppExecutor INSTANCE = new AppExecutor();
    }

    public static AppExecutor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void executeOnIO(Runnable runnable) {
        executor.execute(runnable);
    }

    private void executeOnMain(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMain(runnable);
        }
    }

    private void postToMain(Runnable runnable) {
        mainHandler.post(runnable);
    }

    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static Handler createAsync(Looper looper) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Handler.createAsync(looper);
        } else {
            try {
                // This constructor was added as private in JB MR1:
                return Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class, boolean.class)
                        .newInstance(looper, null, true);
            } catch (Exception ignored) {
                return new Handler(looper);
            }
        }
    }

}


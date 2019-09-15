package com.mitash.fragmentnavcontroller;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.concurrent.Executor;

/**
 * Created by Mitash Gaurh on 9/4/2018.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class NavigationExecutors {

    private static NavigationExecutors sNavigationExecutors;

    private final Executor mMainThread;

    public static NavigationExecutors getInstance() {
        if (null == sNavigationExecutors) {
            sNavigationExecutors = new NavigationExecutors();
        }
        return sNavigationExecutors;
    }

    private NavigationExecutors(Executor mainThread) {
        this.mMainThread = mainThread;
    }

    private NavigationExecutors() {
        this(new MainThreadExecutor());
    }

    public Executor mainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

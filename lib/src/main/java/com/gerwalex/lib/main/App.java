package com.gerwalex.lib.main;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import androidx.lifecycle.MutableLiveData;

import com.gerwalex.lib.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class App extends Application {

    public static final String ABOUTHTML = "no_about.html";
    public static final String COPYRIGHT = "no_copyright.html";
    public static final String linefeed = System.getProperty("line.separator");
    public static final MutableLiveData<Boolean> noAds = new MutableLiveData<>(false);
    private static final String backupDirName = "backup";
    private static final String downloadDirName = "download";
    private static final String importDirName = "import";
    public static boolean isTestDevice;
    public static TaskRunner taskRunner = new TaskRunner();

    public static File getAppBackupDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        File backupDir = new File(fileDir, backupDirName);
        backupDir.mkdirs();
        return backupDir;
    }

    public static File getAppDownloadDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        File downloadDir = new File(fileDir, downloadDirName);
        downloadDir.mkdirs();
        return downloadDir;
    }

    public static File getAppImportDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        File importDir = new File(fileDir, importDirName);
        importDir.mkdirs();
        return importDir;
    }

    /**
     * Submitted ein Runnable
     *
     * @param runInBackground das Runnable
     */
    public static void run(Runnable runInBackground) {
        taskRunner.execute(runInBackground);
    }

    /**
     * Submitted ein Callable
     *
     * @param runInBackground das Callable
     * @return ein future
     */
    public static <T> Future<T> run(Callable<T> runInBackground) {
        return taskRunner.submit(runInBackground);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.StrictMode) {
            //            StrictMode.enableDefaults();
            StrictMode.VmPolicy.Builder vmPolicy = new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedRegistrationObjects()
                    .detectActivityLeaks();
            vmPolicy.detectLeakedClosableObjects();
            vmPolicy.detectFileUriExposure();
            vmPolicy.detectLeakedRegistrationObjects();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vmPolicy.detectContentUriWithoutPermission();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                vmPolicy.detectNonSdkApiUsage();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vmPolicy.detectCredentialProtectedWhileLocked();
                vmPolicy.detectImplicitDirectBoot();
                vmPolicy.detectUntaggedSockets();
                vmPolicy.detectCredentialProtectedWhileLocked();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                vmPolicy.detectIncorrectContextUse();
                vmPolicy.detectUnsafeIntentLaunch();
            }
            StrictMode.setVmPolicy(vmPolicy
                    .penaltyLog()
                    .build());
            final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
                paramThrowable.printStackTrace();
                try (PrintWriter pw = new PrintWriter(new File(getFilesDir(), "stacktrace.txt"))) {
                    paramThrowable.printStackTrace(pw);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                if (oldHandler != null) {
                    oldHandler.uncaughtException(paramThread, paramThrowable); //Delegates to Android's error handling
                } else {
                    System.exit(2); //Prevents the service/app from freezing
                }
            });
        }
    }
}
package com.gerwalex.lib.kotlin

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.lifecycle.MutableLiveData
import com.gerwalex.lib.BuildConfig
import com.gerwalex.lib.main.TaskRunner
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.concurrent.Callable
import java.util.concurrent.Future
import kotlin.system.exitProcess

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appResources = resources
        if (BuildConfig.StrictMode) {
            //            StrictMode.enableDefaults();
            val vmPolicy = VmPolicy
                .Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedRegistrationObjects()
                .detectActivityLeaks()
            vmPolicy.detectLeakedClosableObjects()
            vmPolicy.detectFileUriExposure()
            vmPolicy.detectLeakedRegistrationObjects()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vmPolicy.detectContentUriWithoutPermission()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                vmPolicy.detectNonSdkApiUsage()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vmPolicy.detectCredentialProtectedWhileLocked()
                vmPolicy.detectImplicitDirectBoot()
                vmPolicy.detectUntaggedSockets()
                vmPolicy.detectCredentialProtectedWhileLocked()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                vmPolicy.detectIncorrectContextUse()
                vmPolicy.detectUnsafeIntentLaunch()
            }
            StrictMode.setVmPolicy(
                vmPolicy
                    .penaltyLog()
                    .build()
            )
            val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { paramThread: Thread, paramThrowable: Throwable ->
                paramThrowable.printStackTrace()
                try {
                    PrintWriter(File(filesDir, "stacktrace.txt"))
                        .use { pw ->
                            paramThrowable.printStackTrace(pw)
                        }
                } catch (ex: FileNotFoundException) {
                    ex.printStackTrace()
                }
                if (oldHandler != null) {
                    oldHandler.uncaughtException(paramThread, paramThrowable) //Delegates to Android's error handling
                } else {
                    exitProcess(2) //Prevents the service/app from freezing
                }
            }
        }
    }

    companion object {

        const val ABOUTHTML = "no_about.html"
        const val COPYRIGHT = "no_copyright.html"
        val linefeed: String = System.getProperty("line.separator") as String
        val noAds = MutableLiveData(false)
        private const val backupDirName = "backup"
        private const val downloadDirName = "download"
        private const val importDirName = "import"
        var isTestDevice = false
        private val taskRunner = TaskRunner()
        var appResources: Resources? = null
            private set

        fun getAppBackupDir(context: Context): File {
            val fileDir = context.getExternalFilesDir(null)
            val backupDir = File(fileDir, backupDirName)
            backupDir.mkdirs()
            return backupDir
        }

        fun getAppDownloadDir(context: Context): File {
            val fileDir = context.getExternalFilesDir(null)
            val downloadDir = File(fileDir, downloadDirName)
            downloadDir.mkdirs()
            return downloadDir
        }

        fun getAppImportDir(context: Context): File {
            val fileDir = context.getExternalFilesDir(null)
            val importDir = File(fileDir, importDirName)
            importDir.mkdirs()
            return importDir
        }

        /**
         * Submitted ein Runnable
         *
         * @param runInBackground das Runnable
         */
        fun run(runInBackground: Runnable?) {
            taskRunner.execute(runInBackground)
        }

        /**
         * Submitted ein Callable
         *
         * @param runInBackground das Callable
         * @return ein future
         */
        fun <T> run(runInBackground: Callable<T>): Future<T> {
            return taskRunner.submit(runInBackground)
        }
    }
}
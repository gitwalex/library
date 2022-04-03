package com.gerwalex.lib.main;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.room.Ignore;

import com.gerwalex.lib.BuildConfig;
import com.gerwalex.lib.database.ObservableTableRow;

import java.util.concurrent.TimeUnit;

public abstract class Timer extends ObservableTableRow {
    @Ignore
    private static final long delay;
    @Ignore
    private static final Handler handler;

    static {
        delay = BuildConfig.DEBUG ? TimeUnit.SECONDS.toMillis(5) : TimeUnit.MINUTES.toMillis(1);
        handler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    public final String timerkey;
    // @formatter:off
    public long endTime;
    // @formatter:on
    @Ignore
    private boolean alreadyCounting;
    @Ignore
    private CountdownListener countDownListener;

    @Ignore
    public Timer(Cursor c) {
        super(c);
        this.endTime = getAsLong("endTime");
        this.timerkey = getAsString("key");
    }

    public Timer(long endTime, @NonNull String timerkey) {
        this.endTime = endTime;
        this.timerkey = timerkey;
    }

    public Timer(@NonNull String key, int timeInMinutes) {
        this.timerkey = key;
        endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(timeInMinutes);
    }

    public boolean isExpired() {
        return endTime < System.currentTimeMillis();
    }

    @UiThread
    private void runCountDown() {
        long time = System.currentTimeMillis();
        if (time < endTime) {
            countDownListener.onCountdownUpdated(endTime - time);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runCountDown();
                }
            }, delay);
        } else {
            alreadyCounting = false;
            countDownListener.onCountDownFinished();
        }
    }

    @AnyThread
    public void startCountdown(@NonNull CountdownListener listener) {
        if (alreadyCounting && countDownListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    countDownListener.onCountdownInterrupted();
                }
            });
        }
        this.countDownListener = listener;
        alreadyCounting = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                runCountDown();
            }
        });
    }

    public interface CountdownListener {
        /**
         * Aufruf, wenn das Ende des Countdown erreicht wurde.
         * Default: Logging
         */
        @UiThread
        default void onCountDownFinished() {
            Log.d("gerwalex", "Countdown interrupted. ");
        }

        /**
         * Aufruf, wenn der Countdown durch einen weiteren gestarteten Countdown unterbrochen wurde
         * Default: Logging
         */
        @UiThread
        default void onCountdownInterrupted() {
            Log.d("gerwalex", "Countdown stopped. ");
        }

        /**
         * Wird menuetlich aufgerufen
         *
         * @param time Anzahl Millis, bis Ende des Countdown erreicht ist..
         */
        @UiThread
        void onCountdownUpdated(long time);
    }
}

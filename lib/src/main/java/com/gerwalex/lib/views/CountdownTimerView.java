package com.gerwalex.lib.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.room.Ignore;

import com.gerwalex.lib.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CountdownTimerView extends AppCompatTextView {

    public long endTime;
    // @formatter:on
    @Ignore
    private boolean alreadyCounting;
    @Ignore
    private CountdownListener countDownListener;

    public CountdownTimerView(@NonNull Context context) {
        this(context, null);
    }

    public CountdownTimerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownTimerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isExpired() {
        return endTime < System.currentTimeMillis();
    }

    @UiThread
    private void runCountDown() {
        long time = System.currentTimeMillis();
        if (time < endTime) {
            int minutes = (int) ((endTime - time) / TimeUnit.MINUTES.toMillis(1));
            int h = minutes / 60;
            int m = minutes % 60;
            setText(getResources().getString(R.string.timerViewText, h, m));
            countDownListener.onCountdownUpdated(minutes);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    runCountDown();
                }
            }, TimeUnit.MINUTES.toMillis(1));
        } else {
            alreadyCounting = false;
            countDownListener.onCountDownFinished();
        }
    }

    public void startCountDown(long endTime, @NonNull CountdownListener listener) {
        this.endTime = endTime;
        if (alreadyCounting && countDownListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    countDownListener.onCountdownInterrupted();
                }
            });
        }
        this.countDownListener = listener;
        alreadyCounting = true;
        post(new Runnable() {
            @Override
            public void run() {
                runCountDown();
            }
        });
    }

    @AnyThread
    public void startCountdown(@NonNull Date endTime, @NonNull CountdownListener listener) {
        startCountDown(endTime.getTime(), listener);
    }

    @AnyThread
    public void startCountdown(int minutes, @NonNull CountdownListener listener) {
        startCountDown(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes), listener);
    }

    public interface CountdownListener {
        /**
         * Aufruf, wenn das Ende des Countdown erreicht wurde.
         */
        @UiThread
        void onCountDownFinished();

        /**
         * Aufruf, wenn der Countdown durch einen weiteren gestarteten Countdown unterbrochen wurde
         * Default: Logging
         */
        @UiThread
        default void onCountdownInterrupted() {
            Log.d("gerwalex", "Countdown interrupted. ");
        }

        /**
         * Wird menuetlich aufgerufen. Default: Log.
         *
         * @param minutesRemaining Anzahl Minuten, bis Ende des Countdown erreicht ist..
         */
        @UiThread
        default void onCountdownUpdated(int minutesRemaining) {
            Log.d("gerwalex", "Countdown running. Remaining: " + minutesRemaining);
        }
    }
}


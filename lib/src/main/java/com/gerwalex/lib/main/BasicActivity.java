package com.gerwalex.lib.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

/**
 * Basis-Activity  mit einem simplen Layout. Stellt ein Bundle args mit den extras aus Intent
 * bereit.
 */
public abstract class BasicActivity extends AppCompatActivity {
    public static final String ACTIONBARSUBTITLE = "ACTIONBARSUBTITLE";
    public static final String ACTIONBARTITLE = "ACTIONBARTITLE";
    /**
     * Bundle fuer Argumente. Wird in SaveStateInstance gesichert und in onCreate
     * wiederhergestellt.
     */
    protected final Bundle args = new Bundle();
    protected SharedPreferences prefs;
    private Handler handler;

    /**
     * Hides a Keyboard
     *
     * @see "stackoverflow.com/questions/1109022/close-hide-the-android-soft -keyboard"
     */
    @SuppressWarnings("unused")
    public void hide_keyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Bereitstellen der Argumente aus Intent bzw savedStateInstance in args. Ist in den Args ein
     * String unter "NEXTACTIVIT" vorhanden, wird diese Activity nachgestartet Setzen der View
     *
     * @param savedInstanceState SavedState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState != null) {
            args.putAll(savedInstanceState);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                args.putAll(extras);
            }
        }
    }

    /**
     * Setzt den Subtitle aus args neu
     */
    @Override
    protected void onResume() {
        super.onResume();
        setSubTitle(args.getString(ACTIONBARSUBTITLE));
    }

    /**
     * Sicherung aller Argumente
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putAll(args);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Startet ein Runnable verzoegert auf dem MainThread
     *
     * @param r           Runnable
     * @param delayMillis Verzoegerung in Millis
     */
    public void postOnUiThread(Runnable r, long delayMillis) {
        if (handler == null) {
            handler = new Handler(getMainLooper());
        }
        handler.postDelayed(r, delayMillis);
    }

    /**
     * Setzt den SubTitle in der SupportActionBar
     *
     * @param subTitleResID resID des Subtitles
     */
    public final void setSubTitle(@StringRes int subTitleResID) {
        setSubTitle(getString(subTitleResID));
    }

    /**
     * Setzt den SubTitle in der SupportActionBar, wenn vorhanden
     *
     * @param subTitle Text des Subtitles
     */
    public final void setSubTitle(CharSequence subTitle) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setSubtitle(subTitle);
        }
        args.putCharSequence(ACTIONBARSUBTITLE, subTitle);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
    }
}
